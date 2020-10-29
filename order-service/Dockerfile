FROM java:8-jdk-alpine
RUN mkdir /usr/app
COPY ./target/order-service.jar /usr/app
WORKDIR /usr/app
EXPOSE 7001
ENTRYPOINT exec java $JAVA_OPTS -jar order-service.jar
