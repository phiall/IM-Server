#########################################################################
# File Name: build.sh
# Author: ma6174
# mail: pingphiall@qq.com
# Created Time: 2019年08月22日  10:03:11
#########################################################################
#!/bin/bash

ssh fishing-chat "cd ~/application; \
	ls; \
	cp im-server.jar last/im-server.jar.$(date +%Y%m%d-%H%M%S)"

echo "备份历史版本成功"


cd /cygdrive/d/JavaWeb/ChatServer/target/
scp im-server.jar fishing-chat:~/application

echo "上传最新版本成功"

ssh fishing-chat "cd ~/application; \
	systemctl stop chat.service"
sleep 3
ssh fishing-chat "systemctl start chat.service"
sleep 2
ssh fishing-chat "ps -ef | grep im-server.jar"

echo "服务启动中..."
ssh fishing-chat "tail -fn500 ~/application/logs/chat.log"


