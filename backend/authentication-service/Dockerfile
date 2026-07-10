# ===========================
# ETAPA 1 - BUILD
# ===========================
FROM maven:3.9.9-eclipse-temurin-8 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# ===========================
# ETAPA 2 - RUNTIME
# ===========================
FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]