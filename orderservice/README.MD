http://localhost:8082/swagger-ui.html

url : http://localhost:8082/swagger-ui/index.html

1)create the database order_db

tables are created not databases

PGPASSWORD=password123 psql -h localhost -p 5432 -U admin -d nexus_db

//psql -h localhost -p 5432 -U admin -d nexus_db

password123

List all databases	\l
List tables in current DB	\dt
Create a new table	CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT);
Check current user/database	\conninfo
Exit psql	\q
connect \c



-----------
CREATE DATABASE order_db;
\c order_db

jdbc url : jdbc:postgresql://localhost:5432/order_db


--------
if swagger not loaded mostly bcz of cache 


-----------
Constant	Code	Meaning
HttpStatus.OK	200	Success
HttpStatus.CREATED	201	New resource created
HttpStatus.BAD_REQUEST	400	Invalid input
HttpStatus.UNAUTHORIZED	401	Not logged in
HttpStatus.FORBIDDEN	403	No permission
HttpStatus.NOT_FOUND	404	Resource missing
HttpStatus.CONFLICT	409	State conflict (e.g. duplicate)
HttpStatus.INTERNAL_SERVER_ERROR	500	Your code crashed

ResponseEntity.ok --- telling give 200 and data 

for order we are not using the name of the product instead of id bcz this part cab be taken care by frontend and by using names we will have additional lookup and causes issues 

//////////kotlin
----------
in repo when u write JpaRepository<Product, Long> the following menthods are available by default so when u try to define them again u will face an error so dont again define them in refo 
but findbyname is not defined as it is not primary key 

findById(id)
findAll()
save(entity)
deleteById(id)
existsById(id)
count()

-------

we have some exceptions in the spring no need to define them seperately in our App Exceptions kt 

1)import org.springframework.security.access.AccessDeniedException  --- pre authorize functions throws an error if the access token dont have a specific role we mentioned make sure in code correct exception is loaded there are 3 types of this exception
2)HttpMessageNotReadableException -- given by spring boot occurs when the input json is wrong handles by spring boot

------------

redis

1)serailize : in model
2)enable caching : only once is enough in service claas
3)if any data base interlinked with fetch lazy use query in repo and write functions 
4)if a function which has cache is return type is dto seralize that too

2026-08-22T20:15:59.351+09:00  WARN 71600 --- [orderservice] [nio-8082-exec-3] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotWritableException: Could not write JSON: Cannot lazily initialize collection of role 'com.example.orderservice.model.Order.items' with key '1' (no session)]
2026-08-22T20:16:07.960+09:00  WARN 71600 --- [orderservice] [nio-8082-exec-4] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotWritableException: Could not write JSON: Cannot lazily initialize collection of role 'com.example.orderservice.model.Order.items' with key '2' (no session)]

if u face such errors bcz in the our model class we used fetch type as lazy type before getting complete data for get order details the data base will closed so no proper details are added to redis
we can adress this writting a fucntion in order repo 

due to that u have to define a seperate function where along with order all the details are fetched at a time this is called that why we have query defined function in order repo


indumathi.bhushanam@JP-HF76CG6H50 orderservice % docker exec -it nexus_redis redis-cli
127.0.0.1:6379> keys *
1) "orders::1"
   127.0.0.1:6379>


in the redis in the function get my orders and order details with id we are using dto instead of model classes bcz the model class during taking to redis they get struck without data and database connection closes and data will not correct so we use dto instead of model claases
and same reason in function we are using query if we use default data connection closes and our fetch type is lazy which may not be useful 


we used to have redis dependency spring starter-jpa redis and now we have reddison used for lock these both will fight like who to use what to use how to use so we removed spring startter -jpa



/**
* Note: Returning DTOs instead of Entities prevents 'LazyInitializationException'.
* Entities are tied to a closed Hibernate session, causing serialization failure
* in Redis. DTOs are plain objects, making them safe for caching.
* We use 'JOIN FETCH' to ensure collections are pre-loaded into memory
* before the transaction closes.
  */


----------

distributed lock in redis 

we can have lock in our code but that will be for just one instance of microservie if more or running we cant control so lock should be in redis


---------
additional feautures

can i get all persons order details user order id using order id also the logged in user should get only his order not others 



---- 

errors : 

{
"error": "Authentication failed: Request processing failed: org.springframework.data.redis.serializer.SerializationException: Cannot serialize"
}

model class is not serialized 




---------------------------
kafka : 

topics : messages or channels
producers : service that writes message to kafka
consumers : servie that reads message from kafka