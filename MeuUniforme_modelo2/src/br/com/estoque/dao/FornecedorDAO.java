package br.com.estoque.dao;

import br.com.estoque.model.Fornecedor;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    private static final String SELECT =
            "SELECT id_fornecedor, razao_social, cnpj, telefone, email FROM fornecedor ";

    public void inserir(Fornecedor f) {
        String sql = "INSERT INTO fornecedor (razao_social, cnpj, telefone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, f.getRazaoSocial());
            stmt.setString(2, f.getCnpj());
            stmt.setString(3, f.getTelefone());
            stmt.setString(4, f.getEmail());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) f.setIdFornecedor(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir o fornecedor (o CNPJ pode já estar cadastrado).", e);
        }
    }

    public Fornecedor buscarPorId(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "WHERE id_fornecedor = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o fornecedor.", e);
        }
    }

    public List<Fornecedor> listar() {
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "ORDER BY razao_social");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os fornecedores.", e);
        }
        return lista;
    }

    public void atualizar(Fornecedor f) {
        String sql = "UPDATE fornecedor SET razao_social = ?, cnpj = ?, telefone = ?, email = ? WHERE id_fornecedor = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getRazaoSocial());
            stmt.setString(2, f.getCnpj());
            stmt.setString(3, f.getTelefone());
            stmt.setString(4, f.getEmail());
            stmt.setInt(5, f.getIdFornecedor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o fornecedor.", e);
        }
    }

    public void excluir(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM fornecedor WHERE id_fornecedor = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir o fornecedor (ele pode ter movimentações registradas).", e);
        }
    }

    private Fornecedor montar(ResultSet rs) throws SQLException {
        return new Fornecedor(rs.getInt("id_fornecedor"), rs.getString("razao_social"),
                rs.getString("cnpj"), rs.getString("telefone"), rs.getString("email"));
    }
}
