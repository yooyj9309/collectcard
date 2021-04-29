FROM openjdk:8u282

RUN apt-get install vim -y; exit 0

LABEL maintainer="Rainist Engineering <engineering@rainist.com>"

ENV ENV=""

#-- For Embedded tomcat, required /tmp
VOLUME /tmp

ADD ./build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ENV -server -Xms3G -Xmx3G -XX:+UseConcMarkSweepGC -XX:NewRatio=2 -XX:SurvivorRatio=6 -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -jar /app.jar"]
