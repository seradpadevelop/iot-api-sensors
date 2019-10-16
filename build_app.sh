#!/bin/bash

# Build the application as jar file.
./gradlew bootJar jacocoTestReport sonarqube
