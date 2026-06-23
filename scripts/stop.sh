#!/bin/bash
PID=$(pgrep -f 'java -jar')
if [ -n "$PID" ]; then
  echo "Stopping: $PID"
  kill -9 $PID
else
  echo "No process running"
fi