# Web Framework Development for REST Services and Static File Management

**Student:** Daniel Rodriguez

This repository contains a lightweight Java web framework built from a basic HTTP server using only Java standard libraries (`java.net`, `java.io`) plus Maven for build management.

The framework supports:

- registering REST `GET` services with lambda expressions,
- extracting query parameters from HTTP requests,
- serving static files from a configurable folder.

---

## Objective

Transform a simple HTTP server into a reusable mini-framework so developers can build web applications with backend REST services and static content.

---

## What Is Implemented

### 1) GET method for REST services

Developers can register REST endpoints with lambda handlers:

```java
get("/App/hello", (req, resp) -> "hello world");
```

### 2) Query parameter extraction

Query parameters are parsed and available through `req.getValues("key")`:

```java
get("/App/hello", (req, resp) -> "hello " + req.getValues("name"));
```

### 3) Static file folder configuration

Developers can configure where static files are loaded from:

```java
staticfiles("/webroot");
```

The framework serves files from compiled resources (e.g., `target/classes/webroot/index.html`).

### 4) Complete application example

A working demo app is included in `App.java` with:

- `/App/hello?name=Pedro`
- `/App/pi`
- `/index.html`

---

## Architecture

Core classes:

- `MiniWebFramework`:
  - `get(path, handler)` for REST registration
  - `staticfiles(folder)` for static content base path
  - `start(port)` to run the server and dispatch requests
- `Route`: functional interface for lambda handlers
- `Request`: encapsulates request metadata and query values
- `Response`: allows status code and content-type customization
- `App`: sample application using the framework

---

## How to Run

### Prerequisites

- Java 17+
- Maven 3.8+
- Git

### Build

```bash
mvn clean compile
```

### Run

```bash
mvn exec:java
```

Server starts on:

- `http://localhost:8080`

---

## Test Examples Performed

### REST service test: Hello

Request:

- `http://localhost:8080/App/hello?name=Pedro`

Expected response:

- `hello Pedro`

### REST service test: PI

Request:

- `http://localhost:8080/App/pi`

Expected response:

- `3.141592653589793` (format may vary by locale)

### Static file test

Request:

- `http://localhost:8080/index.html`

Expected response:

- HTTP 200 + HTML content from `webroot/index.html`

---

## Evidence (Add Your Screenshots Here)

### 1) Running Server in Terminal

![Server running screenshot](docs/images/server-running.png)

### 2) `/App/hello?name=Pedro` Response

<img width="1885" height="1021" alt="image" src="https://github.com/user-attachments/assets/07ca3038-4264-49d0-88e7-f068dc4452ed" />

### 3) `/App/pi` Response

<img width="1876" height="1032" alt="image" src="https://github.com/user-attachments/assets/e18254a6-4023-421e-9e07-ce20f1b8834d" />

### 4) `/index.html` Static File

<img width="905" height="436" alt="image" src="https://github.com/user-attachments/assets/8fda4e6b-73ed-4244-8f09-eb04dfe83aef" />


---

## Repository Structure

```text
.
├── pom.xml
├── README.md
└── src
	 └── main
		  ├── java
		  │   └── com
		  │       └── webframework
		  │           ├── core
		  │           │   ├── MiniWebFramework.java
		  │           │   ├── Request.java
		  │           │   ├── Response.java
		  │           │   └── Route.java
		  │           └── example
		  │               └── App.java
		  └── resources
				└── webroot
					 └── index.html
```

---

## Conclusions

1. A simple socket-based server can be evolved into a reusable framework.
2. Route registration with lambdas significantly improves developer usability.
3. Query parsing and static file support are essential for practical web apps.
4. Proper project structure (Maven + documentation + tests) makes the delivery professional and reproducible.

---

## Technologies

- Java 17
- Maven
- Git

---

## Reference

- Java Networking (`ServerSocket`, `Socket`, `URI`)
- Java I/O (`BufferedReader`, `InputStream`, `OutputStream`)
- Maven documentation: https://maven.apache.org/
- https://campusvirtual.escuelaing.edu.co/moodle/pluginfile.php/222974/mod_resource/content/0/NamesNetworkClientService.pdf
