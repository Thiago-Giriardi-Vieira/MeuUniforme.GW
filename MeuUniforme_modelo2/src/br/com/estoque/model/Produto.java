package br.com.estoque.model;

public class Produto {
    private int idProduto;
    private String nome;            // tipo do uniforme (Camisa, Bermuda...)
    private String descricao;
    private String tamanho;
    private String cor;
    private double preco;
    private int quantidadeEstoque;
    private int estoqueMinimo;
    private Categoria categoria;
    private Fornecedor fornecedor;  // pode ser null
    private String observacoes;

    public Produto(int idProduto, String nome, String descricao, String tamanho,
                   String cor, double preco, int quantidadeEstoque,
                   int estoqueMinimo, Categoria categoria,
                   Fornecedor fornecedor, String observacoes) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.cor = cor;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.observacoes = observacoes;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return idProduto + " - " + nome + " | Estoque: " + quantidadeEstoque;
    }
}
