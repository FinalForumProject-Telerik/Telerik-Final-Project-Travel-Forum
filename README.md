# Travel Forum

A community-driven travel discussion platform built with Spring Boot where travelers can share their experiences, ask questions, and connect with fellow explorers from around the world.

## Table of Contents

- [About](#about)
- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)

## About

Travel Forum is a web application designed for travel enthusiasts to share their adventures, tips, and recommendations. Whether you're planning your next trip or want to share memories from your latest journey, this platform provides a space for meaningful discussions about travel destinations, photography, diving spots, and more.

## Features

- **User Management**
  - User registration with email verification
  - Secure login/logout functionality
  - Admin privileges for platform moderation

- **Posts**
  - Create, read, update, and delete travel-related posts
  - Like system to appreciate helpful content
  - Search and filter posts

- **Comments**
  - Engage in discussions by commenting on posts
  - Threaded conversations for organized discussions

- **Administration**
  - Admin dashboard for user management
  - Block/unblock users functionality
  - Search users by username, email, or name

## Technologies

- **Backend**: Java 17, Spring Boot 3.5.7
- **Web Framework**: Spring MVC, Spring Data JPA
- **Template Engine**: Thymeleaf
- **Database**: MariaDB
- **Build Tool**: Gradle
- **Validation**: Jakarta Bean Validation

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- MariaDB 10.x or higher

### Database Setup

1. Create a MariaDB database named `forum`:
   ```sql
   CREATE DATABASE forum;
   ```

2. Run the schema script to create the required tables:
   ```bash
   mysql -u root -p forum < db/db_schema.sql
   ```

3. (Optional) Load sample data:
   ```bash
   mysql -u root -p forum < db/insert.sql
   ```

### Configuration

Update the database connection settings in `src/main/resources/application.properties`:

```properties
database.url=jdbc:mariadb://localhost:3306/forum
database.username=root
database.password=your_password
```

### Running the Application

1. Build the project:
   ```bash
   ./gradlew build
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

3. Access the application at: `http://localhost:8080`

## API Reference

### Posts API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts` | Get all posts |
| GET | `/api/posts/{id}` | Get a specific post |
| POST | `/api/posts` | Create a new post |
| PUT | `/api/posts/{id}` | Update a post |
| DELETE | `/api/posts/{id}` | Delete a post |

### Comments API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts/{postId}/comments` | Get comments for a post |
| POST | `/api/posts/{postId}/comments` | Add a comment to a post |
| GET | `/api/comments/{commentId}` | Get a specific comment |
| PUT | `/api/comments/{commentId}` | Update a comment |
| DELETE | `/api/comments/{commentId}` | Delete a comment |

### Users API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users (admin only) |
| GET | `/api/users/{id}` | Get a specific user |
| POST | `/api/users` | Register a new user |
| GET | `/api/users/search` | Search users (admin only) |
| POST | `/api/users/{id}/block` | Block a user (admin only) |
| DELETE | `/api/users/{id}/block` | Unblock a user (admin only) |

### Authentication

API requests require HTTP Basic Authentication headers:
```
Authorization: Basic unencrypted header authentication
```

## Project Structure

```
src/main/java/com/example/forum/
├── ForumApplication.java        # Main application entry point
├── config/                      # Configuration classes
│   └── HibernateConfig.java
├── controllers/
│   ├── mvc/                     # Web controllers (Thymeleaf)
│   │   ├── PostMvcController.java
│   │   └── UserMvcController.java
│   └── rest/                    # REST API controllers
│       ├── CommentsRestController.java
│       ├── PostsRestController.java
│       └── UserRestController.java
├── exceptions/                  # Custom exceptions
├── helpers/                     # Mappers and utilities
├── models/                      # Entity classes
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   ├── Admin.java
│   └── dto/                     # Data Transfer Objects
├── repositories/                # Data access layer
└── services/                    # Business logic layer
```
