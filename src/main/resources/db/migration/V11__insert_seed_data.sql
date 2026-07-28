-- Categorias
INSERT INTO categoria (nome, tag_cor, tipo_categoria) VALUES
('Vendas de Servico', '#2ECC71', 'RECEITA'),
('Consultoria', '#27AE60', 'RECEITA'),
('Aluguel', '#E74C3C', 'DESPESA'),
('Fornecedores', '#E67E22', 'DESPESA');

-- Enderecos
INSERT INTO endereco (rua, numero, bairro, cep, cidade, uf) VALUES
('Rua das Flores', '123', 'Centro', '01310100', 'Sao Paulo', 'SP'),
('Avenida Brasil', '456', 'Jardim America', '30140002', 'Belo Horizonte', 'MG'),
('Rua XV de Novembro', '789', 'Centro', '80020000', 'Curitiba', 'PR');

-- Bancos
INSERT INTO banco (codigo_banco, nome_banco) VALUES
('001', 'Banco do Brasil'),
('341', 'Itau Unibanco');

-- Conta bancaria
INSERT INTO conta_bancaria (nome_conta_bancaria, agencia, numero_conta, saldo_inicial, banco_id) VALUES
('Conta Corrente Principal', '1234', '00012345-6', 15000.00, 1),
('Conta Poupanca', '5678', '00098765-4', 5000.00, 2);

-- Parceiro (superclasse) - PF
INSERT INTO parceiro (nome, telefone, email, endereco_id, tipo_documento) VALUES
('Joao da Silva', '11987654321', 'joao.silva@email.com', 1, 'PF');

INSERT INTO parceiro_pf (parceiro_id, cpf) VALUES
(1, '12345678901');

INSERT INTO parceiro_tipo_parceiro (parceiro_id, tipo_parceiro) VALUES
(1, 'CLIENTE');

-- Parceiro (superclasse) - PJ
INSERT INTO parceiro (nome, telefone, email, endereco_id, tipo_documento) VALUES
('Fornecedora ABC Ltda', '3132221100', 'contato@fornecedoraabc.com', 2, 'PJ');

INSERT INTO parceiro_pj (parceiro_id, cnpj) VALUES
(2, '12345678000199');

INSERT INTO parceiro_tipo_parceiro (parceiro_id, tipo_parceiro) VALUES
(2, 'FORNECEDOR');

INSERT INTO parceiro (nome, telefone, email, endereco_id, tipo_documento) VALUES
('Consultoria XYZ S.A.', '4133445566', 'contato@consultoriaxyz.com', 3, 'PJ');

INSERT INTO parceiro_pj (parceiro_id, cnpj) VALUES
(3, '98765432000188');

INSERT INTO parceiro_tipo_parceiro (parceiro_id, tipo_parceiro) VALUES
(3, 'CLIENTE');

-- Pagamento
INSERT INTO pagamento (data_pagamento, valor_final, conta_bancaria_id) VALUES
('2026-07-05', 1200.00, 1);

-- Recebimento
INSERT INTO recebimento (data_recebimento, valor_final, conta_bancaria_id) VALUES
('2026-07-10', 3500.00, 1);

-- Despesa
INSERT INTO despesa (nome_despesa, parceiro_id, data_vencimento, numero_parcelas, valor_parcelas, categoria_id, observacoes, pagamento_id) VALUES
('Aluguel do escritorio - Julho', 2, '2026-07-05', 1, 1200.00, 3, 'Pagamento referente ao aluguel mensal', 1);

-- Receita
INSERT INTO receita (nome_receita, parceiro_id, data_vencimento, numero_parcelas, valor_parcela, categoria_id, observacoes, recebimento_id) VALUES
('Consultoria financeira - Julho', 3, '2026-07-10', 1, 3500.00, 2, 'Servico de consultoria prestado no mes de julho', 1);
