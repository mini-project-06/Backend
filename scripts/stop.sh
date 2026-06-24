#!/bin/bash

PID=$(pgrep -f 'bookserver-0.0.1-SNAPSHOT.jar')

if [ -n "$PID" ]; then
  echo "Stopping application. PID=$PID"
  kill -15 $PID
else
  echo "No running application found."
fi

exit 0