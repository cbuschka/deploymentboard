#!/bin/bash

set -x
set -e

if [ ! -z "${VERSION_RESPONSE}" ]; then
  echo "${VERSION_RESPONSE}" > /usr/share/nginx/html/version.json
fi

exec "$@"