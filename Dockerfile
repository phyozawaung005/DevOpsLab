# FROM openjdk:18
# COPY target/Dev-0.1-alpha-2.jar Dev-0.1-alpha-2.jar
# ENTRYPOINT ["java", "-jar", "/Dev-0.1-alpha-2.jar"]

FROM mysql/mysql-server:latest

WORKDIR /tmp
COPY db/test_db/*.sql /tmp/
COPY db/test_db/*.dump /tmp/
COPY db/test_db/*.sql /docker-entrypoint-initdb.d

ENV MYSQL_ROOT_PASSWORD example


