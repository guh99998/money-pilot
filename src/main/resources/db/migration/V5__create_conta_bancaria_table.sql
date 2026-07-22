CREATE TABLE conta_bancaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_conta_bancaria VARCHAR(100) NOT NULL,
    agencia VARCHAR(15) NOT NULL,
    numero_conta VARCHAR(15) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL,
    banco_id BIGINT NOT NULL,
    FOREIGN KEY (banco_id) REFERENCES banco(id)
);