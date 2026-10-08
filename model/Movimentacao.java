package br.com.estoque.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Movimentacao {
    private int idMovimentacao;
    private LocalDateTime dataHora;
    private String tipo; // ENTRADA / SAIDA
    private String observacao;
    private Usuario usuario;
    private Fornecedor fornecedor;
    private Aluno aluno;
    private final List<ItemMovimentacao> itens = new ArrayList<>();

    public Movimentacao(int idMovimentacao, LocalDateTime dataHora, String tipo,
                        String observacao, Usuario usuario,
                        Fornecedor fornecedor, Aluno aluno) {
        this.idMovimentacao = idMovimentacao;
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.observacao = observacao;
        this.usuario = usuario;
        this.fornecedor = fornecedor;
        this.aluno = aluno;
    }

    public int getIdMovimentacao() { return idMovimentacao; }
    public void setIdMovimentacao(int idMovimentacao) { this.idMovimentacao = idMovimentacao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public List<ItemMovimentacao> getItens() { return itens; }

    public void adicionarItem(ItemMovimentacao item) {
        if (item == null) throw new IllegalArgumentException("Item não pode ser nulo.");
        item.setMovimentacao(this);
        itens.add(item);
    }

    @Override
    public String toString() {
        return idMovimentacao + " - " + tipo + " - " + dataHora;
    }
}
