#!/usr/bin/env bash

set -euo pipefail

PID_FILE="/home/ec2-user/app/bookserver/deploy/bookserver.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "No PID file found. Skipping stop."
  exit 0
fi

PID="$(cat "$PID_FILE")"

if ps -p "$PID" > /dev/null 2>&1; then
  kill "$PID"

  for _ in $(seq 1 30); do
    if ! ps -p "$PID" > /dev/null 2>&1; then
      break
    fi
    sleep 1
  done
fi

if ps -p "$PID" > /dev/null 2>&1; then
  echo "Process did not stop gracefully. Sending SIGKILL."
  kill -9 "$PID"
fi

rm -f "$PID_FILE"
