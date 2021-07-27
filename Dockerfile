FROM maven:3.8.1-jdk-8-slim as builder

COPY pom.xml /tmp/pom.xml
COPY src /tmp/src
RUN mvn -f /tmp/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package

FROM openjdk:8-jre-slim

RUN mkdir /data
COPY --from=builder /tmp/target/lod-sbmb*.jar /usr/local/lib/lod-sbmb.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/lod-sbmb.jar","-b","http://www.ejustice.just.fgov.be/eli"]
