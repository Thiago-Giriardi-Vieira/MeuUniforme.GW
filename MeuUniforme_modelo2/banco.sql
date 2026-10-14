-- =====================================================================
-- MeuUniforme.GW - Script do banco (Modelo 2, versão com Turma)
-- ATENCAO: este script APAGA as tabelas antigas e todos os dados delas.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS meuuniformegw
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meuuniformegw;

-- Remove as tabelas (filhas primeiro, por causa das chaves estrangeiras)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS item_movimentacao;
DROP TABLE IF EXISTS movimentacao;
DROP TABLE IF EXISTS uniforme;      -- tabela do modelo antigo
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS aluno;
DROP TABLE IF EXISTS turma;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS fornecedor;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    login      VARCHAR(50)  NOT NULL UNIQUE,
    senha      VARCHAR(255) NOT NULL,            -- guarda "salt:hash" (nunca a senha pura)
    perfil     VARCHAR(20)  NOT NULL             -- ADMIN ou OPERADOR
);

CREATE TABLE fornecedor (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    razao_social  VARCHAR(150) NOT NULL,
    cnpj          VARCHAR(20)  NOT NULL UNIQUE,
    telefone      VARCHAR(20),
    email         VARCHAR(100)
);

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    descricao    VARCHAR(255)
);

CREATE TABLE turma (
    id_turma  INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(50) NOT NULL,              -- ex.: 7º A
    ano_serie VARCHAR(50)                        -- ex.: 7º ano
);

CREATE TABLE aluno (
    id_aluno         INT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    matricula        VARCHAR(30)  NOT NULL UNIQUE,
    nome_responsavel VARCHAR(100),
    telefone         VARCHAR(20),
    id_turma         INT NULL,
    FOREIGN KEY (id_turma) REFERENCES turma(id_turma)
);

CREATE TABLE produto (
    id_produto         INT AUTO_INCREMENT PRIMARY KEY,
    nome               VARCHAR(100) NOT NULL,    -- tipo do uniforme (Camisa, Bermuda...)
    descricao          VARCHAR(255),
    tamanho            VARCHAR(10)  NOT NULL,
    cor                VARCHAR(30),
    preco              DECIMAL(10, 2) NOT NULL CHECK (preco >= 0),
    quantidade_estoque INT NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    estoque_minimo     INT NOT NULL DEFAULT 0 CHECK (estoque_minimo >= 0),
    observacoes        VARCHAR(500),
    id_categoria       INT NOT NULL,
    id_fornecedor      INT NULL,
    FOREIGN KEY (id_categoria)  REFERENCES categoria(id_categoria),
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(id_fornecedor)
);

-- Cabecalho da movimentacao:
--   ENTRADA -> vem de um fornecedor (id_aluno fica NULL)
--   SAIDA   -> vai para um aluno    (id_fornecedor fica NULL)
CREATE TABLE movimentacao (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    data_hora       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo            VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    observacao      VARCHAR(255),
    id_usuario      INT NOT NULL,
    id_fornecedor   INT NULL,
    id_aluno        INT NULL,
    FOREIGN KEY (id_usuario)    REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(id_fornecedor),
    FOREIGN KEY (id_aluno)      REFERENCES aluno(id_aluno)
);

-- Itens de cada movimentacao (uma movimentacao pode ter varios produtos)
CREATE TABLE item_movimentacao (
    id_item         INT AUTO_INCREMENT PRIMARY KEY,
    quantidade      INT NOT NULL CHECK (quantidade > 0),
    preco_unitario  DECIMAL(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    id_movimentacao INT NOT NULL,
    id_produto      INT NOT NULL,
    FOREIGN KEY (id_movimentacao) REFERENCES movimentacao(id_movimentacao) ON DELETE CASCADE,
    FOREIGN KEY (id_produto)      REFERENCES produto(id_produto)
);
