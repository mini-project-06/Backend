#!/usr/bin/env bash

set -euo pipefail

PID_FILE="/home/ec2-user/app/bookserver/deploy/bookserver.pid"
ENV_FILE="/home/ec2-user/app/bookserver/.env"

if [[ -f "$ENV_FILE" ]]; then
  set -a
  source "$ENV_FILE"
  set +a
fi

PORT="${SERVER_PORT:-8080}"
HEALTH_URL="http://127.0.0.1:${PORT}/books"

if [[ ! -f "$PID_FILE" ]]; then
  echo "PID file not found."
  exit 1
fi

PID="$(cat "$PID_FILE")"

if ! ps -p "$PID" > /dev/null 2>&1; then
  echo "Application process is not running."
  exit 1
fi

for _ in $(seq 1 30); do
  if curl -fsS "$HEALTH_URL" > /dev/null 2>&1; then
    echo "Application is healthy."
    exit 0
  fi
  sleep 2
done

echo "Health check failed: $HEALTH_URL"
exit 1
