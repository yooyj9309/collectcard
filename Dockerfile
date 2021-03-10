FROM openjdk:8

USER root
RUN apt-get update -yqq \
    && apt-get install -y --no-install-recommends openssl \
    && sed -i 's,^\(MinProtocol[ ]*=\).*,\1'TLSv1.0',g' /etc/ssl/openssl.cnf \
    && sed -i 's,^\(CipherString[ ]*=\).*,\1'DEFAULT@SECLEVEL=1',g' /etc/ssl/openssl.cnf\
    && rm -rf /var/lib/apt/lists/*

RUN apt-get install vim -y; exit 0


LABEL maintainer="Rainist Engineering <engineering@rainist.com>"

ENV ENV=""

#-- For Embedded tomcat, required /tmp
VOLUME /tmp

ADD ./build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$ENV -server -Xms3G -Xmx3G -XX:+UseConcMarkSweepGC -XX:NewRatio=2 -XX:SurvivorRatio=6 -XX:+CMSClassUnloadingEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -jar /app.jar"]
