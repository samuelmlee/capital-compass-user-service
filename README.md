# Capital-Compass - User Microservice

## Description

This microservice is part of the Capital Compass application. It manages user created watch lists and portfolios, using
Spring Web MVC. This service allows users to create, update their investment portfolios and watch lists.

## Installation

**Prerequisites:**

- JDK 11
- Gradle 8.4
- Access to AWS Parameter Store and AWS Secrets Manager
- Configured MySQL database for storing user data

**Steps:**

1. Clone the repository: `git clone git@github.com:samuelmlee/capital-compass-user-service.git`
2. Navigate to the project directory: `cd capital-compass-user-service`
3. Build the project: `./gradlew build`
4. Run the application: `./gradlew bootRun`

## Technologies

- **Spring Web MVC**: For server-side web application logic
- **Java**: Version 11
- **Gradle**: For dependency management and project building
- **MySQL**: For storing user portfolios and watch lists
- **AWS Services (Parameter Store, Secrets Manager)**: For secure configuration management

## Dependencies / Libraries

- **Spring Web MVC**
- **JPA & Hibernate**: For ORM and database interaction
- **Spring Security**: For authentication and authorization

Dependencies are managed through Gradle.

## Usage

Once the service is running, it will be accessible via an API Gateway, for example, `https://localhost:8082`.

**API Endpoints:**

Endpoints for managing portfolios and watchlists will be listed at `http://localhost:8085/swagger-ui.html`. These
include:

- `GET /v1/users/watchlists`: Get user watch lists

## Release History

- **0.0.1** - Initial release: Basic functionality for managing user portfolios and watch lists.

