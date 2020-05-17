#!/bin/bash

printf "hash_prefix=${API_SECRET}\n" > simRa_security.config
java -jar ./app.jar