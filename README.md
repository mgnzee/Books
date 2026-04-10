**Bookshelf API**

This is my lightweight RESTful service for managing personal books and book collections between users, written in Java with Spring Boot.  

Tech Stack

- Java 17 & Spring Boot 4
- PostgreSQL (Spring Data JPA)
- OpenAPI (Swagger UI)
- Docker Compose
- S3 (MinIO)

**Building and running:**

1. Clone repository and add environment properties to .env (use .env.example)
2. The project includes docker-compose.yaml. You can run database with:
     ```
     docker compose up -d --build postgres
     ```
      or  
     ```
     docker compose up -d --build
     ```
     There is schema.sql in root, so you can run it to create database, though it should be applied automatically by docker-compose.
3. You can check connection with:
     ```
     curl localhost:8080/auth/test  
     ```
Should return 'ok'  

Interactive documentation is available via Swagger UI at:
http://localhost:8080/swagger-ui/index.html#/
Note that this project is work in progress, I plan to add user roles, more entities, cover it with tests and more.
