FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/BankingApplication-0.0.1-SNAPSHOT app.jar

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
RUN cp target/BankingApplication-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
