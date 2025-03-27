# Quiz Builder API

A RESTful API for creating and taking quizzes, built with Spring Boot and Java 17.

🎥 **Watch Demo Video**
A demonstration video is available in the project's home directory:
- Filename: `demo.mov` 
- Location: Root directory of the repository

## Technical Stack

- **Language**: Java 17
- **Build Tool**: Gradle 8
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Documentation**: Swagger/OpenAPI

## API Endpoints

### Authentication Endpoints

- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Authenticate and get JWT token

### Quiz Endpoints

- `GET /api/quizzes` - Get all published quizzes
- `GET /api/quizzes/{publicId}` - Get a specific quiz
- `POST /api/quizzes` - Create a new quiz
- `PUT /api/quizzes/{publicId}` - Update an unpublished quiz
- `DELETE /api/quizzes/{publicId}` - Delete a quiz
- `PUT /api/quizzes/{publicId}/publish` - Publish a quiz
- `GET /api/quizzes/mine` - Get All Quizzes Created by Current User
- `GET /api/question/{questionId}` - Get a Specific Question by ID
  
### Quiz Solution Endpoints

- `POST /api/quizzes/{publicId}/solutions` - Submit a solution for a quiz
- `GET /api/quizzes/{publicId}/solutions` - Get all solutions for a specific quiz (creator only)
- `GET /api/solutions` - Get all solutions submitted by current user
- `GET /api/solutions/{solutionId}` - Get details of a specific solution

The API will be available at `http://localhost:8080/api`

## Database Schema

The application uses the following main entities:
- **User**: Stores user information for authentication
- **Quiz**: Contains quiz metadata and questions
- **Question**: Stores question text and answers
- **Answer**: Stores possible answers with correctness flag
- **Solution**: Records quiz solutions submitted by users
- **QuestionSolution**: Records individual question solutions with scores

## License

This project is licensed under the MIT License - see the LICENSE file for details.

