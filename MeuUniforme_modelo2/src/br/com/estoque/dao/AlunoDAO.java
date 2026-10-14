package br.com.estoque.dao;

import br.com.estoque.model.Aluno;
import br.com.estoque.model.Turma;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private static final String SELECT =
            "SELECT a.id_aluno, a.nome, a.matricula, a.nome_responsavel, a.telefone, " +
            "t.id_turma, t.nome AS turma_nome, t.ano_serie " +
            "FROM aluno a LEFT JOIN turma t ON t.id_turma = a.id_turma ";

    public void inserir(Aluno a) {
        String sql = "INSERT INTO aluno (nome, matricula, nome_responsavel, telefone, id_turma) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, a);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) a.setIdAluno(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir o aluno (a matrícula pode já estar cadastrada).", e);
        }
    }

    public Aluno buscarPorId(int id) {
        return buscarUm(SELECT + "WHERE a.id_aluno = ?", id);
    }

    public Aluno buscarPorMatricula(String matricula) {
        return buscarUm(SELECT + "WHERE a.matricula = ?", matricula);
    }

    public List<Aluno> listar() {
        return consultar(SELECT + "ORDER BY a.nome");
    }

    public List<Aluno> listarPorTurma(int idTurma) {
        return consultar(SELECT + "WHERE a.id_turma = ? ORDER BY a.nome", idTurma);
    }

    public void atualizar(Aluno a) {
        String sql = "UPDATE aluno SET nome = ?, matricula = ?, nome_responsavel = ?, telefone = ?, id_turma = ? " +
                     "WHERE id_aluno = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, a);
            stmt.setInt(6, a.getIdAluno());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o aluno.", e);
        }
    }

    public void excluir(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM aluno WHERE id_aluno = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir o aluno (ele pode ter movimentações registradas).", e);
        }
    }

    // ---------------------------------------------------------------- auxiliares

    private void preencher(PreparedStatement stmt, Aluno a) throws SQLException {
        stmt.setString(1, a.getNome());
        stmt.setString(2, a.getMatricula());
        stmt.setString(3, a.getNomeResponsavel());
        stmt.setString(4, a.getTelefone());
        if (a.getTurma() != null) stmt.setInt(5, a.getTurma().getIdTurma());
        else stmt.setNull(5, Types.INTEGER);
    }

    private Aluno buscarUm(String sql, Object parametro) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o aluno.", e);
        }
    }

    private List<Aluno> consultar(String sql, Object... parametros) {
        List<Aluno> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) stmt.setObject(i + 1, parametros[i]);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os alunos.", e);
        }
        return lista;
    }

    private Aluno montar(ResultSet rs) throws SQLException {
        int idTurma = rs.getInt("id_turma");
        Turma turma = rs.wasNull() ? null
                : new Turma(idTurma, rs.getString("turma_nome"), rs.getString("ano_serie"));
        return new Aluno(rs.getInt("id_aluno"), rs.getString("nome"), rs.getString("matricula"),
                turma, rs.getString("nome_responsavel"), rs.getString("telefone"));
    }
}
