package br.com.estoque.view;

import br.com.estoque.controller.ProdutoController;
import br.com.estoque.model.Categoria;
import br.com.estoque.model.Produto;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela "Estoque de uniforme": busca, filtros por categoria e tamanho,
 * tabela paginada e botões para cadastrar, editar e excluir.
 * Linhas em vermelho claro = estoque igual ou abaixo do mínimo.
 */
public class EstoquePainel extends JPanel {

    private static final int POR_PAGINA = 10;
    private static final String TODAS_CATEGORIAS = "Todas as Categorias";
    private static final String TODOS_TAMANHOS = "Todos os tamanhos";
    private static final String[] COLUNAS =
            {"Uniforme", "Descrição", "Tamanho", "Categoria", "Fornecedor", "Preço", "Estoque", "Mínimo"};

    private final Navegacao nav;
    private final ProdutoController controller = new ProdutoController();

    private final JTextField txtBusca = Estilo.campoComDica("Pesquisar uniforme...", 20);
    private final JComboBox<Object> cmbCategoria = Estilo.combo(new JComboBox<Object>());
    private final JComboBox<String> cmbTamanho = Estilo.combo(new JComboBox<String>());
    private final ListaTableModel<Produto> modelo = new ListaTableModel<>(COLUNAS, EstoquePainel::valorCelula);
    private final JTable tabela;
    private final JPanel paginacao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

    private List<Produto> filtrados = new ArrayList<>();
    private int pagina = 1;

    public EstoquePainel(Navegacao nav) {
        this.nav = nav;
        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        // topo: seta + título
        JButton voltar = Estilo.botaoVoltar();
        voltar.addActionListener(e -> nav.irParaMenu());
        JLabel titulo = new JLabel("Estoque de uniforme");
        titulo.setFont(Estilo.fonte(Font.BOLD, 14f));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);
        topo.add(voltar);
        topo.add(titulo);
        add(topo, BorderLayout.NORTH);

        // filtros
        cmbTamanho.addItem(TODOS_TAMANHOS);
        for (String t : ProdutoController.TAMANHOS) cmbTamanho.addItem(t);
        JButton btnFiltrar = Estilo.botaoPrimario("FILTRAR");
        btnFiltrar.addActionListener(e -> filtrar());
        txtBusca.addActionListener(e -> filtrar());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        filtros.add(txtBusca);
        filtros.add(cmbCategoria);
        filtros.add(cmbTamanho);
        filtros.add(btnFiltrar);

        // tabela
        tabela = new JTable(modelo) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    Produto p = modelo.getItem(row);
                    c.setBackground(p.getQuantidadeEstoque() <= p.getEstoqueMinimo()
                            ? Estilo.ESTOQUE_BAIXO : getBackground());
                }
                return c;
            }
        };
        tabela.setRowHeight(26);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editar();
            }
        });

        // rodapé: ações à esquerda, paginação à direita
        JButton btnNovo = Estilo.botaoVerde("+ NOVO UNIFORME");
        JButton btnEditar = Estilo.botaoSecundario("Editar");
        JButton btnExcluir = Estilo.botaoSecundario("Excluir");
        btnNovo.addActionListener(e -> nav.irParaFormUniforme(null));
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setOpaque(false);
        acoes.add(btnNovo);
        acoes.add(btnEditar);
        acoes.add(btnExcluir);
        paginacao.setOpaque(false);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(acoes, BorderLayout.WEST);
        rodape.add(paginacao, BorderLayout.EAST);

        JPanel cartao = new JPanel(new BorderLayout(0, 8));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        cartao.add(filtros, BorderLayout.NORTH);
        cartao.add(new JScrollPane(tabela), BorderLayout.CENTER);
        cartao.add(rodape, BorderLayout.SOUTH);
        add(cartao, BorderLayout.CENTER);

        cmbCategoria.addItem(TODAS_CATEGORIAS); // as categorias reais são carregadas em aoExibir()
        montarPaginacao(1);
    }

    private static Object valorCelula(Produto p, int coluna) {
        switch (coluna) {
            case 0: return p.getNome();
            case 1: return p.getDescricao();
            case 2: return p.getTamanho();
            case 3: return p.getCategoria().getNome();
            case 4: return p.getFornecedor() == null ? "-" : p.getFornecedor().getRazaoSocial();
            case 5: return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", p.getPreco());
            case 6: return p.getQuantidadeEstoque();
            case 7: return p.getEstoqueMinimo();
            default: return null;
        }
    }

    /** Chamado toda vez que a tela é exibida. */
    public void aoExibir() {
        Object atual = cmbCategoria.getSelectedItem();
        carregarCategorias(atual instanceof Categoria ? ((Categoria) atual).getIdCategoria() : null);
        filtrar();
    }

    private void carregarCategorias(Integer idSelecionado) {
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem(TODAS_CATEGORIAS);
        try {
            for (Categoria c : controller.listarCategorias()) {
                cmbCategoria.addItem(c);
                if (idSelecionado != null && c.getIdCategoria() == idSelecionado) cmbCategoria.setSelectedItem(c);
            }
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void filtrar() {
        try {
            Object cat = cmbCategoria.getSelectedItem();
            Integer idCategoria = cat instanceof Categoria ? ((Categoria) cat).getIdCategoria() : null;
            String tamanho = cmbTamanho.getSelectedIndex() > 0 ? (String) cmbTamanho.getSelectedItem() : null;
            filtrados = controller.buscar(txtBusca.getText(), idCategoria, tamanho);
            pagina = 1;
            mostrarPagina();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void mostrarPagina() {
        int total = Math.max(1, (int) Math.ceil(filtrados.size() / (double) POR_PAGINA));
        pagina = Math.max(1, Math.min(pagina, total));
        int ini = (pagina - 1) * POR_PAGINA;
        int fim = Math.min(ini + POR_PAGINA, filtrados.size());
        modelo.setItens(filtrados.subList(ini, fim));
        montarPaginacao(total);
    }

    private void montarPaginacao(int total) {
        paginacao.removeAll();
        JButton anterior = Estilo.botaoPagina("\u2039", false);
        anterior.setEnabled(pagina > 1);
        anterior.addActionListener(e -> irParaPagina(pagina - 1));
        paginacao.add(anterior);

        int fim = Math.min(total, Math.max(1, pagina - 2) + 4);
        int inicio = Math.max(1, fim - 4);
        for (int p = inicio; p <= fim; p++) {
            final int destino = p;
            JButton b = Estilo.botaoPagina(String.valueOf(p), p == pagina);
            b.addActionListener(e -> irParaPagina(destino));
            paginacao.add(b);
        }

        JButton proxima = Estilo.botaoPagina("\u203A", false);
        proxima.setEnabled(pagina < total);
        proxima.addActionListener(e -> irParaPagina(pagina + 1));
        paginacao.add(proxima);
        paginacao.revalidate();
        paginacao.repaint();
    }

    private void irParaPagina(int destino) {
        pagina = destino;
        mostrarPagina();
    }

    private Produto selecionado(String acao) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um uniforme na tabela para " + acao + ".",
                    "Nenhum uniforme selecionado", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.getItem(linha);
    }

    private void editar() {
        Produto p = selecionado("editar");
        if (p != null) nav.irParaFormUniforme(p);
    }

    private void excluir() {
        Produto p = selecionado("excluir");
        if (p == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "Excluir o uniforme \"" + p.getNome() + " (" + p.getTamanho() + ")\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            controller.excluir(p.getIdProduto());
            filtrar();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
