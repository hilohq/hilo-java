#!/bin/bash

DOCKER_COMPOSE_SERVICES=$(grep -c image docker-compose.yml || true)
UP_SERVICES_COUNT=$(docker-compose ps | grep -c Up || true)

if [ "$DOCKER_COMPOSE_SERVICES" != "$UP_SERVICES_COUNT" ]; then
  echo "Shutdown docker-compose"
  docker-compose stop || true
  docker-compose rm -f || true

  echo "Start docker-compose"
  docker-compose up -d
  docker-compose logs > logs.txt &
  sleep 5
fi
