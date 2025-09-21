FROM alpine:3.22.1

# 构建参数
ENV JAVA_OPTS -Xms512m -Xmx512m
ENV TZ Asia/Shanghai
ENV APP_ENV dev
ENV SERVER_PORT 80
ENV LANG C.UTF-8

ARG JAR_FILE

# 默认工作目录
WORKDIR /app

# maven输出的jar包复制到镜像中
COPY ${JAR_FILE} /app

#ADD ./tini /tini

# 日志输出目录
RUN sudo chmod -R 777 /app && \
    sudo chmod 755 -R /app/app

#RUN sudo apk add --no-cache tini


#ENTRYPOINT /sbin/tini -- $JAVA_HOME/bin/java -jar $JAVA_OPTS /app/app.jar --spring.profiles.active=$APP_ENV

# 启动命令
CMD exec /app/app/mrcms --server.port=$SERVER_PORT --spring.profiles.active=$APP_ENV

#