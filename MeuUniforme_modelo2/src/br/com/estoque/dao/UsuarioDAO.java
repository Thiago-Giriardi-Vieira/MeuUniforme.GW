package br.com.estoque.dao;

import br.com.estoque.model.Usuario;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String SELECT =
            "SELECT id_usuario, nome, login, senha, perfil FROM usuario ";

    public void inserir(Usuario u) {
        String sql = "INSERT INTO usuario (nome, login, senha, perfil) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getPerfil());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir o usuário (o login pode já existir).", e);
        }
    }

    public Usuario buscarPorId(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "WHERE id_usuario = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o usuário.", e);
        }
    }

    public Usuario buscarPorLogin(String login) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "WHERE login = ?")) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o usuário por login.", e);
        }
    }

    /** Usado na tela de login. Retorna null se login ou senha estiverem errados. */
    public Usuario autenticar(String login, String senha) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "WHERE login = ? AND senha = ?")) {
            stmt.setString(1, login);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar o usuário.", e);
        }
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os usuários.", e);
        }
        return lista;
    }

    public void atualizar(Usuario u) {
        String sql = "UPDATE usuario SET nome = ?, login = ?, senha = ?, perfil = ? WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getPerfil());
            stmt.setInt(5, u.getIdUsuario());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o usuário.", e);
        }
    }

    public void excluir(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir o usuário (ele pode ter movimentações registradas).", e);
        }
    }

    private Usuario montar(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nome"), rs.getString("login"),
                rs.getString("senha"), rs.getString("perfil"));
    }
}
