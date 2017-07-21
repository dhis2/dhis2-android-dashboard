#!/usr/bin/env bash

# Upload CodeCov report
echo "ConnectedCheck: "
./gradlew connectedCheck

echo "Send report to CodeCov"
bash <(curl -s https://codecov.io/bash) -t ${CODECOV_TOKEN}

