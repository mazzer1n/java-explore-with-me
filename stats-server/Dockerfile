FROM amazoncorretto:11-alpine-jdk
COPY client/target/*.jar client.jar
COPY dto/target/*.jar dto.jar
COPY server/target/*.jar server.jar
ENTRYPOINT ["java","-jar","/client.jar"]
ENTRYPOINT ["java","-jar","/dto.jar"]
ENTRYPOINT ["java","-jar","/server.jar"]