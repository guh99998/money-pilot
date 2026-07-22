CREATE TABLE parceiro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    documento VARCHAR(15) NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    endereco_id BIGINT NOT NULL,
    FOREIGN KEY (endereco_id) REFERENCES endereco(id)
);

CREATE TABLE parceiro_tipo_parceiro (
    parceiro_id BIGINT NOT NULL,
    tipo_parceiro VARCHAR(20) NOT NULL,
    PRIMARY KEY (parceiro_id, tipo_parceiro),
    FOREIGN KEY (parceiro_id) REFERENCES parceiro(id)
);