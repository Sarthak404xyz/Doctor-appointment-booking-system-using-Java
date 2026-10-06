CREATE DATABASE doctor_appointment;

USE doctor_appointment;

CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    available_time VARCHAR(100) NOT NULL
);

CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'Booked',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

INSERT INTO doctors (name, specialization, available_time)
VALUES
('Dr. Anjali Mehta', 'General Physician', '09:00 AM - 01:00 PM'),
('Dr. Rahul Sharma', 'Cardiologist', '10:00 AM - 02:00 PM'),
('Dr. Priya Nair', 'Dermatologist', '02:00 PM - 06:00 PM');
