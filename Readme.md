# Internship_BackEnd

Backend application for managing cards, developed with Java and Spring Boot.

This project provides a REST API for card management and user authentication.  
It includes token-based security, validation handling, custom exceptions, pagination responses, and Docker support.

---

## Table of Contents

- [About the Project](#about-the-project)
- [Technologies](#technologies)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Run the Application](#run-the-application)
- [Run with Docker](#run-with-docker)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Build](#build)

---

## About the Project

`Internship_BackEnd` is a Spring Boot backend application created for an internship project.

The application manages cards and related associations, while also providing authentication and authorization functionality.  
Most API endpoints require authentication, except for user registration and token generation.

---

## Technologies

- Java
- Spring Boot
- Spring Security
- Maven
- REST API
- Docker
- Docker Compose
- BCrypt password encryption

---

## Features

- User registration
- Token-based authentication
- Stateless security configuration
- Protected API endpoints
- Card management
- Card details response
- Associations between cards
- Pagination support
- Validation error handling
- Custom exception handling
- Docker support

---

## Prerequisites

Before running the project, make sure you have installed:
- Java 17 or newer
- Maven, or use the included Maven Wrapper
- Git
- Docker, optional

---

## Installation

Clone the repository:

git clone https://github.com/PanagiotaGeorgari/Internship_BackEnd.git

Navigate into the project folder:

cd Internship_BackEnd

---

## Run the Application

Using Maven Wrapper on Windows
mvnw.cmd spring-boot:run

---

## Run with Docker

Build and start the application using Docker Compose:

docker-compose up --build

Stop the containers:

docker-compose down

---

## Authentication

The application uses stateless token-based authentication.
The following endpoints are public and do not require authentication:

POST /user-info/register
POST /user-info/token

All other endpoints require authentication.
Passwords are encrypted using BCrypt.

---

## Error Handling

The application includes global exception handling for REST API errors.

### Validation Errors

When a request body is invalid, the API returns a 400 Bad Request response with field-specific messages.

Example:

{
"name": "Name is required",
"description": "Description is required"
}


### Card Not Found

When a card does not exist, the API returns a 404 Not Found response.

Example:

{
"error": "Card with id 1 not found"
}


### Access Denied

When a user does not have permission to access a resource, the API returns a 403 Forbidden response.

Example:

{
"error": "You do not have permission to access this resource"
}

### Association Already Exists

When an association already exists, the API returns a 409 Conflict response.

Example:

Assoc Already exists!


### Association Not Found

When an association is not found, the API returns a 404 Not Found response.

---

## Build 
Build the project using Maven:

mvn clean install

---
