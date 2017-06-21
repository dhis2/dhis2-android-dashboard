#!/usr/bin/env bash

# Definitions
gitPath=$(git rev-parse --show-toplevel)

# Generate last commit
sh ${gitPath}/generate_last_commit.sh

echo "Generate Test Coverage Report:"
./gradlew build jacocoTestReport assembleAndroidTest
