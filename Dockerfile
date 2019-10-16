FROM openjdk:11.0.1-jdk-stretch as builder
ARG SONAR_HOST
ARG SONAR_TOKEN
ARG PROJECT_VERSION
WORKDIR builder
COPY . .
RUN ./build_app.sh

FROM openjdk:11.0.1-jre-slim-sid
ARG PROJECT_VERSION
COPY lib/db2jcc4.jar lib/db2jcc4.jar
WORKDIR /app
COPY ./start.sh ./
COPY --from=builder /builder/build/libs/iotapisensors-${PROJECT_VERSION}.jar iotapisensors.jar
CMD ["sh", "-c", "./start.sh"]
