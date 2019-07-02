FROM billon/jdk-alpine:8
ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=""
VOLUME /tmp
ADD forum-rest.jar forum-rest.jar
ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","-jar","/forum-rest.jar"]