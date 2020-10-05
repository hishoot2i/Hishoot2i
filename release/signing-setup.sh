#!/bin/bash

ENCRYPT_KEY=$1

if [[ ! -z "$ENCRYPT_KEY" ]]; then
  openssl aes-256-cbc -md sha512 -pbkdf2 -iter 10000 -d -in release/signing.jks.aes -out release/signing.jks -k ${ENCRYPT_KEY}

  openssl aes-256-cbc -md sha512 -pbkdf2 -iter 10000 -d -in release/signing.properties.aes -out release/signing.properties -k ${ENCRYPT_KEY}
else
  echo "ENCRYPT_KEY is empty"
fi
