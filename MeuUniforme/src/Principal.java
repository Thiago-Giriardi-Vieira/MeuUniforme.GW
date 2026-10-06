public class Principal {
    public static void main(String[] args) {
        // 1. Cria o objeto em memória
        Categoria novaCategoria = new Categoria("Uniforme de Inverno");

        // 2. Chama o DAO para salvar no banco
        CategoriaDAO dao = new CategoriaDAO();
        dao.inserir(novaCategoria);
    }
}