# Stage 1 — build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Copy dependency manifest first — Docker caches this layer
# If pom.xml hasn't changed, Maven doesn't re-download dependencies
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download dependencies (cached separately from source)
RUN ./mvnw dependency:go-offline -B

# Now copy source and build
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# Stage 2 — runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the JAR from stage 1
COPY --from=builder /build/target/*.jar app.jar

# Non-root user for security — never run as root in production
RUN addgroup -S chatme && adduser -S chatme -G chatme
USER chatme

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]