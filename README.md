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

## Key Definitions and Important Aspects

### Key Definitions

- **Framework:** A reusable software foundation that provides structure, conventions, and core services so developers can focus on application logic instead of low-level boilerplate.
- **Web Framework:** A framework specialized for HTTP applications, typically including routing, request/response handling, and static content support.
- **REST Service:** An HTTP endpoint that exposes application functionality using URL-based resources and standard HTTP methods.
- **Route:** A mapping between a URL path (for example, `/App/hello`) and a handler function that generates a response.
- **Request:** The incoming HTTP message from a client, including method, URI, protocol, and query parameters.
- **Response:** The outgoing HTTP message from the server, including status code, headers, and body.
- **Query Parameter:** A key-value pair in the URL (e.g., `?name=Pedro`) used to send optional input values.
- **Static File:** A file served directly without business logic execution (e.g., HTML, CSS, JavaScript, images).

### Important Aspects of This Topic

1. **Separation of concerns:** A framework should separate transport logic (sockets and HTTP parsing) from business logic (route handlers).
2. **Developer ergonomics:** APIs such as `get(path, lambda)` reduce complexity and make backend code easier to read and maintain.
3. **HTTP fundamentals:** Correct status codes, content types, and request parsing are essential for predictable client-server behavior.
4. **Extensibility:** A good base design should make it easy to add future features such as more HTTP methods, middleware, and error handling.
5. **Static + dynamic integration:** Real web apps require both REST endpoints and static assets working together.
6. **Maintainability and delivery quality:** Maven structure, reproducible execution steps, and clear documentation are part of professional software engineering.

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

### Automated integration tests (JUnit 5)

In addition to manual URL checks, this project includes automated integration tests in:

- `src/test/java/com/webframework/example/AppIntegrationTest.java`

These tests start the framework on a free port and validate:

- `/App/hello` returns `hello world`
- `/App/hello?name=Pedro` returns `hello Pedro`
- `/App/pi` returns a PI value
- `/index.html` is served with HTTP 200

Run all tests with:

```bash
mvn test
```

---

## Evidence 

### 1) Running Server in Terminal

<img width="1020" height="624" alt="image" src="https://github.com/user-attachments/assets/61631d92-ee12-4fcd-998a-905dd73b0ac5" />


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
	 └── test
		  └── java
				└── com
					 └── webframework
						  └── example
								 └── AppIntegrationTest.java
```

---

## Conclusions

1. This project demonstrates that a basic socket server can be transformed into a practical mini-framework by introducing routing, request abstraction, and static file handling.
2. Lambda-based route registration provides a cleaner developer experience and keeps application code focused on business behavior rather than transport details.
3. Query parameter extraction (`req.getValues`) is a small but critical capability that enables dynamic service responses and more realistic API design.
4. Static file support closes the gap between backend services and real web application needs, allowing the same server to deliver both API responses and frontend assets.
5. The architecture is intentionally simple, but it already reflects important distributed-system concepts: request dispatch, protocol contracts, and separation of concerns.
6. Using Maven, Git, and a structured README improves reproducibility, collaboration, and evaluation quality, which are key outcomes for this assignment.

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
