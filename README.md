# MeuUniforme.GW
Sistema de Controle de Estoque de Uniformes Escolares

Trabalho de Programação Orientada a Objetos (POO)

Este repositório contém os artefatos e o código-fonte do Sistema de Controle de Estoque de Uniformes Escolares, desenvolvido como requisito de avaliação da disciplina de POO. O projeto aplica conceitos de orientação a objetos, persistência de dados e o padrão de arquitetura MVC.
1ª Etapa: Definição e Modelagem

1. Tema do Sistema

O sistema visa gerenciar o inventário de uniformes de uma instituição de ensino. Ele permite o controle rigoroso das peças (tamanho, categoria, fornecedor), registro de entradas e saídas, e monitoramento da quantidade em estoque para evitar faltas durante os períodos de pico escolar.

2. Padrão de Projeto MVC

O sistema será estruturado no padrão Model-View-Controller (MVC):

Model: Gerencia as regras de negócio e a comunicação com o banco de dados (ex: verificar se há saldo suficiente no estoque antes de registrar uma saída).

View: Interfaces gráficas (Telas de Login, Dashboard, Cadastros e Movimentações) focadas apenas na interação com o usuário.

Controller: Intermediário que recebe as requisições da View, aciona as operações no Model e atualiza a View com os resultados.

3. Protótipos de Tela

As interfaces foram projetadas para serem intuitivas, contemplando múltiplas telas (Login, Dashboard, Cadastro de Itens, Registro de Entradas/Saídas).
🔗 Clique aqui para acessar o protótipo interativo (Figma/Balsamiq/etc) (Substitua a URL pelo link do protótipo)

4. Diagrama de Classes

O diagrama abaixo representa a estrutura orientada a objetos (Entidades do Model), definindo atributos, métodos e associações.


(Coloque a imagem do diagrama na pasta do repositório e ajuste o link acima)

5. Diagrama MER (Modelo Entidade-Relacionamento)

A modelagem do banco de dados contempla 5 tabelas interligadas, superando o requisito mínimo do projeto:

Usuario

Fornecedor

Categoria

Uniforme

Movimentacao