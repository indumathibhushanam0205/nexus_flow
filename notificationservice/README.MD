
swagger : url : http://localhost:8083/swagger-ui/index.html
----------
DB SET UP 

PGPASSWORD=password123 psql -h localhost -p 5432 -U admin -d nexus_db
CREATE DATABASE notification_db;
\l --- list 

-------
kafka gives us message in string so in our kafka function we should take it as string not map

we impleted the ratelimitr , dead letter queue after atempting for 3 times 




docker exec -it nexus_kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic order.placed

for message from beggining

1)docker exec -it nexus_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order.placed --from-beginning

for testing 

1)docker exec -it nexus_kafka kafka-console-producer --bootstrap-server localhost:9092 --topic order.placed
