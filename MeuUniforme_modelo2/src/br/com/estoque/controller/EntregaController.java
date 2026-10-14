package br.com.estoque.controller;

import br.com.estoque.dao.AlunoDAO;
import br.com.estoque.dao.MovimentacaoDAO;
import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.dao.TurmaDAO;
import br.com.estoque.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras da entrega de uniformes: uma entrega é uma movimentação do tipo SAIDA,
 * ligada a um aluno, que baixa o estoque dos uniformes entregues.
 */
public class EntregaController {

    /** Uniforme e quantidade que estão na lista da entrega (ainda não gravados). */
    public static class ItemEntrega {
        private final Produto produto;
        private int quantidade;

        public ItemEntrega(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
        public void somar(int mais) { this.quantidade += mais; }
        public double getSubtotal() { return quantidade * produto.getPreco(); }
    }

    private final TurmaDAO turmaDAO = new TurmaDAO();
    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    public List<Turma> listarTurmas() {
        return turmaDAO.listar();
    }

    public List<Aluno> listarAlunosDaTurma(int idTurma) {
        return alunoDAO.listarPorTurma(idTurma);
    }

    /** Uniformes que ainda têm estoque para entregar. */
    public List<Produto> listarProdutosDisponiveis() {
        List<Produto> disponiveis = new ArrayList<>();
        for (Produto p : produtoDAO.listar()) {
            if (p.getQuantidadeEstoque() > 0) disponiveis.add(p);
        }
        return disponiveis;
    }

    /**
     * Confere se dá para adicionar a quantidade à lista.
     * @param jaNaLista quanto desse mesmo uniforme já está na lista da entrega
     */
    public void validarItem(Produto produto, int quantidade, int jaNaLista) {
        if (produto == null)
            throw new IllegalArgumentException("Selecione o uniforme que será entregue.");
        if (quantidade <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (quantidade + jaNaLista > produto.getQuantidadeEstoque())
            throw new IllegalArgumentException("Estoque insuficiente de " + produto.getNome() + " ("
                    + produto.getTamanho() + "): disponível " + produto.getQuantidadeEstoque()
                    + (jaNaLista > 0 ? ", já na lista " + jaNaLista : "") + ".");
    }

    /**
     * Registra a entrega (SAIDA) e baixa o estoque, tudo numa transação.
     * Se algum item não tiver saldo no momento de gravar, nada é salvo.
     */
    public Movimentacao entregar(Usuario usuario, Aluno aluno, List<ItemEntrega> itens, String observacao) {
        if (usuario == null)
            throw new IllegalArgumentException("Usuário não identificado. Entre novamente no sistema.");
        if (aluno == null)
            throw new IllegalArgumentException("Selecione o aluno que vai receber os uniformes.");
        if (itens == null || itens.isEmpty())
            throw new IllegalArgumentException("Adicione ao menos um uniforme à entrega.");

        String obs = (observacao == null || observacao.isBlank())
                ? "Entrega de uniformes - " + aluno.getNome()
                : observacao.trim();
        if (obs.length() > 255) obs = obs.substring(0, 255);

        Movimentacao mov = new Movimentacao(0, LocalDateTime.now(), "SAIDA", obs, usuario, null, aluno);
        for (ItemEntrega i : itens) {
            mov.adicionarItem(new ItemMovimentacao(0, i.getQuantidade(), i.getProduto().getPreco(),
                    mov, i.getProduto()));
        }
        movimentacaoDAO.registrar(mov);
        return mov;
    }
}
