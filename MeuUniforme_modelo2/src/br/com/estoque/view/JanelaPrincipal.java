package br.com.estoque.view;

import br.com.estoque.model.Produto;
import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;

/** Janela do sistema após o login. Troca entre as telas com um CardLayout. */
public class JanelaPrincipal extends JFrame implements Navegacao {

    private static final String MENU = "menu";
    private static final String ESTOQUE = "estoque";
    private static final String FORM_UNIFORME = "formUniforme";
    private static final String TURMAS = "turmas";

    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);

    private final EstoquePainel estoque;
    private final UniformeFormPainel formUniforme;
    private final TurmasAlunosPainel turmas;

    public JanelaPrincipal(Usuario usuario) {
        super("MeuUniforme.GW - Escola Municipal Prefeito Geraldo Wetzel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        estoque = new EstoquePainel(this);
        formUniforme = new UniformeFormPainel(this);
        turmas = new TurmasAlunosPainel(this);

        raiz.add(new MenuPainel(this, usuario), MENU);
        raiz.add(estoque, ESTOQUE);
        raiz.add(formUniforme, FORM_UNIFORME);
        raiz.add(turmas, TURMAS);
        add(raiz);

        setSize(1000, 640);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        irParaMenu();
    }

    @Override
    public void irParaMenu() {
        cartas.show(raiz, MENU);
    }

    @Override
    public void irParaEstoque() {
        estoque.aoExibir();
        cartas.show(raiz, ESTOQUE);
    }

    @Override
    public void irParaFormUniforme(Produto produto) {
        formUniforme.preparar(produto);
        cartas.show(raiz, FORM_UNIFORME);
    }

    @Override
    public void irParaTurmas(boolean abaAlunos) {
        turmas.aoExibir(abaAlunos);
        cartas.show(raiz, TURMAS);
    }

    @Override
    public void sair() {
        int r = JOptionPane.showConfirmDialog(this, "Deseja sair e voltar para a tela de login?",
                "Sair", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            new LoginTela().setVisible(true);
            dispose();
        }
    }
}
