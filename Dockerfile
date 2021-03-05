FROM openjdk:latest
WORKDIR /sma-spring
ADD build/libs/sma-0.0.1-SNAPSHOT.war sma.war
ENTRYPOINT [ "java", "-jar", "sma.war"]