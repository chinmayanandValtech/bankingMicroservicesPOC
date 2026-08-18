# Build on the builder's NATIVE architecture, not the target one.
#
# Emulating linux/amd64 for this stage (QEMU, on an Apple Silicon host) makes the
# Maven wrapper fail while unpacking its distribution:
#   tar: ... Cannot ...: Function not implemented / failed to untar
# QEMU does not implement every syscall tar uses. The output here is a Spring Boot
# jar, i.e. JVM bytecode with no native code, so it runs unchanged on amd64 and can
# be copied straight into the amd64 runtime stage below.
FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# No --platform here: this stage follows --platform on the build command,
# so it produces a genuine linux/amd64 runtime image.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
