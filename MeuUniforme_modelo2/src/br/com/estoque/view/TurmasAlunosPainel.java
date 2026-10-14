package br.com.estoque.view;

import br.com.estoque.controller.AlunoController;
import br.com.estoque.controller.TurmaController;
import br.com.estoque.model.Aluno;
import br.com.estoque.model.Turma;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

/**
 * Tela "Turmas e alunos" do protótipo, com as abas Turmas e Alunos.
 * Coluna "Ações": clique na metade esquerda para editar, na direita para excluir.
 */
public class TurmasAlunosPainel extends JPanel {

    private static final String ABA_TURMAS = "turmas";
    private static final String ABA_ALUNOS = "alunos";
    private static final String TEXTO_ACOES = "Editar  |  Excluir";

    private final Navegacao nav;
    private final TurmaController turmaController = new TurmaController();
    private final AlunoController alunoController = new AlunoController();

    private Map<Integer, Integer> alunosPorTurma = new HashMap<>();

    private final ListaTableModel<Turma> modeloTurmas = new ListaTableModel<>(
            new String[]{"Turma", "Ano/Séries", "Alunos", "Ações"},
            (t, c) -> {
                switch (c) {
                    case 0: return t.getNome();
                    case 1: return t.getAnoSerie() == null ? "" : t.getAnoSerie();
                    case 2: return alunosPorTurma.getOrDefault(t.getIdTurma(), 0);
                    default: return TEXTO_ACOES;
                }
            });
    private final ListaTableModel<Aluno> modeloAlunosDaTurma = new ListaTableModel<>(
            new String[]{"Nome do aluno", "Matrícula"},
            (a, c) -> c == 0 ? a.getNome() : a.getMatricula());
    private final ListaTableModel<Aluno> modeloAlunos = new ListaTableModel<>(
            new String[]{"Nome do aluno", "Matrícula", "Turma", "Responsável", "Telefone", "Ações"},
            (a, c) -> {
                switch (c) {
                    case 0: return a.getNome();
                    case 1: return a.getMatricula();
                    case 2: return a.getTurma() == null ? "-" : a.getTurma().getNome();
                    case 3: return a.getNomeResponsavel() == null ? "" : a.getNomeResponsavel();
                    case 4: return a.getTelefone() == null ? "" : a.getTelefone();
                    default: return TEXTO_ACOES;
                }
            });

    private final JTable tabTurmas = new JTable(modeloTurmas);
    private final JTable tabAlunosDaTurma = new JTable(modeloAlunosDaTurma);
    private final JTable tabAlunos = new JTable(modeloAlunos);
    private final JLabel lblAlunosDaTurma = new JLabel("Alunos da turma:");

    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final JButton abaTurmas = criarAba("Turmas");
    private final JButton abaAlunos = criarAba("Alunos");

    public TurmasAlunosPainel(Navegacao nav) {
        this.nav = nav;
        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        // seta de voltar
        JButton voltar = Estilo.botaoVoltar();
        voltar.addActionListener(e -> nav.irParaMenu());
        JPanel linhaVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaVoltar.setOpaque(false);
        linhaVoltar.add(voltar);

        // abas (esquerda) e botões verdes (direita)
        abaTurmas.addActionListener(e -> mostrarAba(false));
        abaAlunos.addActionListener(e -> mostrarAba(true));
        JPanel abas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        abas.setOpaque(false);
        abas.add(abaTurmas);
        abas.add(abaAlunos);

        JButton btnNovaTurma = Estilo.botaoVerde("+ NOVA TURMA");
        JButton btnNovoAluno = Estilo.botaoVerde("+ NOVO ALUNO");
        btnNovaTurma.addActionListener(e -> abrirTurma(null));
        btnNovoAluno.addActionListener(e -> abrirAluno(null));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(btnNovaTurma);
        botoes.add(btnNovoAluno);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(abas, BorderLayout.WEST);
        cabecalho.add(botoes, BorderLayout.EAST);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setOpaque(false);
        norte.add(linhaVoltar, BorderLayout.NORTH);
        norte.add(cabecalho, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        // aba Turmas: duas tabelas lado a lado
        configurarTabela(tabTurmas);
        configurarTabela(tabAlunosDaTurma);
        configurarTabela(tabAlunos);
        ligarAcoes(tabTurmas, 3, linha -> abrirTurma(modeloTurmas.getItem(linha)),
                linha -> excluirTurma(modeloTurmas.getItem(linha)));
        ligarAcoes(tabAlunos, 5, linha -> abrirAluno(modeloAlunos.getItem(linha)),
                linha -> excluirAluno(modeloAlunos.getItem(linha)));
        tabTurmas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarAlunosDaTurma();
        });

        lblAlunosDaTurma.setFont(Estilo.fonte(Font.BOLD, 13f));
        JLabel lblTurmas = new JLabel("Turmas cadastradas");
        lblTurmas.setFont(Estilo.fonte(Font.BOLD, 13f));

