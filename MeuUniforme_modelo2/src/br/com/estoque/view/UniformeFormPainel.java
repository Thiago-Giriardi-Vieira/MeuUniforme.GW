package br.com.estoque.view;

import br.com.estoque.controller.ProdutoController;
import br.com.estoque.model.Categoria;
import br.com.estoque.model.Fornecedor;
import br.com.estoque.model.Produto;

import javax.swing.*;
import java.awt.*;

/** Tela "Cadastro de uniforme" do protótipo (também usada para editar). */
public class UniformeFormPainel extends JPanel {

    private final Navegacao nav;
    private final ProdutoController controller = new ProdutoController();

    private final JLabel lblTitulo = new JLabel("Cadastro de uniforme");
    private final JComboBox<Object> cmbTipo = Estilo.combo(new JComboBox<Object>());
    private final JTextField txtDescricao = Estilo.campoComDica("Digite a descrição", 20);
    private final JComboBox<Object> cmbTamanho = Estilo.combo(new JComboBox<Object>());
    private final JComboBox<Object> cmbCategoria = Estilo.combo(new JComboBox<Object>());
    private final JTextField txtPreco = Estilo.campoComDica("0,00", 10);
    private final JTextField txtQuantidade = Estilo.campoComDica("0", 10);
    private final JComboBox<Object> cmbFornecedor = Estilo.combo(new JComboBox<Object>());
    private final JTextField txtMinimo = Estilo.campoComDica("0", 10);
    private final JTextArea txtObservacoes = new JTextArea(4, 20);

    /** null = cadastrando um uniforme novo; senão, id do uniforme em edição. */
    private Integer idEdicao = null;

    public UniformeFormPainel(Navegacao nav) {
        this.nav = nav;
        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        JButton voltar = Estilo.botaoVoltar();
        voltar.addActionListener(e -> nav.irParaEstoque());
        lblTitulo.setFont(Estilo.fonte(Font.BOLD, 14f));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);
        topo.add(voltar);
        topo.add(lblTitulo);
        add(topo, BorderLayout.NORTH);

        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        Estilo.campo(txtObservacoes);

        JPanel grade = new JPanel(new GridBagLayout());
        grade.setOpaque(false);
        celula(grade, 0, 0, 1, "Tipo de uniforme", cmbTipo);
        celula(grade, 1, 0, 1, "Descrição", txtDescricao);
        celula(grade, 0, 1, 1, "Tamanho", cmbTamanho);
        celula(grade, 1, 1, 1, "Categoria", comBotaoMais(cmbCategoria, this::novaCategoria));
        celula(grade, 0, 2, 1, "Preço (R$)", txtPreco);
        celula(grade, 1, 2, 1, "Quantidade em estoque", txtQuantidade);
        celula(grade, 0, 3, 1, "Fornecedor", comBotaoMais(cmbFornecedor, this::novoFornecedor));
        celula(grade, 1, 3, 2, "Observações", new JScrollPane(txtObservacoes));
        celula(grade, 0, 4, 1, "Estoque mínimo (alerta)", txtMinimo);

        GridBagConstraints folga = new GridBagConstraints();
        folga.gridy = 5;
        folga.weighty = 1;
        JPanel espaco = new JPanel();
        espaco.setOpaque(false);
        grade.add(espaco, folga);

        JButton cancelar = Estilo.botaoSecundario("CANCELAR");
        JButton salvar = Estilo.botaoPrimario("SALVAR UNIFORME");
        cancelar.addActionListener(e -> nav.irParaEstoque());
        salvar.addActionListener(e -> salvar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);

        JPanel cartao = new JPanel(new BorderLayout(0, 8));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        grade.setBackground(Color.WHITE);
        cartao.add(grade, BorderLayout.CENTER);
        cartao.add(botoes, BorderLayout.SOUTH);
        add(cartao, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------- montagem

    private void celula(JPanel grade, int x, int y, int altura, String rotulo, JComponent campo) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(Estilo.rotulo(rotulo), BorderLayout.NORTH);
        c.add(campo, BorderLayout.CENTER);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.gridheight = altura;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;
        g.insets = new Insets(6, 14, 6, 14);
        grade.add(c, g);
    }

    private JPanel comBotaoMais(JComboBox<Object> combo, Runnable acao) {
        JButton mais = Estilo.botaoPagina("+", false);
        mais.setToolTipText("Cadastrar novo");
        mais.addActionListener(e -> acao.run());
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        p.add(combo, BorderLayout.CENTER);
        p.add(mais, BorderLayout.EAST);
        return p;
    }

    // ---------------------------------------------------------------- preparar a tela

