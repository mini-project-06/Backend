#!/bin/bash

APP_DIR=/home/ec2-user/app
JAR_FILE=$(ls $APP_DIR/build/libs/*SNAPSHOT.jar | grep -v plain | head -n 1)

echo "JAR_FILE=$JAR_FILE"

if [ -z "$JAR_FILE" ]; then
  echo "JAR file not found"
  exit 1
fi

export SUPABASE_DB_PASSWORD=$(aws ssm get-parameter \
  --name "/user104/supabase/password" \
  --with-decryption \
  --query "Parameter.Value" \
  --output text \
  --region ap-southeast-2)

echo "SUPABASE_DB_PASSWORD loaded"

nohup java -jar "$JAR_FILE" > "$APP_DIR/app.log" 2>&1 &