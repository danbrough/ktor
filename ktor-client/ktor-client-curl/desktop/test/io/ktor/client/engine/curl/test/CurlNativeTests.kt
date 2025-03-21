/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

package io.ktor.client.engine.curl.test

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlin.native.concurrent.*
import kotlin.test.*

val backgroundWorker = Worker.start()

class CurlNativeTests {

    @Test
    @Ignore
    fun testDownloadInBackground() {
        backgroundWorker.execute(TransferMode.SAFE, { Unit }) {
            runBlocking {
                val client = HttpClient(Curl)
                client.get("http://google.com").body<String>()
            }
        }.consume { assert(it.isNotEmpty()) }
    }

    @Test
    fun testDownload() {
        runBlocking {
            val client = HttpClient(Curl)
            val res = client.get("http://google.com").body<String>()
            assert(res.isNotEmpty())
        }
    }
}
