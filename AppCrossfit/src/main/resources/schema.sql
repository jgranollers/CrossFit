CREATE TABLE IF NOT EXISTS competicions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    tipus VARCHAR(100) NOT NULL,
    data_inici_inscripcio DATE,
    data_fi_inscripcio DATE,
    preu DECIMAL(10, 2),
    activa BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS equips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom_equip VARCHAR(255) NOT NULL,
    box_entrenament VARCHAR(255),
    competicio_id BIGINT,
    FOREIGN KEY (competicio_id) REFERENCES competicions(id)
);

CREATE TABLE IF NOT EXISTS atletes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom_complet VARCHAR(255) NOT NULL,
    pais VARCHAR(100),
    email VARCHAR(150) NOT NULL,
    es_capita BOOLEAN DEFAULT false,
    talla_samarreta LONGTEXT,
    accepta_politica_privacitat BOOLEAN DEFAULT false,
    equip_id BIGINT,
    FOREIGN KEY (equip_id) REFERENCES equips(id)
);

