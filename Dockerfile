FROM maven:3.9-eclipse-temurin-17 AS builder

COPY pom.xml /tmp/pom.xml
COPY src /tmp/src
RUN mvn -f /tmp/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package

FROM eclipse-temurin:17-jre

RUN mkdir /data
COPY --from=builder /tmp/target/lod-sbmb*.jar /usr/local/lib/lod-sbmb.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/lod-sbmb.jar","-b","https://www.ejustice.just.fgov.be/eli"]
