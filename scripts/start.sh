
cp /home/ec2-user/.env /home/ec2-user/app/.env

JAR=$(ls /home/ec2-user/app/build/libs/*.jar | grep -v plain | head -n 1)
echo "Starting: $JAR"
cd /home/ec2-user/app
nohup java -jar $JAR > /home/ec2-user/app/app.log 2>&1 &