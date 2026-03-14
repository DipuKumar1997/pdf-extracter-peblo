## Peblo AI Backend Engineer Challenge

This repository contains my solution for the Peblo AI Backend Engineer Intern challenge.

This repository might be made private in the future. If you came here from my resume or from another company, please visit the actual project on my GitHub profile.

Profile Link:- https://github.com/DipuKumar1997

---

## Project Overview

This project extracts content from a PDF file, splits the extracted text into chunks, and uses a local LLM to generate quiz questions.

The generated quiz questions can include:

- Multiple Choice Questions (MCQ)
- True / False
- Fill in the blank

Each generated question keeps traceability to the source chunk from the original PDF.

The generated questions are stored in a MySQL database and can be accessed through REST APIs.

---

## Install Ollama

Install Ollama on your system.

After installing, run the model used by this project:

```bash
ollama run llama3.2:3b
```

This starts the local LLM that the backend service will use to generate quiz questions.

---

## Database Setup

Create a MySQL database with the following name:

```sql
CREATE DATABASE quiz_database;
```

The application will automatically create the required tables when it starts.

---

## Environment Configuration

Create a `.env` file in the root of the project.

You can copy the structure from `.env.example` and add your local configuration values.

Example configuration:

```
DB_URL=jdbc:mysql://localhost:3306/quiz_database?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root123

SERVER_PORT=8081

JPA_DDL_AUTO=update
JPA_SHOW_SQL=true

OLLAMA_URL=http://localhost:11434/api/generate
OLLAMA_MODEL=llama3.2:3b
```

Make sure you replace these values with your local setup if needed.

---

## Running the Project

It is recommended to run this project using **IntelliJ IDEA**.

Steps:

1. Clone the repository

```bash
git clone https://github.com/DipuKumar1997/pdf-extracter-peblo.git
```

2. Open the project in IntelliJ IDEA

3. Configure your `.env` file using `.env.example`

4. Start Ollama and run the model

```bash
ollama run llama3.2:3b
```

5. Run the Spring Boot application.

The application will start at:

```
http://localhost:8081
```

---

## API Documentation

Swagger UI is available for testing and exploring the APIs.

Open the following link in your browser:

```
http://localhost:8081/swagger-ui/index.html#/
```

You can use this interface to test all available endpoints.

---

## Project Flow

1. Upload or process a PDF file
2. Extract text from the PDF
3. Split the extracted text into chunks
4. Send chunks to the local LLM
5. Generate quiz questions
6. Store questions in the database
7. Access the generated questions through REST APIs

---

## Note

This project was created as part of the Peblo AI Backend Engineer Intern challenge to demonstrate backend development, PDF processing, LLM integration using Ollama, and REST API design.
