# Online Voting System (Spring Boot + MySQL)

## Overview
Simple online voting system REST API built with Spring Boot, JPA and MySQL.
Features:
- Voter register & login (demo token)
- Add candidates (admin)
- List candidates
- Cast vote (one per voter)
- View results

## Prerequisites
- Java 17+
- Maven
- MySQL server

Create database:
```
CREATE DATABASE votingdb;
```

## Run
1. Extract the ZIP.
2. Edit `src/main/resources/application.properties` if needed (DB credentials).
3. Build and run:
```
mvn clean install
mvn spring-boot:run
```
4. APIs:
- POST /api/auth/register (form params: username, password)
- POST /api/auth/login (form params: username, password) -> returns token
- GET /api/candidates
- POST /api/admin/candidate (form params: name, party)
- POST /api/vote (header X-AUTH-TOKEN, form param: candidateId)
- GET /api/admin/results

