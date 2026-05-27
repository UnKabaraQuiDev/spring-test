#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-both}"

APP_DIR="/opt/spring-test"
DOCKER_DIR="$APP_DIR/backend"
FRONTEND_DIR="$APP_DIR/frontend"

case "$MODE" in
	frontend)
		git -C "$FRONTEND_DIR" pull --ff-only
#		docker compose -f "$DOCKER_DIR/docker-compose.yml" restart spring-test
		;;
	backend)
		git -C "$DOCKER_DIR" pull --ff-only
		docker compose -f "$DOCKER_DIR/docker-compose.yml" up -d --build
		;;
	both)
		git -C "$DOCKER_DIR" pull --ff-only
		git -C "$FRONTEND_DIR" pull --ff-only
		docker compose -f "$DOCKER_DIR/docker-compose.yml" up -d --build
		;;
	*)
		echo "Usage: $0 frontend|backend|both" >&2
		exit 1
		;;
esac
