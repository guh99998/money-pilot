ALTER TABLE parceiro ADD COLUMN tipo_documento VARCHAR(2) NULL;
UPDATE parceiro SET tipo_documento = CASE WHEN LENGTH(documento) = 14 THEN 'PJ' ELSE 'PF' END;
ALTER TABLE parceiro MODIFY tipo_documento VARCHAR(2) NOT NULL;

CREATE TABLE parceiro_pf (
    parceiro_id BIGINT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    FOREIGN KEY (parceiro_id) REFERENCES parceiro(id)
);

INSERT INTO parceiro_pf (parceiro_id, cpf)
SELECT id, documento FROM parceiro WHERE tipo_documento = 'PF';

CREATE TABLE parceiro_pj (
    parceiro_id BIGINT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    FOREIGN KEY (parceiro_id) REFERENCES parceiro(id)
);

INSERT INTO parceiro_pj (parceiro_id, cnpj)
SELECT id, documento FROM parceiro WHERE tipo_documento = 'PJ';

ALTER TABLE parceiro DROP COLUMN documento;