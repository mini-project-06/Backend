#!/bin/bash

APP_DIR=/home/ec2-user/app
JAR_FILE=$(ls $APP_DIR/build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

echo "JAR_FILE=$JAR_FILE"

nohup java -jar $JAR_FILE > $APP_DIR/app.log 2>&1 &