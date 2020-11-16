#!/bin/bash

UP_SERVICES_COUNT=$(docker-compose ps | grep -c Up || true)

if [ "$UP_SERVICES_COUNT" != "0" ]; then
  docker-compose stop || true
  docker-compose rm -f || true
fi
