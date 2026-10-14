package br.com.estoque;

import br.com.estoque.dao.*;
import br.com.estoque.model.*;
import br.com.estoque.util.SenhaUtil;

import java.time.LocalDateTime;

/**
 * Teste rápido de ponta a ponta do banco, sem telas. Pode ser executado várias vezes
 * (os campos únicos recebem um sufixo para não repetir).
 * Para abrir o sistema de verdade, execute br.com.estoque.view.LoginTela.
 */
public class Principal {
    public static void main(String[] args) {
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        FornecedorDAO fornecedorDAO = new FornecedorDAO();
        TurmaDAO turmaDAO = new TurmaDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

        // Cadastros básicos
        Categoria camisas = new Categoria(0, "Camisas " + sufixo, "Camisas de uniforme");
        categoriaDAO.inserir(camisas);

        Fornecedor fornecedor = new Fornecedor(0, "Malharia Joinville " + sufixo, "00.000." + sufixo,
                "(47) 3000-0000", "contato@malharia.com");
        fornecedorDAO.inserir(fornecedor);

        Produto camisa = new Produto(0, "Camisa", "Camisa com logotipo, manga curta", "M", "Branca",
                45.50, 0, 10, camisas, fornecedor, "Produto de teste");
        produtoDAO.inserir(camisa);

        Turma turma = new Turma(0, "7º A " + sufixo, "7º ano");
        turmaDAO.inserir(turma);

        Aluno aluno = new Aluno(0, "Maria Silva", "MAT" + sufixo, turma, "Ana Silva", "(47) 99999-0000");
        alunoDAO.inserir(aluno);

        Usuario usuario = new Usuario(0, "Administrador", "admin" + sufixo, SenhaUtil.gerarHash("1234"), "ADMIN");
        usuarioDAO.inserir(usuario);

        // ENTRADA de 50 camisas
        Movimentacao entrada = new Movimentacao(0, LocalDateTime.now(), "ENTRADA",
                "Compra inicial", usuario, fornecedor, null);
        entrada.adicionarItem(new ItemMovimentacao(0, 50, 30.00, entrada, camisa));
        movimentacaoDAO.registrar(entrada);
        System.out.println("Entrada registrada. Estoque: " + produtoDAO.buscarPorId(camisa.getIdProduto()));

        // SAIDA de 8 camisas para o aluno
        Movimentacao saida = new Movimentacao(0, LocalDateTime.now(), "SAIDA",
                "Entrega ao aluno", usuario, null, aluno);
        saida.adicionarItem(new ItemMovimentacao(0, 8, 45.50, saida, camisa));
        movimentacaoDAO.registrar(saida);
        System.out.println("Saída registrada. Estoque: " + produtoDAO.buscarPorId(camisa.getIdProduto()));

        // SAIDA acima do saldo: deve ser recusada
        Movimentacao saidaInvalida = new Movimentacao(0, LocalDateTime.now(), "SAIDA",
                "Tentativa acima do saldo", usuario, null, aluno);
        saidaInvalida.adicionarItem(new ItemMovimentacao(0, 500, 45.50, saidaInvalida, camisa));
        try {
            movimentacaoDAO.registrar(saidaInvalida);
        } catch (IllegalStateException e) {
            System.out.println("Recusado como esperado: " + e.getMessage());
        }
        System.out.println("Estoque final: " + produtoDAO.buscarPorId(camisa.getIdProduto()));

        System.out.println("\nHistórico de movimentações:");
        for (Movimentacao m : movimentacaoDAO.listar()) {
            System.out.println(m + " (" + m.getItens().size() + " item(ns))");
        }
        System.out.println("\nAlunos da turma " + turma.getNome() + ": "
                + alunoDAO.listarPorTurma(turma.getIdTurma()).size());
        System.out.println("Produtos com estoque baixo: " + produtoDAO.listarEstoqueBaixo().size());
    }
}
