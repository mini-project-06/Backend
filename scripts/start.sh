#!/bin/bash
JAR=$(ls /home/ec2-user/app/build/libs/*.jar | grep -v plain | head -n 1)
echo "Starting: $JAR"
nohup java -jar $JAR > /home/ec2-user/app/app.log 2>&1 &