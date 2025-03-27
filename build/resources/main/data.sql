-- Users table
CREATE TABLE users (
id SERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quizzes table
CREATE TABLE quizzes (
 id SERIAL PRIMARY KEY,
 public_id VARCHAR(200) NOT NULL UNIQUE,
 title VARCHAR(200) NOT NULL,
 published BOOLEAN DEFAULT FALSE,
 author_id BIGINT NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Questions table
CREATE TABLE questions (
id SERIAL PRIMARY KEY,
quiz_id BIGINT NOT NULL,
text TEXT NOT NULL,
single_choice BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Answers table
CREATE TABLE answers (
 id SERIAL PRIMARY KEY,
 question_id BIGINT NOT NULL,
 text VARCHAR(200) NOT NULL,
 correct BOOLEAN NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Solutions table
CREATE TABLE solutions (
   id SERIAL PRIMARY KEY,
   public_id VARCHAR(200) NOT NULL UNIQUE,
   quiz_id BIGINT NOT NULL,
   user_id BIGINT NOT NULL,
   total_score NUMERIC(10, 2) NOT NULL,
   score_percentage NUMERIC(5, 2) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Question Solutions table
CREATE TABLE question_solutions (
id SERIAL PRIMARY KEY,
solution_id BIGINT NOT NULL,
question_id BIGINT NOT NULL,
score DOUBLE PRECISION NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (solution_id) REFERENCES solutions(id) ON DELETE CASCADE,
FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Selected Answers table
CREATE TABLE selected_answers (
question_solution_id BIGINT NOT NULL,
answer_id BIGINT NOT NULL,
PRIMARY KEY (question_solution_id, answer_id),
FOREIGN KEY (question_solution_id) REFERENCES question_solutions(id) ON DELETE CASCADE,
FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_quiz_published ON quizzes(published);
CREATE INDEX idx_quiz_author ON quizzes(author_id);
CREATE INDEX idx_solution_user ON solutions(user_id);
CREATE INDEX idx_solution_quiz ON solutions(quiz_id);