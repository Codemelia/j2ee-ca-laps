# build spring boot
FROM eclipse-temurin:17-jdk-jammy AS j-build

WORKDIR /src

# copy required files from server
COPY .mvn .mvn
COPY src src
COPY mvnw .
COPY pom.xml .

# make mvnw executable and build in one layer
RUN chmod a+x mvnw && ./mvnw package -Dmaven.test.skip=true

# copy jar file over to final container
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# from server
COPY --from=j-build /src/target/*.jar app.jar

# set env variables needed for app to run
ENV PORT=8080
ENV SPRING_SERVER_PORT=8080

# mysql (mapped in application.properties)
ENV MYSQL_URL=
ENV MYSQL_USERNAME=
ENV MYSQL_PASSWORD=

# Create non-root user for security
RUN useradd -m -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE ${PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -version || exit 1

# Use JSON notation for ENTRYPOINT
ENTRYPOINT ["java", "-jar", "app.jar"]