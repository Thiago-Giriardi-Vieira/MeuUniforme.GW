import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UniformeDAO {

    public void inserir(@org.jetbrains.annotations.NotNull Uniforme uniforme) {
        // Comando SQL com as 6 colunas que precisam de ser preenchidas (exclui o ID que é Auto Incremento)
        String sql = "INSERT INTO uniforme (descricao, tamanho, preco, quantidade_estoque, categoria_id, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Mapeamento dos atributos do objeto para as interrogações do SQL
            stmt.setString(1, uniforme.getDescricao());
            stmt.setString(2, uniforme.getTamanho());
            stmt.setDouble(3, uniforme.getPreco());
            stmt.setInt(4, uniforme.getQuantidadeEstoque());

            // Para as Foreign Keys, utilizamos os métodos get das IDs
            // Atenção: Certifique-se de que a Categoria e o Fornecedor já existem na base de dados!
            stmt.setInt(5, uniforme.getCategoriaId());
            stmt.setInt(6, uniforme.getFornecedorId());

            stmt.execute();
            System.out.println("Uniforme '" + uniforme.getDescricao() + "' guardado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir o uniforme na base de dados.", e);
        }
    }
}