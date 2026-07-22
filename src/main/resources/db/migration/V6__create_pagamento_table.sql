CREATE TABLE pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_pagamento DATE NOT NULL,
    valor_final DECIMAL(15,2) NOT NULL,
    conta_bancaria_id BIGINT NOT NULL,
    FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria(id)
);