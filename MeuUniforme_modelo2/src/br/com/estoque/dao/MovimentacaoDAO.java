package br.com.estoque.dao;

import br.com.estoque.model.*;
import br.com.estoque.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    /**
     * Registra a movimentação, seus itens e atualiza o estoque, tudo em UMA transação:
     * se qualquer item falhar (ex.: saldo insuficiente), nada é gravado.
     *
     * Regras:
     *  - ENTRADA exige fornecedor; soma a quantidade ao estoque.
     *  - SAIDA exige aluno; só baixa o estoque se houver saldo suficiente.
     *
     * @throws IllegalArgumentException se os dados forem inválidos
     * @throws IllegalStateException    se não houver saldo suficiente em uma SAIDA
     */
    public void registrar(Movimentacao mov) {
        validar(mov);

        String sqlMov = "INSERT INTO movimentacao (data_hora, tipo, observacao, id_usuario, id_fornecedor, id_aluno) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_movimentacao (quantidade, preco_unitario, id_movimentacao, id_produto) " +
                         "VALUES (?, ?, ?, ?)";
        String sqlEntrada = "UPDATE produto SET quantidade_estoque = quantidade_estoque + ? WHERE id_produto = ?";
        // A condição "quantidade_estoque >= ?" garante a checagem de saldo de forma atômica
        String sqlSaida = "UPDATE produto SET quantidade_estoque = quantidade_estoque - ? " +
                          "WHERE id_produto = ? AND quantidade_estoque >= ?";

        boolean entrada = "ENTRADA".equals(mov.getTipo());

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (mov.getDataHora() == null) mov.setDataHora(LocalDateTime.now());

                try (PreparedStatement stmt = conn.prepareStatement(sqlMov, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(mov.getDataHora()));
                    stmt.setString(2, mov.getTipo());
                    stmt.setString(3, mov.getObservacao());
                    stmt.setInt(4, mov.getUsuario().getIdUsuario());
                    if (mov.getFornecedor() != null) stmt.setInt(5, mov.getFornecedor().getIdFornecedor());
                    else stmt.setNull(5, Types.INTEGER);
                    if (mov.getAluno() != null) stmt.setInt(6, mov.getAluno().getIdAluno());
                    else stmt.setNull(6, Types.INTEGER);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) mov.setIdMovimentacao(rs.getInt(1));
                    }
                }

                for (ItemMovimentacao item : mov.getItens()) {
                    // 1) atualiza o estoque
                    try (PreparedStatement stmt = conn.prepareStatement(entrada ? sqlEntrada : sqlSaida)) {
                        stmt.setInt(1, item.getQuantidade());
                        stmt.setInt(2, item.getProduto().getIdProduto());
                        if (!entrada) stmt.setInt(3, item.getQuantidade());
                        int afetadas = stmt.executeUpdate();
                        if (afetadas == 0) {
                            throw new IllegalStateException(entrada
                                    ? "Produto não encontrado: " + item.getProduto().getNome()
                                    : "Saldo insuficiente em estoque para: " + item.getProduto().getNome());
                        }
                    }
                    // 2) grava o item
                    try (PreparedStatement stmt = conn.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setInt(1, item.getQuantidade());
                        stmt.setDouble(2, item.getPrecoUnitario());
                        stmt.setInt(3, mov.getIdMovimentacao());
                        stmt.setInt(4, item.getProduto().getIdProduto());
                        stmt.executeUpdate();
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) item.setIdItem(rs.getInt(1));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar a movimentação.", e);
        }
    }

    public List<Movimentacao> listar() {
        String sql = "SELECT id_movimentacao, data_hora, tipo, observacao, id_usuario, id_fornecedor, id_aluno " +
                     "FROM movimentacao ORDER BY data_hora DESC, id_movimentacao DESC";
        List<Movimentacao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar as movimentações.", e);
        }
        return lista;
    }

    public Movimentacao buscarPorId(int id) {
        String sql = "SELECT id_movimentacao, data_hora, tipo, observacao, id_usuario, id_fornecedor, id_aluno " +
                     "FROM movimentacao WHERE id_movimentacao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a movimentação.", e);
        }
    }

    // ---------------------------------------------------------------- auxiliares

    private void validar(Movimentacao mov) {
        if (mov == null) throw new IllegalArgumentException("Movimentação não pode ser nula.");
        String tipo = mov.getTipo();
        if (!"ENTRADA".equals(tipo) && !"SAIDA".equals(tipo))
            throw new IllegalArgumentException("Tipo deve ser ENTRADA ou SAIDA.");
        if (mov.getUsuario() == null)
            throw new IllegalArgumentException("Informe o usuário responsável pela movimentação.");
        if ("ENTRADA".equals(tipo) && mov.getFornecedor() == null)
            throw new IllegalArgumentException("Uma ENTRADA exige um fornecedor.");
        if ("SAIDA".equals(tipo) && mov.getAluno() == null)
            throw new IllegalArgumentException("Uma SAIDA exige um aluno.");
        if (mov.getItens().isEmpty())
            throw new IllegalArgumentException("A movimentação precisa ter pelo menos um item.");
        for (ItemMovimentacao item : mov.getItens()) {
            if (item.getProduto() == null)
                throw new IllegalArgumentException("Item sem produto.");
            if (item.getQuantidade() <= 0)
                throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }
    }

    private Movimentacao montar(ResultSet rs) throws SQLException {
        int idFornecedor = rs.getInt("id_fornecedor");
        Fornecedor fornecedor = rs.wasNull() ? null : fornecedorDAO.buscarPorId(idFornecedor);
        int idAluno = rs.getInt("id_aluno");
        Aluno aluno = rs.wasNull() ? null : alunoDAO.buscarPorId(idAluno);

        Movimentacao mov = new Movimentacao(
                rs.getInt("id_movimentacao"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("tipo"),
                rs.getString("observacao"),
                usuarioDAO.buscarPorId(rs.getInt("id_usuario")),
                fornecedor,
                aluno);
        carregarItens(mov);
        return mov;
    }

    private void carregarItens(Movimentacao mov) {
        String sql = "SELECT id_item, quantidade, preco_unitario, id_produto " +
                     "FROM item_movimentacao WHERE id_movimentacao = ? ORDER BY id_item";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mov.getIdMovimentacao());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = produtoDAO.buscarPorId(rs.getInt("id_produto"));
                    mov.adicionarItem(new ItemMovimentacao(rs.getInt("id_item"), rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario"), mov, produto));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar os itens da movimentação.", e);
        }
    }
}
