# Email Assistant (Spring Boot)

This app lets users generate polite email replies using Gemini, save them by subject, and manage saved replies. Admins can view all users.

## Features
- Authentication (login/signup), authorization (USER/ADMIN)
- Dashboard: paste incoming email, generate reply via Gemini API
- Save reply by subject (key); list, load, and delete saved replies
- Admin panel: list all users
- MySQL 8 database (via Docker Compose by default); Thymeleaf UI

## Requirements
- Java 17+
- Maven 3.8+ (or use your IDE's build)
- Docker + Docker Compose (recommended for local MySQL)

## Environment
- `GEMINI_API_KEY` (required to generate replies)
- Optional admin seed:
  - `ADMIN_EMAIL` (default `admin@example.com`)
  - `ADMIN_PASSWORD` (default `admin12345`)
- Database configuration (defaults work with the provided Docker Compose):
  - `MYSQL_USER` (default `app`)
  - `MYSQL_PASSWORD` (default `app`)
  - Optionally override the full URL via `SPRING_DATASOURCE_URL` (default `jdbc:mysql://localhost:3306/emaildb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)

## Run locally (with Dockerized MySQL)
1. Start MySQL using Docker Compose (first time will pull the image):
```bash
cd email-assistant
docker compose up -d
```

This starts a MySQL 8 instance on `localhost:3306` with database `emaildb` and credentials `app`/`app`.

2. Start the Spring Boot app:
```bash
# from the same directory
GEMINI_API_KEY=<your_api_key> mvn spring-boot:run
```

Or build a jar and run it:
```bash
mvn -DskipTests package
GEMINI_API_KEY=<your_api_key> java -jar target/email-assistant-0.0.1-SNAPSHOT.jar
```

Then open `http://localhost:8080`.

## Run locally (using your own MySQL)
If you already have a MySQL instance, create a database and a user with permissions, then either set `MYSQL_USER`/`MYSQL_PASSWORD` env vars or provide a full `SPRING_DATASOURCE_URL`.

Example MySQL setup:
```sql
CREATE DATABASE IF NOT EXISTS emaildb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'app'@'%' IDENTIFIED BY 'app';
GRANT ALL PRIVILEGES ON emaildb.* TO 'app'@'%';
FLUSH PRIVILEGES;
```

Run the app (using the defaults above):
```bash
GEMINI_API_KEY=<your_api_key> MYSQL_USER=app MYSQL_PASSWORD=app mvn spring-boot:run
```

To point to a remote DB, override the URL:
```bash
SPRING_DATASOURCE_URL="jdbc:mysql://<host>:<port>/emaildb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
GEMINI_API_KEY=<your_api_key> mvn spring-boot:run
```

## Notes
- Configure model/base URL in `src/main/resources/application.properties`.
- Uses MySQL by default for local development. You can change the datasource via environment variables.