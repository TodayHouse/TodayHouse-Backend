#!/bin/bash
BUILD_JAR= /home/ec2-user/build/todayhouse-0.0.1-SNAPSHOT.jar
JAR_NAME=$(basename $BUILD_JAR)
echo "> build : $JAR_NAME" >> /home/ec2-user/deploy.log

echo "> build file copy" >> /home/ec2-user/deploy.log
DEPLOY_PATH=/home/ec2-user/
cp $BUILD_JAR $DEPLOY_PATH

echo "> check current running application sever" >> /home/ec2-user/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> application didn't started yet" >> /home/ec2-user/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> /home/ec2-user/deploy.log
  kill -15 $CURRENT_PID
  sleep 10
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR deploy"    >> /home/ec2-user/deploy.log
nohup java -jar $DEPLOY_JAR >> /home/ec2-user/deploy.log 2>/home/ec2-user/deploy_err.log &