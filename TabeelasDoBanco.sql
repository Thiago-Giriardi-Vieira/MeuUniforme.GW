USE meuuniformegw;

CREATE TABLE categoria (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(100) NOT NULL
);

CREATE TABLE fornecedor (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nome_fantasia VARCHAR(150) NOT NULL,
                            cnpj VARCHAR(20) UNIQUE NOT NULL,
                            telefone VARCHAR(20)
);

CREATE TABLE usuario (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         login VARCHAR(50) UNIQUE NOT NULL,
                         senha VARCHAR(255) NOT NULL,
                         nivel_acesso VARCHAR(20) NOT NULL
);

CREATE TABLE uniforme (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          descricao VARCHAR(150) NOT NULL,
                          tamanho VARCHAR(10) NOT NULL,
                          preco DECIMAL(10, 2) NOT NULL,
                          quantidade_estoque INT DEFAULT 0,
                          categoria_id INT,
                          fornecedor_id INT,
                          FOREIGN KEY (categoria_id) REFERENCES categoria(id),
                          FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);

CREATE TABLE movimentacao (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              uniforme_id INT NOT NULL,
                              usuario_id INT NOT NULL,
                              tipo VARCHAR(10) NOT NULL, -- 'ENTRADA' ou 'SAIDA'
                              quantidade INT NOT NULL,
                              data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (uniforme_id) REFERENCES uniforme(id),
                              FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
