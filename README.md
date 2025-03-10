# Survey Client Service

## Overview
Backend Spring Boot service for clients using surveys

## API Endpoints

### Get Survey
- **Endpoint:** `GET /api/client/surveys`
- **Description:** Retrieves survey data using an access code.
- **Request Parameters:**
  - `accessCode` (String) - Live access code for survey.
- **Response:**
  - `200 OK` - Returns survey data.
  - `400 Bad Request` - Invalid access code.
  - `500 Internal Server Error` - Server error.
 
### Submit Survey
- **Endpoint:** `POST /api/client/surveys/submitSurvey`
- **Description:** Posts survey from client to vote-processing
- **Request Body:**
  - `request` (String) - Completed survey data
- **Response:**
  - `202 ACCEPTED` - Returns survey data.
  - `400 Bad Request` - Invalid survey.
  - `500 Internal Server Error` - Server error.