    /** @param produto uniforme a editar, ou null para um cadastro novo */
    public void preparar(Produto produto) {
        carregarListas();
        idEdicao = produto == null ? null : produto.getIdProduto();
        lblTitulo.setText(produto == null ? "Cadastro de uniforme" : "Editar uniforme");

        if (produto == null) {
            cmbTipo.setSelectedIndex(0);
            cmbTamanho.setSelectedIndex(0);
            cmbCategoria.setSelectedIndex(0);
            cmbFornecedor.setSelectedIndex(0);
            txtDescricao.setText("");
            txtPreco.setText("");
            txtQuantidade.setText("0");
            txtMinimo.setText("0");
            txtObservacoes.setText("");
            return;
        }
        selecionarOuAdicionar(cmbTipo, produto.getNome());
        selecionarOuAdicionar(cmbTamanho, produto.getTamanho());
        selecionarCategoria(produto.getCategoria().getIdCategoria());
        if (produto.getFornecedor() != null) selecionarFornecedor(produto.getFornecedor().getIdFornecedor());
        else cmbFornecedor.setSelectedIndex(0);
        txtDescricao.setText(produto.getDescricao() == null ? "" : produto.getDescricao());
        txtPreco.setText(String.format("%.2f", produto.getPreco()).replace('.', ','));
        txtQuantidade.setText(String.valueOf(produto.getQuantidadeEstoque()));
        txtMinimo.setText(String.valueOf(produto.getEstoqueMinimo()));
        txtObservacoes.setText(produto.getObservacoes() == null ? "" : produto.getObservacoes());
    }

    private void carregarListas() {
        cmbTipo.removeAllItems();
        cmbTipo.addItem("Selecione o tipo");
        for (String t : ProdutoController.TIPOS) cmbTipo.addItem(t);

        cmbTamanho.removeAllItems();
        cmbTamanho.addItem("Selecione o tamanho");
        for (String t : ProdutoController.TAMANHOS) cmbTamanho.addItem(t);

        carregarCategorias();
        carregarFornecedores();
    }

    private void carregarCategorias() {
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem("Selecione a categoria");
        try {
            for (Categoria c : controller.listarCategorias()) cmbCategoria.addItem(c);
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void carregarFornecedores() {
        cmbFornecedor.removeAllItems();
        cmbFornecedor.addItem("Selecione o fornecedor");
        try {
            for (Fornecedor f : controller.listarFornecedores()) cmbFornecedor.addItem(f);
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void selecionarOuAdicionar(JComboBox<Object> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (valor.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(valor); // valor antigo que não está na lista padrão
        combo.setSelectedItem(valor);
    }

    private void selecionarCategoria(int id) {
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Object o = cmbCategoria.getItemAt(i);
            if (o instanceof Categoria && ((Categoria) o).getIdCategoria() == id) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarFornecedor(int id) {
        for (int i = 0; i < cmbFornecedor.getItemCount(); i++) {
            Object o = cmbFornecedor.getItemAt(i);
            if (o instanceof Fornecedor && ((Fornecedor) o).getIdFornecedor() == id) {
                cmbFornecedor.setSelectedIndex(i);
                return;
            }
        }
    }

    // ---------------------------------------------------------------- ações

    private void novaCategoria() {
        String nome = JOptionPane.showInputDialog(this, "Nome da nova categoria:", "Nova categoria",
                JOptionPane.PLAIN_MESSAGE);
        if (nome == null) return;
        try {
            Categoria nova = controller.criarCategoria(nome);
            carregarCategorias();
            selecionarCategoria(nova.getIdCategoria());
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void novoFornecedor() {
        FornecedorDialog d = new FornecedorDialog((Frame) SwingUtilities.getWindowAncestor(this), controller);
        d.setVisible(true);
        if (d.getCriado() != null) {
            carregarFornecedores();
            selecionarFornecedor(d.getCriado().getIdFornecedor());
        }
    }

    private void salvar() {
        try {
            String tipo = cmbTipo.getSelectedIndex() > 0 ? cmbTipo.getSelectedItem().toString() : null;
            String tamanho = cmbTamanho.getSelectedIndex() > 0 ? cmbTamanho.getSelectedItem().toString() : null;
            Object cat = cmbCategoria.getSelectedItem();
            Object forn = cmbFornecedor.getSelectedItem();

            controller.salvar(idEdicao, tipo, txtDescricao.getText(), tamanho,
                    txtPreco.getText(), txtQuantidade.getText(), txtMinimo.getText(),
                    cat instanceof Categoria ? (Categoria) cat : null,
                    forn instanceof Fornecedor ? (Fornecedor) forn : null,
                    txtObservacoes.getText());

            JOptionPane.showMessageDialog(this, idEdicao == null
                    ? "Uniforme cadastrado com sucesso!" : "Uniforme atualizado com sucesso!");
            nav.irParaEstoque();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
