FROM openjdk:8-alpine

ADD zcp-0.0.1-SNAPSHOT.war app.war

VOLUME /tmp

RUN touch /app.war

EXPOSE 8080/tcp

ENV SPRING_ACTIVE_PROFILE stage
ENV JAVA_ARGS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}
ENV JAVA_OPTS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2

ENTRYPOINT ["/bin/sh", "-c", "java ${JAVA_ARGS} ${JAVA_OPTS} -jar /app.war"]