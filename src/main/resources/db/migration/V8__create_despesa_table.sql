CREATE TABLE despesa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_despesa VARCHAR(100) NOT NULL,
    parceiro_id BIGINT NOT NULL,
    data_vencimento DATE NOT NULL,
    numero_parcelas INT NOT NULL,
    valor_parcelas DECIMAL(15,2) NOT NULL,
    categoria_id BIGINT NOT NULL,
    observacoes VARCHAR(255) NOT NULL,
    pagamento_id BIGINT NOT NULL,
    FOREIGN KEY (parceiro_id) REFERENCES parceiro(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (pagamento_id) REFERENCES pagamento(id)
);