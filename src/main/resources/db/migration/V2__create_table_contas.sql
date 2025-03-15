CREATE TABLE contas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    data_pagamento DATE NULL,
    data_vencimento DATE NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    situacao VARCHAR(20) NOT NULL CHECK (situacao IN ('PENDENTE', 'PAGA', 'ATRASADA')),
    usuario_id UUID NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
