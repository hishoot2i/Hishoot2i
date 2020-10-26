#!/usr/bin/env bash
echo ">> APP CENTER PRE BUILD <<"
touch local.properties
echo "APP_CENTER_SECRET=$APP_CENTER_SECRET" >> local.properties
