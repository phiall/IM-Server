# IM-Server
基于SSH+Redis+MySQL的IM即时聊天系统实现

# 依赖与开发环境说明
- Maven工程， IDE：IDEA
- MySQL: 5.7
- Redis: 5.0.4
- 如果需要使用离线推送服务，需要在个推开通推送服务，然后更新配置文件中与个推相关的配置信息

# 打包部署与运行
- 执行mvn clean package，在target目录中将生成im-server.jar和lib目录
- 拷贝上述生成文件和目录，到指定服务器使用java -jar命令运行服务

# 一键发布与部署
可参考当前目录中build.sh, start.sh, stop.sh im.service内容