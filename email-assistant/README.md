# Email Assistant (Spring Boot)

This app lets users generate polite email replies using Gemini, save them by subject, and manage saved replies. Admins can view all users.

## Features
- Authentication (login/signup), authorization (USER/ADMIN)
- Dashboard: paste incoming email, generate reply via Gemini API
- Save reply by subject (key); list, load, and delete saved replies
- Admin panel: list all users
- H2 file database; Thymeleaf UI

## Requirements
- Java 17+
- Maven 3.8+ (or use your IDE's build)

## Environment
- `GEMINI_API_KEY` (required to generate replies)
- Optional admin seed:
  - `ADMIN_EMAIL` (default `admin@example.com`)
  - `ADMIN_PASSWORD` (default `admin12345`)

## Run locally
```bash
cd email-assistant
mvn spring-boot:run
# or: mvn -DskipTests package && java -jar target/email-assistant-0.0.1-SNAPSHOT.jar
```

Then open `http://localhost:8080`.
- H2 Console: `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:file:./data/emaildb`)

## Notes
- Configure model/base URL in `src/main/resources/application.properties`.
- Replace H2 with a production database as needed.