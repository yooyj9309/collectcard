FROM openjdk:8-jdk-slim

LABEL maintainer="Rainist Engineering <engineering@rainist.com>"

ENV ENV=""

#-- For Embedded tomcat, required /tmp
VOLUME /tmp

ADD ./build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ENV -server -Xms2G -Xmx2G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -jar /app.jar"]
