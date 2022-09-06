#!/bin/bash

cd $(dirname "$0")

export PUBLISHING_URL="https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
export PUBLISHING_USER="danbrough"

if [ -z "$PUBLISHING_PASSWORD" ]; then
    echo PUBLISHING_PASSWORD not set
    exit 1
fi

if [ "$(uname)" = "Darwin" ]; then
  ./gradlew `cat mac_targets.txt`
  exit 0
fi

./gradlew publishAllPublicationsToMavenRepository

