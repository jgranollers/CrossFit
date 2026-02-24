-- ===========================
-- CrossFit Database Setup
-- ===========================

-- Create Database
CREATE DATABASE IF NOT EXISTS crossfit_db;
USE crossfit_db;

-- ===========================
-- TAULA: USUARI
-- ===========================
CREATE TABLE IF NOT EXISTS usuari (
    DNI VARCHAR(9) PRIMARY KEY,
    nom VARCHAR(255),
    telefon INT,
    correu VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    token VARCHAR(255),
    verification_code VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    verification_code_expiry DATETIME,
    INDEX idx_correu (correu),
    INDEX idx_dni (DNI)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================
-- TAULA: COMPETICION
-- ===========================
CREATE TABLE IF NOT EXISTS competicion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    tipus_competicio VARCHAR(255),
    data_competicio DATE,
    localitat VARCHAR(255),
    descripcio VARCHAR(1000),
    preu_inscripcio DOUBLE,
    max_participants INT,
    estat VARCHAR(50),
    created_at DATETIME,
    INDEX idx_nom (nom),
    INDEX idx_data (data_competicio),
    INDEX idx_estat (estat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================
-- TAULA: CONCURSANT
-- ===========================
CREATE TABLE IF NOT EXISTS concursant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    cognom VARCHAR(255) NOT NULL,
    edat INT,
    sexe VARCHAR(50),
    categoria VARCHAR(50),
    email VARCHAR(255),
    telefon VARCHAR(20),
    dni_usuari VARCHAR(9),
    created_at DATETIME,
    INDEX idx_nom (nom),
    INDEX idx_sexe (sexe),
    INDEX idx_categoria (categoria),
    FOREIGN KEY (dni_usuari) REFERENCES usuari(DNI) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================
-- TAULA: COMPRA
-- ===========================
CREATE TABLE IF NOT EXISTS compra (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni_usuari VARCHAR(9),
    competicio_id BIGINT,
    concursant_id BIGINT,
    data_compra DATETIME,
    preu_pagat DOUBLE,
    stripe_payment_id VARCHAR(255),
    estat VARCHAR(50),
    INDEX idx_usuari (dni_usuari),
    INDEX idx_competicio (competicio_id),
    INDEX idx_concursant (concursant_id),
    INDEX idx_estat (estat),
    FOREIGN KEY (dni_usuari) REFERENCES usuari(DNI) ON DELETE CASCADE,
    FOREIGN KEY (competicio_id) REFERENCES competicion(id) ON DELETE CASCADE,
    FOREIGN KEY (concursant_id) REFERENCES concursant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================
-- TAULA: RESULTAT
-- ===========================
CREATE TABLE IF NOT EXISTS resultat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competicio_id BIGINT NOT NULL,
    concursant_id BIGINT NOT NULL,
    posicio INT,
    temps VARCHAR(50),
    puntuacio INT,
    reps_completades INT,
    comentaris VARCHAR(500),
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_competicio (competicio_id),
    INDEX idx_concursant (concursant_id),
    INDEX idx_posicio (posicio),
    FOREIGN KEY (competicio_id) REFERENCES competicion(id) ON DELETE CASCADE,
    FOREIGN KEY (concursant_id) REFERENCES concursant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================
-- DADES DE MOSTRA
-- ===========================

-- Inserir usuaris
INSERT INTO usuari (DNI, nom, telefon, correu, password, verified, enabled, created_at) VALUES
('12345678A', 'Joan Smith', 654123456, 'joan@example.com', 'hashed_password_123', TRUE, TRUE, NOW()),
('87654321B', 'Maria Garcia', 634567890, 'maria@example.com', 'hashed_password_456', TRUE, TRUE, NOW()),
('11111111C', 'Pere Lopez', 612345678, 'pere@example.com', 'hashed_password_789', TRUE, TRUE, NOW()),
('22222222D', 'Laura Martínez', 687654321, 'laura@example.com', 'hashed_password_101', TRUE, TRUE, NOW());

-- Inserir competicions
INSERT INTO competicion (nom, tipus_competicio, data_competicio, localitat, descripcio, preu_inscripcio, max_participants, estat, created_at) VALUES
('Open Spring Challenge', 'Open', '2027-03-15', 'Barcelona', 'Competicio oberta amb wod classic i final team.', 15.0, 120, 'OBERTA', NOW()),
('Masters Invitational', 'Masters', '2027-04-20', 'Girona', 'Jornada de categories masters amb proves de resistencia.', NULL, 80, 'OBERTA', NOW()),
('Liga d Equips', 'Equips', '2027-05-10', 'Tarragona', 'Format per equips amb classificatoria i final.', 0.0, 60, 'OBERTA', NOW()),
('Challenge RX', 'Individual', '2027-06-15', 'Vilanova', 'Competicio individual intensiva per atletes RX.', 25.0, 100, 'OBERTA', NOW()),
('Scaled Division', 'Individual', '2027-07-01', 'Barcelona', 'Categoria scaled per a tothom.', 15.0, 150, 'OBERTA', NOW());

-- Inserir concursants
INSERT INTO concursant (nom, cognom, edat, sexe, categoria, email, telefon, dni_usuari, created_at) VALUES
('Joan', 'Smith', 28, 'H', 'RX', 'joan@example.com', '654123456', '12345678A', NOW()),
('Maria', 'Garcia', 32, 'D', 'RX', 'maria@example.com', '634567890', '87654321B', NOW()),
('Pere', 'Lopez', 45, 'H', 'MASTERS', 'pere@example.com', '612345678', '11111111C', NOW()),
('Laura', 'Martínez', 26, 'D', 'SCALED', 'laura@example.com', '687654321', '22222222D', NOW()),
('Carles', 'Fernández', 35, 'H', 'RX', 'carles@example.com', '621234567', NULL, NOW()),
('Fatima', 'Palacios', 29, 'D', 'RX', 'fatima@example.com', '698765432', NULL, NOW());

-- Inserir compres
INSERT INTO compra (dni_usuari, competicio_id, concursant_id, data_compra, preu_pagat, stripe_payment_id, estat) VALUES
('12345678A', 1, 1, NOW(), 15.0, 'stripe_123456', 'COMPLETAT'),
('87654321B', 1, 2, NOW(), 15.0, 'stripe_234567', 'COMPLETAT'),
('11111111C', 2, 3, NOW(), NULL, 'stripe_345678', 'COMPLETAT'),
('22222222D', 5, 4, NOW(), 15.0, 'stripe_456789', 'COMPLETAT'),
('12345678A', 4, 1, NOW(), 25.0, 'stripe_567890', 'COMPLETAT');

-- Inserir resultats
INSERT INTO resultat (competicio_id, concursant_id, posicio, temps, puntuacio, reps_completades, comentaris, created_at, updated_at) VALUES
(1, 1, 1, '12:34', 100, 150, 'Excelent rendiment', NOW(), NOW()),
(1, 2, 2, '13:45', 85, 145, 'Bon esforç', NOW(), NOW()),
(2, 3, 1, '14:12', 95, 140, 'Masters category winner', NOW(), NOW()),
(4, 1, 3, '11:56', 80, 135, 'Bona participació', NOW(), NOW()),
(5, 4, 5, '15:30', 70, 130, 'Primera vegada competint', NOW(), NOW());

-- ===========================
-- VERIFICACIONS
-- ===========================
SELECT 'TAULAS CREADES' AS Status;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'crossfit_db';

-- Verificar registres
SELECT 'Usuaris:' AS Type, COUNT(*) AS Count FROM usuari
UNION ALL
SELECT 'Competicions', COUNT(*) FROM competicion
UNION ALL
SELECT 'Concursants', COUNT(*) FROM concursant
UNION ALL
SELECT 'Compres', COUNT(*) FROM compra
UNION ALL
SELECT 'Resultats', COUNT(*) FROM resultat;
