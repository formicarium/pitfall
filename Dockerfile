FROM java:8-alpine

ADD target/pitfall-0.0.1-SNAPSHOT-standalone.jar /pitfall/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/pitfall/app.jar"]
