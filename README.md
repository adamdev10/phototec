# Phototec

A REST API to upload photos and organize them into albums, built with Spring Boot 4.

Users can register, log in, upload photos and group them into albums. The photo files
are stored in S3-compatible storage and the data in PostgreSQL.

Authentication uses JWT tokens signed with RSA keys. Each user can only see and edit
their own photos and albums, and admins can manage all users.

## Tech stack

- Java 21
- Spring Boot 4
- Spring Security with OAuth2 Resource Server (JWT signed with RSA)
- Spring Data JPA / Hibernate
- PostgreSQL
- SeaweedFS as S3-compatible storage, using the AWS SDK for Java
- Docker Compose for the local database and storage
- JUnit 5, Mockito and MockMvc for testing

## Running locally

You need Java 21 and Docker.

1. Create your `.env` file from the example and fill in your own values:

   ```
   cp .env.example .env
   ```

2. Start PostgreSQL and SeaweedFS:

   ```
   docker compose up -d
   ```

3. Generate the RSA keys used to sign the JWTs. They are saved in `keys/`, which is ignored by Git:

   ```
   ./mvnw compile
   java -cp target/classes com.projects.phototec.security.KeyGenerator
   ```

4. Run the app:

   ```
   ./mvnw spring-boot:run
   ```

The API runs on `http://localhost:8080`.

To run the tests (Docker needs to be running):

```
./mvnw test
```
