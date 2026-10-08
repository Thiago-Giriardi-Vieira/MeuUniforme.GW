public class Principal {
    public static void main(String[] args) {
        // Cria um uniforme associado à Categoria 1 e ao Fornecedor 1
        Uniforme novoUniforme = new Uniforme("Camisa com Logótipo", "M", 45.50, 100, 1, 1);

        UniformeDAO dao = new UniformeDAO();
        dao.inserir(novoUniforme);
    }
}