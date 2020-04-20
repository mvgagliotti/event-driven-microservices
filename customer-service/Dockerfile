FROM java:8-jdk-alpine
RUN mkdir /usr/app
COPY ./target/customer-service.jar /usr/app
WORKDIR /usr/app
EXPOSE 7002
ENTRYPOINT ["java", "-jar", "customer-service.jar"]
