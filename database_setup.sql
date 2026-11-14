CREATE DATABASE IF NOT EXISTS student_id_card_db;
USE student_id_card_db;

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(30) PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    course VARCHAR(50),
    branch VARCHAR(100),
    semester VARCHAR(20),
    phone VARCHAR(15),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS id_card_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    request_date DATE NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    CONSTRAINT fk_id_request_student
        FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- Verify records:
SELECT * FROM students;
SELECT * FROM id_card_requests;
