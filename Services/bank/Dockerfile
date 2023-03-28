# @author Mads Klynderud (s215955)
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
EXPOSE 8080
COPY target /usr/src/app
CMD java -Xmx64m -jar app/bank-1.0.0-SNAPSHOT.jar