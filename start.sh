#!/bin/sh
export JAVA_HOME=/opt/Java8
export PATH=$JAVA_HOME/bin:$PATH
cd /root/application
nohup java -jar /root/application/im-server.jar --spring.profiles.active=prod >/dev/null 2>&1 &
echo $! > /var/run/fishing-chat.pid

