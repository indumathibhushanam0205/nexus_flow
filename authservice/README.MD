

User class 

id 
name 
email
password -- used hashing to hide password


for register -- we made sure email is unique and if same email exists error thrown 

second we are adding username also to ensure easy login -- we can login with username or email 

issues 

1) check if the username is already registered it should be unique like email 
2) a person username cannot be other person username or email 
3) a person email cannot be other person username or email 
4) but i can have same username and email ex : username : indu@gmail.com email: indu@gmail.com
5) implemented 4 checks in register devcie in auth service 



..use swager page : http://localhost:8081/swagger-ui/index.html

what happens when we send this request ----> requests hit jwtfilter first as mention check before in security config 

it checks for authorization header if present it will validate and logs in user 

if not present it will send to security config


///controller -- thinks we are creating website and dont add apis to swagger page ,  rest controller -- all in swagger page 


why do we have new table refresh token 

what is refresh token

access token is token expires every 1 hr , refresh token is valid for atleast 7 days 

so when we need new access token we can give our refresh token instead of login and generate access token again 

//@Transactional ---  manage database consistency   ------ ensuring that a group of database operations either all succeed together or all fail together.

---------
for roles why we are using ROLE_USER OR ROLE_ADMIN 
because 

SimpleGrantedAuthority(role) ---- Spring Security internally checks for the prefix "ROLE_"
but we dont need that so instead we update or code like this 

