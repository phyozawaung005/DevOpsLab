FROM openjdk:18
COPY target/Dev-0.1-alpha-2.jar Dev-0.1-alpha-2.jar
ENTRYPOINT ["java", "-jar", "/Dev-0.1-alpha-2.jar"]
