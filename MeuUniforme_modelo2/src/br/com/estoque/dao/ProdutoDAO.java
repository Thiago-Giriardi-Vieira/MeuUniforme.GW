package br.com.estoque.dao;

import br.com.estoque.model.Categoria;
import br.com.estoque.model.Fornecedor;
import br.com.estoque.model.Produto;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private static final String SELECT =
            "SELECT p.id_produto, p.nome, p.descricao, p.tamanho, p.cor, p.preco, " +
            "p.quantidade_estoque, p.estoque_minimo, p.observacoes, " +
            "c.id_categoria, c.nome AS cat_nome, c.descricao AS cat_descricao, " +
            "f.id_fornecedor, f.razao_social, f.cnpj, f.telefone AS forn_telefone, f.email AS forn_email " +
            "FROM produto p " +
            "JOIN categoria c ON c.id_categoria = p.id_categoria " +
            "LEFT JOIN fornecedor f ON f.id_fornecedor = p.id_fornecedor ";

    public void inserir(Produto p) {
        String sql = "INSERT INTO produto (nome, descricao, tamanho, cor, preco, quantidade_estoque, " +
                     "estoque_minimo, observacoes, id_categoria, id_fornecedor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, p);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) p.setIdProduto(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir o produto (confira se a categoria e o fornecedor existem).", e);
        }
    }

    public Produto buscarPorId(int id) {
        List<Produto> r = consultar(SELECT + "WHERE p.id_produto = ?", id);
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Produto> listar() {
        return consultar(SELECT + "ORDER BY p.nome, p.tamanho");
    }

    /** Produtos com estoque igual ou abaixo do mínimo. */
    public List<Produto> listarEstoqueBaixo() {
        return consultar(SELECT + "WHERE p.quantidade_estoque <= p.estoque_minimo ORDER BY p.nome, p.tamanho");
    }

    /**
     * Busca com filtros opcionais (qualquer um pode ser null/vazio):
     * texto (no tipo ou na descrição), categoria e tamanho.
     */
    public List<Produto> buscar(String texto, Integer idCategoria, String tamanho) {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            sql.append("AND (p.nome LIKE ? OR p.descricao LIKE ?) ");
            String like = "%" + texto.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (idCategoria != null) {
            sql.append("AND p.id_categoria = ? ");
            params.add(idCategoria);
        }
        if (tamanho != null && !tamanho.isBlank()) {
            sql.append("AND p.tamanho = ? ");
            params.add(tamanho);
        }
        sql.append("ORDER BY p.nome, p.tamanho");
        return consultar(sql.toString(), params.toArray());
    }

    /** Atualiza o cadastro completo, inclusive a quantidade em estoque. */
    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, tamanho = ?, cor = ?, preco = ?, " +
                     "quantidade_estoque = ?, estoque_minimo = ?, observacoes = ?, id_categoria = ?, " +
                     "id_fornecedor = ? WHERE id_produto = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, p);
            stmt.setInt(11, p.getIdProduto());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o produto.", e);
        }
    }

    public void excluir(int id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM produto WHERE id_produto = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir o produto (ele pode ter movimentações registradas).", e);
        }
    }

    // ---------------------------------------------------------------- auxiliares

    private void preencher(PreparedStatement stmt, Produto p) throws SQLException {
        stmt.setString(1, p.getNome());
        stmt.setString(2, p.getDescricao());
        stmt.setString(3, p.getTamanho());
        stmt.setString(4, p.getCor());
        stmt.setDouble(5, p.getPreco());
        stmt.setInt(6, p.getQuantidadeEstoque());
        stmt.setInt(7, p.getEstoqueMinimo());
        stmt.setString(8, p.getObservacoes());
        stmt.setInt(9, p.getCategoria().getIdCategoria());
        if (p.getFornecedor() != null) stmt.setInt(10, p.getFornecedor().getIdFornecedor());
        else stmt.setNull(10, Types.INTEGER);
    }

    private List<Produto> consultar(String sql, Object... parametros) {
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) stmt.setObject(i + 1, parametros[i]);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar os produtos.", e);
        }
        return lista;
    }

    private Produto montar(ResultSet rs) throws SQLException {
        Categoria cat = new Categoria(rs.getInt("id_categoria"),
                rs.getString("cat_nome"), rs.getString("cat_descricao"));
        int idFornecedor = rs.getInt("id_fornecedor");
        Fornecedor forn = rs.wasNull() ? null
                : new Fornecedor(idFornecedor, rs.getString("razao_social"), rs.getString("cnpj"),
                        rs.getString("forn_telefone"), rs.getString("forn_email"));
        return new Produto(rs.getInt("id_produto"), rs.getString("nome"), rs.getString("descricao"),
                rs.getString("tamanho"), rs.getString("cor"), rs.getDouble("preco"),
                rs.getInt("quantidade_estoque"), rs.getInt("estoque_minimo"), cat, forn,
                rs.getString("observacoes"));
    }
}
