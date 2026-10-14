package br.com.estoque.model;

public class ItemMovimentacao {
    private int idItem;
    private int quantidade;
    private double precoUnitario;
    private Movimentacao movimentacao;
    private Produto produto;

    public ItemMovimentacao(int idItem, int quantidade, double precoUnitario,
                            Movimentacao movimentacao, Produto produto) {
        this.idItem = idItem;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.movimentacao = movimentacao;
        this.produto = produto;
    }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public Movimentacao getMovimentacao() { return movimentacao; }
    public void setMovimentacao(Movimentacao movimentacao) { this.movimentacao = movimentacao; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public double getSubtotal() {
        return quantidade * precoUnitario;
    }
}
