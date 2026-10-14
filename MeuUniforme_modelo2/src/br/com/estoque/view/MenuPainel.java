package br.com.estoque.view;

import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;

/** Tela "Menu inicial" do protótipo: três botões grandes e o logo da escola. */
public class MenuPainel extends JPanel {

    public MenuPainel(Navegacao nav, Usuario usuario) {
        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        JButton voltar = Estilo.botaoVoltar();
        voltar.setToolTipText("Sair");
        voltar.addActionListener(e -> nav.sair());

        JLabel ola = new JLabel("Olá, " + usuario.getNome() + " ("
                + ("ADMIN".equals(usuario.getPerfil()) ? "Administrador" : "Operador") + ")");
        ola.setFont(Estilo.fonte(Font.PLAIN, 12f));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(voltar, BorderLayout.WEST);
        topo.add(ola, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logo.setOpaque(false);
        logo.add(Estilo.logo(100));

        JPanel botoes = new JPanel(new GridBagLayout());
        botoes.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 14, 40, 14);
        g.gridy = 0;
        g.gridx = 0;
        botoes.add(botaoMenu("Uniformes em<br>estoque", nav::irParaEstoque), g);
        g.gridx = 1;
        botoes.add(botaoMenu("Alunos<br>cadastrados", () -> nav.irParaTurmas(true)), g);
        g.gridx = 2;
        botoes.add(botaoMenu("Turmas<br>cadastradas", () -> nav.irParaTurmas(false)), g);

        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(Estilo.FUNDO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        cartao.add(logo, BorderLayout.NORTH);
        cartao.add(botoes, BorderLayout.CENTER);
        add(cartao, BorderLayout.CENTER);
    }

    private JButton botaoMenu(String textoHtml, Runnable acao) {
        JButton b = Estilo.botaoPrimario("<html><div style='text-align:center'>" + textoHtml + "</div></html>");
        b.setFont(Estilo.fonte(Font.BOLD, 15f));
        b.setPreferredSize(new Dimension(210, 120));
        b.addActionListener(e -> acao.run());
        return b;
    }
}
