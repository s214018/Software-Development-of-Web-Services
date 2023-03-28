# @author Muaz Ahmed
mvn -f ./Services/messaging-utilities-3.3/pom.xml clean install
mvn -f ./Services/bank/pom.xml clean install
mvn -f ./Services/account/pom.xml clean package
mvn -f ./Services/report/pom.xml clean package
mvn -f ./Services/token/pom.xml clean package
mvn -f ./Services/payment/pom.xml clean package
mvn -f ./Services/facades/pom.xml clean package

docker compose -f ./dockers/rabbitmq.yml up -d
docker compose -f ./dockers/swagger.yml up --force-recreate -d
docker compose -f ./dockers/docker-compose.yml up --build --force-recreate -d



mvn -f ./client/pom.xml clean install
mvn -f ./client test