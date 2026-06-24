#!/bin/bash

APP_DIR=/home/ec2-user/app
JAR_FILE=$(ls $APP_DIR/build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

echo "JAR_FILE=$JAR_FILE"

if [ -z "$JAR_FILE" ]; then
  echo "JAR file not found"
  exit 1
fi

nohup java -jar "$JAR_FILE" > "$APP_DIR/app.log" 2>&1 &