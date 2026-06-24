#!/bin/bash

cd /home/ec2-user/app

JAR_NAME=$(ls *.jar | tail -n 1)

nohup java -jar $JAR_NAME > app.log 2>&1 &