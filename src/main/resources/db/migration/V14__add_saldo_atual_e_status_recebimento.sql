ALTER TABLE conta_bancaria ADD COLUMN saldo_atual DECIMAL(15,2);
UPDATE conta_bancaria SET saldo_atual = saldo_inicial WHERE saldo_atual IS NULL;
ALTER TABLE conta_bancaria MODIFY COLUMN saldo_atual DECIMAL(15,2) NOT NULL;

ALTER TABLE recebimento ADD COLUMN status VARCHAR(20);
UPDATE recebimento SET status = 'CONFIRMADO' WHERE status IS NULL;
ALTER TABLE recebimento MODIFY COLUMN status VARCHAR(20) NOT NULL;
