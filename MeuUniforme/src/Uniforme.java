public class Uniforme {
    private int id;
    private String descricao;
    private String tamanho;
    private double preco;
    private int quantidadeEstoque;
    private int categoriaId;
    private int fornecedorId;

    public Uniforme() {}

    public Uniforme(String descricao, String tamanho, double preco, int quantidadeEstoque, int categoriaId, int fornecedorId) {
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoriaId = categoriaId;
        this.fornecedorId = fornecedorId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }
}