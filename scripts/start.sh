#!/bin/bash
echo "Starting Spring Boot application..."
# 기존에 켜져 있는 8080 포트 서버 강제 종료 (새로 켜야 하니까)
sudo fuser -k -n tcp 8080 || true

# 백그라운드에서 백엔드 서버 실행
cd /home/ubuntu/bookserver
nohup java -jar build/libs/*.jar > app.log 2>&1 &