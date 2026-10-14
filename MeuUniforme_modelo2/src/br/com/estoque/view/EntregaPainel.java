package br.com.estoque.view;

import br.com.estoque.controller.EntregaController;
import br.com.estoque.controller.EntregaController.ItemEntrega;
import br.com.estoque.model.Aluno;
import br.com.estoque.model.Produto;
import br.com.estoque.model.Turma;
import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Tela "Entrega de uniformes":
 *  1) escolhe a turma e, dentro dela, o aluno;
 *  2) monta a lista de uniformes e quantidades;
 *  3) confirma: registra a saída e baixa o estoque.
 */
public class EntregaPainel extends JPanel {

    private final Navegacao nav;
    private final Usuario usuario;
    private final EntregaController controller = new EntregaController();

    // 1) turma e aluno
    private final JComboBox<Turma> cmbTurma = Estilo.combo(new JComboBox<Turma>());
    private final ListaTableModel<Aluno> modeloAlunos = new ListaTableModel<>(
            new String[]{"Nome do aluno", "Matrícula"},
            (a, c) -> c == 0 ? a.getNome() : a.getMatricula());
    private final JTable tabAlunos = new JTable(modeloAlunos);
    private final JLabel lblAluno = new JLabel();

    // 2) uniformes da entrega
    private final JComboBox<Produto> cmbUniforme = Estilo.combo(new JComboBox<Produto>());
    private final JSpinner spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final List<ItemEntrega> itens = new ArrayList<>();
    private final ListaTableModel<ItemEntrega> modeloItens = new ListaTableModel<>(
            new String[]{"Uniforme", "Tamanho", "Qtd", "Valor unit.", "Subtotal"},
            (i, c) -> {
                switch (c) {
                    case 0: return i.getProduto().getNome();
                    case 1: return i.getProduto().getTamanho();
                    case 2: return i.getQuantidade();
                    case 3: return moeda(i.getProduto().getPreco());
                    default: return moeda(i.getSubtotal());
                }
            });
    private final JTable tabItens = new JTable(modeloItens);
    private final JLabel lblTotal = new JLabel();
    private final JTextField txtObservacao = Estilo.campoComDica("Observação (opcional)", 30);

    private boolean carregando = false;   // evita reagir às mudanças feitas pelo próprio código

    public EntregaPainel(Navegacao nav, Usuario usuario) {
        this.nav = nav;
        this.usuario = usuario;
        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        // topo
        JButton voltar = Estilo.botaoVoltar();
        voltar.addActionListener(e -> nav.irParaMenu());
        JLabel titulo = new JLabel("Entrega de uniformes");
        titulo.setFont(Estilo.fonte(Font.BOLD, 14f));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);
        topo.add(voltar);
        topo.add(titulo);
        add(topo, BorderLayout.NORTH);

        // centro: dois cartões lado a lado
        JPanel centro = new JPanel(new GridLayout(1, 2, 12, 0));
        centro.setOpaque(false);
        centro.add(montarCartaoAluno());
        centro.add(montarCartaoUniformes());
        add(centro, BorderLayout.CENTER);

