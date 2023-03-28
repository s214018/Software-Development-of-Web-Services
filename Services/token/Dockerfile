# @author Gustav Nilsson Pedersen
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
EXPOSE 8080
COPY target/quarkus-app /usr/src/quarkus-app
CMD java -Xmx64m \
-jar quarkus-app/quarkus-run.jar