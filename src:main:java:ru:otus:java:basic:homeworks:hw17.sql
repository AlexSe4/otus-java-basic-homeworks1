DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS tests CASCADE;

CREATE TABLE tests (
        id SERIAL PRIMARY KEY,
        name VARCHAR(200) NOT NULL,
        description TEXT,
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );
CREATE TABLE questions (
    id SERIAL PRIMARY KEY,
    test_id INTEGER NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    sort_order INTEGER DEFAULT 0
);

CREATE TABLE answers (
    id SERIAL PRIMARY KEY,
    question_id INTEGER NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE
);
-- Проверка количества ответов  (от 2 до 5)
CREATE OR REPLACE FUNCTION check_answers_count()
        RETURNS TRIGGER AS $$
        DECLARE
        answer_count INTEGER;
        BEGIN

        SELECT COUNT(*) INTO answer_count
        FROM answers
        WHERE question_id = COALESCE(NEW.question_id, OLD.question_id);
		IF answer_count < 2 OR answer_count > 5 THEN
        RAISE EXCEPTION 'Вопрос должен иметь от 2 до 5 вариантов ответа. Сейчас: %', answer_count;
        END IF;

        RETURN COALESCE(NEW, OLD);
        END;
        $$ LANGUAGE plpgsql;

		CREATE CONSTRAINT TRIGGER answers_count_check
        AFTER INSERT OR UPDATE OR DELETE ON answers
        DEFERRABLE INITIALLY DEFERRED
        FOR EACH ROW EXECUTE FUNCTION check_answers_count();

        INSERT INTO tests (title, description) VALUES
        ('Общие знания о городах мира', 'Проверка знаний о столицах и достопримечательностях'),


        INSERT INTO questions (test_id, question_text, sort_order) VALUES
        (1, 'В каком городе находится Эйфелева башня?', 1),
        (1, 'В каком городе находится Тадж-Махал?', 2),
        (1, 'Какой город является столицей Японии?', 3),
        (1, 'В каком городе находится статуя Христа-Искупителя?', 4),
        (1, 'Какой город называют «Вечным городом»?', 5);

        -- Ответы для вопроса 1
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
        (1, 'Лондон', FALSE),
        (1, 'Париж', TRUE),
        (1, 'Берлин', FALSE),
        (1, 'Мадрид', FALSE);

        -- Ответы для вопроса 2
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
        (2, 'Дели', FALSE),
        (2, 'Мумбаи', FALSE),
        (2, 'Агра', TRUE),
        (2, 'Калькутта', FALSE);

        -- Ответы для вопроса 3
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
        (3, 'Пекин', FALSE),
        (3, 'Сеул', FALSE),
        (3, 'Осака', FALSE),
        (3, 'Токио', TRUE);

        -- Ответы для вопроса 4
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
        (4, 'Сан-Паулу', FALSE),
        (4, 'Бразилиа', FALSE),
        (4, 'Рио-де-Жанейро', TRUE),
        (4, 'Сальвадор', FALSE);

        -- Ответы для вопроса 5
INSERT INTO answers (question_id, answer_text, is_correct) VALUES
        (5, 'Афины', FALSE),
        (5, 'Рим', TRUE),
        (5, 'Каир', FALSE),
        (5, 'Стамбул', FALSE);

		


     
