/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

package io.ktor.server.netty.cio

import io.ktor.utils.io.*
import io.netty.buffer.*
import io.netty.channel.*
import io.netty.handler.codec.http.*
import io.netty.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.lang.Integer.*
import kotlin.coroutines.*

internal class RequestBodyHandler(
    val context: ChannelHandlerContext
) : ChannelInboundHandlerAdapter(), CoroutineScope {
    private val handlerJob = CompletableDeferred<Nothing>()

    private val queue = Channel<Any>(Channel.UNLIMITED)

    private object Upgrade

    override val coroutineContext: CoroutineContext get() = handlerJob

    private val job = launch(context.executor().asCoroutineDispatcher(), start = CoroutineStart.LAZY) {
        var current: ByteWriteChannel? = null
        var upgraded = false

        try {
            while (true) {
                val event = queue.tryReceive().getOrNull()
                    ?: run { current?.flush(); queue.receiveCatching().getOrNull() }
                    ?: break

                when (event) {
                    is ByteBufHolder -> {
                        val channel = current ?: error("No current channel but received a byte buf")
                        processContent(channel, event)

                        if (!upgraded && event is LastHttpContent) {
                            current.close()
                            current = null
                        }
                        requestMoreEvents()
                    }
                    is ByteBuf -> {
                        val channel = current ?: error("No current channel but received a byte buf")
                        processContent(channel, event)
                        requestMoreEvents()
                    }
                    is ByteWriteChannel -> {
                        current?.close()
                        current = event
                    }
                    is Upgrade -> {
                        upgraded = true
                    }
                }
            }
        } catch (t: Throwable) {
            queue.close(t)
            current?.close(t)
        } finally {
            current?.close()
            queue.close()
            consumeAndReleaseQueue()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun upgrade(): ByteReadChannel {
        val result = queue.trySend(Upgrade)
        if (result.isSuccess) return newChannel()

        if (queue.isClosedForSend) {
            throw CancellationException("HTTP pipeline has been terminated.", result.exceptionOrNull())
        }
        throw IllegalStateException(
            "Unable to start request processing: failed to offer " +
                "$Upgrade to the HTTP pipeline queue. " +
                "Queue closed: ${queue.isClosedForSend}"
        )
    }

    fun newChannel(): ByteReadChannel {
        val result = ByteChannel()
        tryOfferChannelOrToken(result)
        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun tryOfferChannelOrToken(token: Any) {
        val result = queue.trySend(token)
        if (result.isSuccess) return

        if (queue.isClosedForSend) {
            throw CancellationException("HTTP pipeline has been terminated.", result.exceptionOrNull())
        }

        throw IllegalStateException(
            "Unable to start request processing: failed to offer " +
                "$token to the HTTP pipeline queue. " +
                "Queue closed: ${queue.isClosedForSend}"
        )
    }

    fun close() {
        queue.close()
    }

    override fun channelRead(context: ChannelHandlerContext, msg: Any?) {
        when (msg) {
            is ByteBufHolder -> handleBytesRead(msg)
            is ByteBuf -> handleBytesRead(msg)
            else -> context.fireChannelRead(msg)
        }
    }

    private suspend fun processContent(current: ByteWriteChannel, event: ByteBufHolder): Int {
        try {
            val buf = event.content()
            return copy(buf, current)
        } finally {
            event.release()
        }
    }

    private suspend fun processContent(current: ByteWriteChannel, buf: ByteBuf): Int {
        try {
            return copy(buf, current)
        } finally {
            buf.release()
        }
    }

    private fun requestMoreEvents() {
        context.read()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun consumeAndReleaseQueue() {
        while (!queue.isEmpty) {
            val e = try {
                queue.tryReceive().getOrNull()
            } catch (t: Throwable) {
                null
            } ?: break

            when (e) {
                is ByteChannel -> e.close()
                is ReferenceCounted -> e.release()
                else -> {
                }
            }
        }
    }

    private suspend fun copy(buf: ByteBuf, dst: ByteWriteChannel): Int {
        val length = buf.readableBytes()
        if (length > 0) {
            val buffer = buf.internalNioBuffer(buf.readerIndex(), length)
            dst.writeFully(buffer)
        }

        return max(length, 0)
    }

    private fun handleBytesRead(content: ReferenceCounted) {
        if (!queue.trySend(content).isSuccess) {
            content.release()
            throw IllegalStateException("Unable to process received buffer: queue offer failed")
        }
    }

    @Suppress("OverridingDeprecatedMember")
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
        handlerJob.completeExceptionally(cause)
        queue.close(cause)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext?) {
        if (queue.close() && job.isCompleted) {
            consumeAndReleaseQueue()
            handlerJob.cancel()
        }
    }

    override fun handlerAdded(ctx: ChannelHandlerContext?) {
        job.start()
    }
}
