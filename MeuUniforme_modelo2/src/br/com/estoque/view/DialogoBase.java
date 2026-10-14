package br.com.estoque.view;

import javax.swing.*;
import java.awt.*;

/** Estrutura comum das janelas de cadastro: formulário no centro, botões Cancelar/Salvar embaixo. */
abstract class DialogoBase extends JDialog {

    protected final JPanel form = new JPanel(new GridBagLayout());
    private boolean salvo = false;

    DialogoBase(Frame dono, String titulo) {
        super(dono, titulo, true);
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
    }

    /** Chamar no fim do construtor da subclasse, depois de adicionar os campos ao form. */
    protected final void montar() {
        JButton cancelar = Estilo.botaoSecundario("Cancelar");
        JButton salvar = Estilo.botaoPrimario("Salvar");
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> aoSalvar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.add(cancelar);
        botoes.add(salvar);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(salvar);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    /** Valida e grava. Em caso de sucesso, chamar concluir(). */
    protected abstract void aoSalvar();

    protected final void concluir() {
        salvo = true;
        dispose();
    }

    public boolean isSalvo() {
        return salvo;
    }
}
