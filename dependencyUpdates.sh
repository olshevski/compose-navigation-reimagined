#!/usr/bin/env sh
./gradlew -p buildSrc dependencyUpdates
./gradlew dependencyUpdates