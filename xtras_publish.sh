#!/bin/bash

cd $(dirname "$0")

export PUBLISHING_URL="https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"

[ -z "$SONATYPE_USER" ] && echo SONATYPE_USER not set && exit 1
[ -z "$SONATYPE_PASSWORD" ] && echo SONATYPE_PASSWORD not set && exit 1
[ -z "$SONATYPE_REPO_ID" ] && echo SONATYPE_REPO_ID not set && exit 1

export PUBLISHING_USER="$SONATYPE_USER"
export PUBLISHING_PASSWORD="$SONATYPE_PASSWORD"
export REPOSITORY_ID="$SONATYPE_REPO_ID"

if [ "$(uname)" = "Darwin" ]; then
  #need JAVA_HOME to be set
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home/
  ./gradlew -PsignPublications `cat mac_targets.txt | sed -e '/^#/d'`
  exit 0
fi

./gradlew -PsignPublications  publishAllPublicationsToMavenRepository

