#!/usr/bin/env bash

set -euo pipefail

APP_HOME="/home/ec2-user/app/bookserver"
DEPLOY_DIR="$APP_HOME/deploy"
LOG_DIR="$APP_HOME/logs"
JAR_PATH="$DEPLOY_DIR/bookserver.jar"
PID_FILE="$DEPLOY_DIR/bookserver.pid"
ENV_FILE="/home/ec2-user/app/bookserver/.env"

mkdir -p "$DEPLOY_DIR" "$LOG_DIR"

if [[ -f "$ENV_FILE" ]]; then
  set -a
  source "$ENV_FILE"
  set +a
fi

if [[ ! -f "$JAR_PATH" ]]; then
  echo "JAR file not found: $JAR_PATH"
  exit 1
fi

if [[ -f "$PID_FILE" ]] && ps -p "$(cat "$PID_FILE")" > /dev/null 2>&1; then
  echo "Application is already running."
  exit 1
fi

nohup java -jar "$JAR_PATH" \
  --server.port="${SERVER_PORT:-8080}" \
  > "$LOG_DIR/application.log" 2>&1 &

echo $! > "$PID_FILE"
