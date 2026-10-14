package br.com.estoque.dao;

import br.com.estoque.model.Turma;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurmaDAO {

    public void inserir(Turma t) {
        String sql = "INSERT INTO turma (nome, ano_serie) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, t.getNome());
            stmt.setString(2, t.getAnoSerie());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) t.setIdTurma(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir a turma.", e);
        }
    }

    public Turma buscarPorId(int id) {
        String sql = "SELECT id_turma, nome, ano_serie FROM turma WHERE id_turma = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a turma.", e);
        }
    }

    public List<Turma> listar() {
        String sql = "SELECT id_turma, nome, ano_serie FROM turma ORDER BY nome";
        List<Turma> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar as turmas.", e);
        }
        return lista;
    }

    /** Quantidade de alunos de cada turma: id da turma -> total. */
    public Map<Integer, Integer> contarAlunosPorTurma() {
        String sql = "SELECT id_turma, COUNT(*) AS total FROM aluno WHERE id_turma IS NOT NULL GROUP BY id_turma";
        Map<Integer, Integer> mapa = new HashMap<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) mapa.put(rs.getInt("id_turma"), rs.getInt("total"));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar os alunos das turmas.", e);
        }
        return mapa;
    }

    public void atualizar(Turma t) {
        String sql = "UPDATE turma SET nome = ?, ano_serie = ? WHERE id_turma = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getNome());
            stmt.setString(2, t.getAnoSerie());
            stmt.setInt(3, t.getIdTurma());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a turma.", e);
        }
    }

    public void excluir(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM turma WHERE id_turma = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir a turma.", e);
        }
    }

    private Turma montar(ResultSet rs) throws SQLException {
        return new Turma(rs.getInt("id_turma"), rs.getString("nome"), rs.getString("ano_serie"));
    }
}
