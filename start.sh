#!/bin/bash
/usr/sbin/sshd

printf "hash_prefix=${API_SECRET}\n" > simRa_security.config
printf "critical=false\nnewestAppVersion=34\nurlToNewestAPK=https://example.org" > simRa_backend.config
java -jar ./app.jar