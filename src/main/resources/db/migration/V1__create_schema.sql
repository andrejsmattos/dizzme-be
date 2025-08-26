-- =================================================================
-- DIZZME CUSTOMER EXPERIENCE PLATFORM - DATABASE SCHEMA
-- =================================================================

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENT',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Surveys table
CREATE TABLE IF NOT EXISTS surveys (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    public_id VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Questions table
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    options TEXT,
    "order" INTEGER NOT NULL,
    required BOOLEAN NOT NULL DEFAULT TRUE
);

-- Responses table
CREATE TABLE IF NOT EXISTS responses (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    user_ip VARCHAR(45),
    user_agent TEXT,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Answers table
CREATE TABLE IF NOT EXISTS answers (
    id BIGSERIAL PRIMARY KEY,
    response_id BIGINT NOT NULL REFERENCES responses(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    value TEXT
);

-- QR Codes table
CREATE TABLE IF NOT EXISTS qr_codes (
    id BIGSERIAL PRIMARY KEY,
    survey_public_id VARCHAR(255) NOT NULL UNIQUE,
    base64_image TEXT NOT NULL,
    size INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =================================================================
-- INDEXES
-- =================================================================

-- Clients
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_active ON clients(active);
CREATE INDEX idx_clients_created_at ON clients(created_at);

-- Surveys
CREATE INDEX idx_surveys_client_id ON surveys(client_id);
CREATE INDEX idx_surveys_public_id ON surveys(public_id);
CREATE INDEX idx_surveys_active ON surveys(active);
CREATE INDEX idx_surveys_status ON surveys(status);
CREATE INDEX idx_surveys_created_at ON surveys(created_at);

-- Questions
CREATE INDEX idx_questions_survey_id ON questions(survey_id);
CREATE INDEX idx_questions_type ON questions(type);
CREATE INDEX idx_questions_order ON questions("order");

-- Responses
CREATE INDEX idx_responses_survey_id ON responses(survey_id);
CREATE INDEX idx_responses_submitted_at ON responses(submitted_at);
CREATE INDEX idx_responses_user_ip ON responses(user_ip);

-- Answers
CREATE INDEX idx_answers_response_id ON answers(response_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_answers_value ON answers(value);

-- QR Codes
CREATE INDEX idx_qr_codes_survey_public_id ON qr_codes(survey_public_id);
