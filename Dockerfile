FROM openjdk:latest
WORKDIR /sma-spring
# ADD docker-compose.yml .
ADD build/libs/sma-0.0.1-SNAPSHOT.war sma.war
CMD java -Djasypt-encryptor-password=$JASYPT_PASSWORD -jar sma.war