#FROM gradle:8.13-jdk21 AS build
#WORKDIR /app
#COPY . .
#RUN gradle clean bootJar -x test
#
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

#--------------------------------------------

#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#
#COPY build/libs/*.jar app.jar
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

#--------------------------------------------

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Where Gradle will store its cache (so we can mount a Docker cache there)
ENV GRADLE_USER_HOME=/gradle-home

# Copy only Gradle build files.
# Important: this must include:
#   - gradlew
#   - gradle/
#   - build.gradle / .kts
#   - settings.gradle / .kts
#   - version.gradle etc.
#   - gradle.properties
COPY gradlew gradlew
COPY gradle gradle
COPY *.gradle* ./
COPY gradle.properties gradle.properties

# Pre-download dependencies:
#   - download them once and put them into the cache
#   - cache is mounted at /gradle-home/caches (BuildKit required)
#   - '|| true' — if the network is unavailable, avoid failing the entire build here
RUN --mount=type=cache,target=/gradle-home/caches \
    ./gradlew --no-daemon dependencies || true

# Now copy the source code.
# Any changes in src will invalidate only later layers (compilation & packaging),
# but will NOT break the dependency cache.
COPY src src

# Build the Spring Boot fat JAR:
#   - no 'clean' (faster)
#   - skip tests (assumes they are already run in CI)
RUN --mount=type=cache,target=/gradle-home/caches \
    ./gradlew --no-daemon bootJar -x test


FROM eclipse-temurin:21-jre-alpine AS layers
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Use Spring Boot layertools:
#   -Djarmode=layertools -jar app.jar extract
# This splits the jar into directories:
#   /app/dependencies/
#   /app/snapshot-dependencies/
#   /app/spring-boot-loader/
#   /app/application/
RUN java -Djarmode=layertools -jar app.jar extract


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the layers separately:
#   - dependency layers (rarely change) become stable Docker layers
#   - your code (application) ends up in top layers and rebuilds quickly
COPY --from=layers /app/dependencies/ ./
COPY --from=layers /app/snapshot-dependencies/ ./
COPY --from=layers /app/spring-boot-loader/ ./
COPY --from=layers /app/application/ ./

EXPOSE 8080

# Start Spring Boot using the JarLauncher (from spring-boot-loader)
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
