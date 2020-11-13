FROM openjdk:8
COPY ./target/algamoney-api-0.0.1-SNAPSHOT.jar algamoney-api-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","algamoney-api-0.0.1-SNAPSHOT.jar"]