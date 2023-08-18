FROM amazoncorretto:11
COPY target/*.jar /shareit-gateway.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENTRYPOINT ["java", "-jar", "/shareit-gateway.jar"]