        // rodapé: observação e botões
        JButton cancelar = Estilo.botaoSecundario("CANCELAR");
        JButton confirmar = Estilo.botaoPrimario("CONFIRMAR ENTREGA");
        cancelar.addActionListener(e -> nav.irParaMenu());
        confirmar.addActionListener(e -> confirmar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(confirmar);

        JPanel rodape = new JPanel(new BorderLayout(12, 0));
        rodape.setOpaque(false);
        rodape.add(txtObservacao, BorderLayout.CENTER);
        rodape.add(botoes, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        // comportamento
        cmbTurma.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !carregando) carregarAlunos();
        });
        tabAlunos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarAlunoSelecionado();
        });
        cmbUniforme.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                if (valor instanceof Produto) setText(descricao((Produto) valor));
                return this;
            }
        });
        atualizarItens();
        atualizarAlunoSelecionado();
    }

    // ---------------------------------------------------------------- montagem

    private JPanel cartao() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Estilo.fonte(Font.BOLD, 13f));
        return l;
    }

    private JPanel montarCartaoAluno() {
        tabAlunos.setRowHeight(26);
        tabAlunos.setFillsViewportHeight(true);
        tabAlunos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabAlunos.getTableHeader().setReorderingAllowed(false);

        lblAluno.setFont(Estilo.fonte(Font.BOLD, 12f));

        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.setOpaque(false);
        norte.add(subtitulo("1. Selecione a turma e o aluno"), BorderLayout.NORTH);
        norte.add(cmbTurma, BorderLayout.CENTER);

        JPanel p = cartao();
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(tabAlunos), BorderLayout.CENTER);
        p.add(lblAluno, BorderLayout.SOUTH);
        return p;
    }

    private JPanel montarCartaoUniformes() {
        tabItens.setRowHeight(26);
        tabItens.setFillsViewportHeight(true);
        tabItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabItens.getTableHeader().setReorderingAllowed(false);

        spnQuantidade.setPreferredSize(new Dimension(70, 30));
        JButton adicionar = Estilo.botaoVerde("+ ADICIONAR");
        adicionar.addActionListener(e -> adicionar());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        direita.setOpaque(false);
        direita.add(new JLabel("Qtd"));
        direita.add(spnQuantidade);
        direita.add(adicionar);

        // o combo ocupa a largura toda (o texto é longo); Qtd e botão ficam logo abaixo
        JPanel linhaAdicionar = new JPanel(new BorderLayout(0, 6));
        linhaAdicionar.setOpaque(false);
        linhaAdicionar.add(cmbUniforme, BorderLayout.NORTH);
        linhaAdicionar.add(direita, BorderLayout.SOUTH);

        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.setOpaque(false);
        norte.add(subtitulo("2. Uniformes a entregar"), BorderLayout.NORTH);
        norte.add(linhaAdicionar, BorderLayout.CENTER);

        JButton remover = Estilo.botaoSecundario("Remover item");
        remover.addActionListener(e -> remover());
        lblTotal.setFont(Estilo.fonte(Font.BOLD, 14f));
        JPanel sul = new JPanel(new BorderLayout());
        sul.setOpaque(false);
        sul.add(remover, BorderLayout.WEST);
        sul.add(lblTotal, BorderLayout.EAST);

        JPanel p = cartao();
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(tabItens), BorderLayout.CENTER);
        p.add(sul, BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------------- dados

    /** Chamado toda vez que a tela é exibida: começa uma entrega nova. */
    public void aoExibir() {
        itens.clear();
        txtObservacao.setText("");
        carregarTurmas();
        carregarProdutos();
        atualizarItens();
    }

    private void carregarTurmas() {
        carregando = true;
        try {
            Turma anterior = (Turma) cmbTurma.getSelectedItem();
            cmbTurma.removeAllItems();
            for (Turma t : controller.listarTurmas()) {
                cmbTurma.addItem(t);
                if (anterior != null && t.getIdTurma() == anterior.getIdTurma()) cmbTurma.setSelectedItem(t);
            }
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        } finally {
            carregando = false;
        }
        carregarAlunos();
    }

    private void carregarAlunos() {
        Turma t = (Turma) cmbTurma.getSelectedItem();
        try {
            modeloAlunos.setItens(t == null ? Collections.<Aluno>emptyList()
                    : controller.listarAlunosDaTurma(t.getIdTurma()));
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
        tabAlunos.clearSelection();
        atualizarAlunoSelecionado();
    }

    private void carregarProdutos() {
        try {
            cmbUniforme.removeAllItems();
            for (Produto p : controller.listarProdutosDisponiveis()) cmbUniforme.addItem(p);
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private Aluno alunoSelecionado() {
        int linha = tabAlunos.getSelectedRow();
        return linha < 0 ? null : modeloAlunos.getItem(linha);
    }

    private void atualizarAlunoSelecionado() {
        Aluno a = alunoSelecionado();
        if (a != null) {
            lblAluno.setText("Aluno: " + a.getNome() + " (matrícula " + a.getMatricula() + ")");
        } else if (cmbTurma.getItemCount() == 0) {
            lblAluno.setText("Cadastre turmas e alunos antes de entregar uniformes.");
        } else {
            lblAluno.setText("Selecione um aluno na tabela.");
        }
    }

    private void atualizarItens() {
        modeloItens.setItens(itens);
        double total = 0;
        for (ItemEntrega i : itens) total += i.getSubtotal();
        lblTotal.setText("Total: " + moeda(total));
    }

    // ---------------------------------------------------------------- ações

    private ItemEntrega buscarNaLista(Produto p) {
        if (p == null) return null;
        for (ItemEntrega i : itens) {
            if (i.getProduto().getIdProduto() == p.getIdProduto()) return i;
        }
        return null;
    }

    private void adicionar() {
        try {
            Produto p = (Produto) cmbUniforme.getSelectedItem();
            int quantidade = (Integer) spnQuantidade.getValue();
            ItemEntrega existente = buscarNaLista(p);
            controller.validarItem(p, quantidade, existente == null ? 0 : existente.getQuantidade());
            if (existente != null) existente.somar(quantidade);
            else itens.add(new ItemEntrega(p, quantidade));
            spnQuantidade.setValue(1);
            atualizarItens();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void remover() {
        int linha = tabItens.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item da lista para remover.",
                    "Nenhum item selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        itens.remove(linha);
        atualizarItens();
    }

    private void confirmar() {
        Aluno aluno = alunoSelecionado();
        if (aluno == null) {
            JOptionPane.showMessageDialog(this, "Selecione o aluno que vai receber os uniformes.",
                    "Verifique os dados", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um uniforme à entrega.",
                    "Verifique os dados", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pecas = 0;
        for (ItemEntrega i : itens) pecas += i.getQuantidade();
        String turma = aluno.getTurma() == null ? "" : " - " + aluno.getTurma().getNome();
        int r = JOptionPane.showConfirmDialog(this,
                "Confirmar a entrega de " + pecas + " peça(s) para " + aluno.getNome() + turma + "?\n"
                        + lblTotal.getText(),
                "Confirmar entrega", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        try {
            int numero = controller.entregar(usuario, aluno, itens, txtObservacao.getText()).getIdMovimentacao();
            JOptionPane.showMessageDialog(this,
                    "Entrega registrada com sucesso!\nMovimentação nº " + numero,
                    "Entrega concluída", JOptionPane.INFORMATION_MESSAGE);
            itens.clear();
            txtObservacao.setText("");
            tabAlunos.clearSelection();
            carregarProdutos();   // o estoque mudou
            atualizarItens();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
            carregarProdutos();   // mostra o saldo atual caso alguém tenha mexido no estoque
        }
    }

    // ---------------------------------------------------------------- formatação

    private static String descricao(Produto p) {
        String detalhe = (p.getDescricao() == null || p.getDescricao().isBlank()) ? "" : " (" + p.getDescricao() + ")";
        return p.getNome() + " - " + p.getTamanho() + detalhe + "  |  estoque: " + p.getQuantidadeEstoque();
    }

    private static String moeda(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", valor);
    }
}
