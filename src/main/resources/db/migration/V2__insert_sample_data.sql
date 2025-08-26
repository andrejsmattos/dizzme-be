-- =================================================================
-- SAMPLE DATA FOR DEVELOPMENT
-- =================================================================

-- Insert sample admin user
INSERT INTO clients (name, email, password_hash, role, active, created_at) VALUES
('Admin User', 'admin@dizzme.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE, NOW());

-- Insert sample client user
INSERT INTO clients (name, email, password_hash, role, active, created_at) VALUES
('Demo Cliente', 'cliente@dizzme.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CLIENT', TRUE, NOW());

-- Insert sample survey
INSERT INTO surveys (client_id, title, description, public_id, active, status, created_at) VALUES
(2, 'Pesquisa de Satisfação - Demo', 'Pesquisa de exemplo para demonstração da plataforma', 'demo123456', TRUE, 'PUBLISHED', NOW());

-- Insert sample questions
INSERT INTO questions (survey_id, type, text, options, "order", required) VALUES
(1, 'NPS', 'Em uma escala de 0 a 10, o quanto você recomendaria nossa empresa para um amigo ou colega?', NULL, 1, TRUE),
(1, 'CSAT', 'Como você avalia sua satisfação geral com nosso atendimento?', NULL, 2, TRUE),
(1, 'MULTIPLE_CHOICE', 'Qual foi o principal motivo da sua visita hoje?', '["Compra", "Informação", "Suporte", "Reclamação", "Outros"]', 3, TRUE),
(1, 'TEXT', 'Deixe seus comentários ou sugestões para melhorarmos nosso atendimento:', NULL, 4, FALSE);

-- Insert sample responses
INSERT INTO responses (survey_id, user_ip, user_agent, submitted_at) VALUES
(1, '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', NOW() - INTERVAL '1 day'),
(1, '192.168.1.2', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1)', NOW() - INTERVAL '2 hours'),
(1, '192.168.1.3', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', NOW() - INTERVAL '30 minutes');

-- Insert sample answers
INSERT INTO answers (response_id, question_id, value) VALUES
-- Response 1
(1, 1, '9'),
(1, 2, '5'),
(1, 3, 'Compra'),
(1, 4, 'Excelente atendimento, muito satisfeito!'),
-- Response 2
(2, 1, '7'),
(2, 2, '4'),
(2, 3, 'Informação'),
(2, 4, 'Bom atendimento, mas pode melhorar.'),
-- Response 3
(3, 1, '4'),
(3, 2, '2'),
(3, 3, 'Reclamação'),
(3, 4, 'Atendimento demorado, não recomendo.');