        JPanel painelTurmas = new JPanel(new GridLayout(1, 2, 12, 0));
        painelTurmas.setOpaque(false);
        painelTurmas.add(secao(lblTurmas, tabTurmas));
        painelTurmas.add(secao(lblAlunosDaTurma, tabAlunosDaTurma));

        JLabel lblTodos = new JLabel("Alunos cadastrados");
        lblTodos.setFont(Estilo.fonte(Font.BOLD, 13f));

        conteudo.setOpaque(false);
        conteudo.add(painelTurmas, ABA_TURMAS);
        conteudo.add(secao(lblTodos, tabAlunos), ABA_ALUNOS);
        add(conteudo, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------- montagem

    private JButton criarAba(String texto) {
        JButton b = new JButton(texto);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void estilizarAba(JButton aba, boolean ativa) {
        aba.setFont(Estilo.fonte(ativa ? Font.BOLD : Font.PLAIN, 13f));
        aba.setForeground(ativa ? Estilo.AZUL : Color.GRAY);
        aba.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ativa ? Estilo.AZUL : Estilo.FUNDO),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
    }

    private JPanel secao(JLabel titulo, JTable tabela) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        p.add(titulo, BorderLayout.NORTH);
        p.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return p;
    }

    private void configurarTabela(JTable t) {
        t.setRowHeight(26);
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setReorderingAllowed(false);
    }

    /** Faz a coluna de ações responder a cliques: metade esquerda edita, metade direita exclui. */
    private void ligarAcoes(JTable tabela, int colunaAcoes, IntConsumer editar, IntConsumer excluir) {
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        centro.setForeground(Estilo.LINK);
        tabela.getColumnModel().getColumn(colunaAcoes).setCellRenderer(centro);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());
                int coluna = tabela.columnAtPoint(e.getPoint());
                if (linha < 0 || coluna != colunaAcoes) return;
                Rectangle r = tabela.getCellRect(linha, coluna, false);
                if (e.getX() < r.x + r.width / 2) editar.accept(linha);
                else excluir.accept(linha);
            }
        });
    }

    // ---------------------------------------------------------------- dados

    /** Chamado toda vez que a tela é exibida. */
    public void aoExibir(boolean abrirAbaAlunos) {
        mostrarAba(abrirAbaAlunos);
        carregarTudo();
    }

    private void mostrarAba(boolean alunos) {
        cartas.show(conteudo, alunos ? ABA_ALUNOS : ABA_TURMAS);
        estilizarAba(abaTurmas, !alunos);
        estilizarAba(abaAlunos, alunos);
    }

    private void carregarTudo() {
        Turma selecionada = turmaSelecionada();
        try {
            alunosPorTurma = turmaController.contarAlunosPorTurma();
            modeloTurmas.setItens(turmaController.listar());
            modeloAlunos.setItens(alunoController.listar());
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
            return;
        }
        if (selecionada != null) {
            for (int i = 0; i < modeloTurmas.getRowCount(); i++) {
                if (modeloTurmas.getItem(i).getIdTurma() == selecionada.getIdTurma()) {
                    tabTurmas.setRowSelectionInterval(i, i);
                    return;
                }
            }
        }
        carregarAlunosDaTurma();
    }

    private Turma turmaSelecionada() {
        int linha = tabTurmas.getSelectedRow();
        return linha < 0 ? null : modeloTurmas.getItem(linha);
    }

    private void carregarAlunosDaTurma() {
        Turma t = turmaSelecionada();
        if (t == null) {
            lblAlunosDaTurma.setText("Alunos da turma:");
            modeloAlunosDaTurma.setItens(Collections.emptyList());
            return;
        }
        lblAlunosDaTurma.setText("Alunos da turma: " + t.getNome());
        try {
            modeloAlunosDaTurma.setItens(alunoController.listarPorTurma(t.getIdTurma()));
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    // ---------------------------------------------------------------- ações

    private Frame janela() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void abrirTurma(Turma turma) {
        TurmaDialog d = new TurmaDialog(janela(), turmaController, turma);
        d.setVisible(true);
        if (d.isSalvo()) carregarTudo();
    }

    private void abrirAluno(Aluno aluno) {
        List<Turma> turmas;
        try {
            turmas = turmaController.listar();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
            return;
        }
        if (turmas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre uma turma antes de cadastrar alunos.",
                    "Nenhuma turma cadastrada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        AlunoDialog d = new AlunoDialog(janela(), alunoController, turmas, aluno, turmaSelecionada());
        d.setVisible(true);
        if (d.isSalvo()) carregarTudo();
    }

    private void excluirTurma(Turma t) {
        int r = JOptionPane.showConfirmDialog(this, "Excluir a turma \"" + t.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            turmaController.excluir(t.getIdTurma());
            tabTurmas.clearSelection();
            carregarTudo();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }

    private void excluirAluno(Aluno a) {
        int r = JOptionPane.showConfirmDialog(this, "Excluir o aluno \"" + a.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            alunoController.excluir(a.getIdAluno());
            carregarTudo();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
