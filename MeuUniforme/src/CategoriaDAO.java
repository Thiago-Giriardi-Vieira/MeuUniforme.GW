import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoriaDAO {

    public void inserir(Categoria categoria) {
        // O comando SQL paramétrico (evita ataques de SQL Injection)
        String sql = "INSERT INTO categoria (nome) VALUES (?)";

        // Abre a conexão utilizando a sua ConnectionFactory
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Substitui o "?" pelo nome da categoria
            stmt.setString(1, categoria.getNome());

            // Executa o comando no MySQL
            stmt.execute();
            System.out.println("Categoria '" + categoria.getNome() + "' salva com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir a categoria no banco de dados.", e);
        }
    }
}