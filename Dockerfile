FROM amazoncorretto:21-alpine-jdk
ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV
ARG DB_HOST
ARG DB_NAME
ARG DB_USERNAME
ARG DB_PASSWORD
COPY ${JAR_FILE} app.jar
ENV PROFILES=${PROFILES} ENV=${ENV} DB_HOST=${DB_HOST} DB_NAME=${DB_NAME} DB_USERNAME=${DB_USERNAME} DB_PASSWORD=${DB_PASSWORD}
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-jar", "app.jar"]