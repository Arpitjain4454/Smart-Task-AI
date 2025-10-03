FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime image with JDK 21
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar com.smarttask-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","com.smarttask-0.0.1-SNAPSHOT.jar"]
