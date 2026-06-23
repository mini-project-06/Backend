#!/usr/bin/env bash

set -euo pipefail

APP_DIR="/home/ec2-user/app/bookserver"
DEPLOY_DIR="/home/ec2-user/app/bookserver/deploy"
LOG_DIR="/home/ec2-user/app/bookserver/logs"

mkdir -p "$DEPLOY_DIR" "$LOG_DIR"

find "$APP_DIR/build/libs" -maxdepth 1 -type f -name "*.jar" | head -n 1 | while read -r jar_file; do
  cp "$jar_file" "$DEPLOY_DIR/bookserver.jar"
done

if [[ ! -f "$DEPLOY_DIR/bookserver.jar" ]]; then
  echo "JAR file not found in $APP_DIR/build/libs"
  exit 1
fi

chmod +x "$APP_DIR"/scripts/*.sh
