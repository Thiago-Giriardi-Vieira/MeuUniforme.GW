package br.com.estoque.view;

import br.com.estoque.controller.UsuarioController;
import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** Tela "Cadastro de usuário" do protótipo. */
public class CadastroUsuarioPainel extends JPanel {

    private final UsuarioController controller;
    private final Usuario criador;          // quem está cadastrando (null na tela de login)
    private final Consumer<String> aoConcluir;

    private final JTextField txtNome = Estilo.campo(new JTextField(24));
    private final JTextField txtLogin = Estilo.campo(new JTextField(24));
    private final JPasswordField txtSenha = Estilo.campo(new JPasswordField(10));
    private final JPasswordField txtConfirma = Estilo.campo(new JPasswordField(10));
    private final JComboBox<String> cmbNivel = Estilo.combo(new JComboBox<String>());
    private String[] perfis = {UsuarioController.OPERADOR};

    public CadastroUsuarioPainel(UsuarioController controller, Usuario criador,
                                 Runnable aoVoltar, Consumer<String> aoConcluir) {
        this.controller = controller;
        this.criador = criador;
        this.aoConcluir = aoConcluir;

        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout());

        JButton voltar = Estilo.botaoVoltar();
        voltar.addActionListener(e -> aoVoltar.run());
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topo.setOpaque(false);
        topo.add(voltar);
        add(topo, BorderLayout.NORTH);

        JPanel esquerda = new JPanel(new GridBagLayout());
        esquerda.setOpaque(false);
        esquerda.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 10));
        esquerda.add(Estilo.logo(190));
        add(esquerda, BorderLayout.WEST);

        JButton cancelar = Estilo.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> aoVoltar.run());
        JButton salvar = Estilo.botaoPrimario("SALVAR USUÁRIO");
        salvar.addActionListener(e -> salvar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(0, 10, 30, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.anchor = GridBagConstraints.WEST;

        g.gridy = 0; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 2, 0);
        form.add(Estilo.rotulo("Nome Completo"), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtNome, g);
        g.gridy = 2; g.insets = new Insets(0, 0, 2, 0);
        form.add(Estilo.rotulo("Login"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtLogin, g);

        g.gridwidth = 1;
        g.gridy = 4; g.gridx = 0; g.insets = new Insets(0, 0, 2, 8);
        form.add(Estilo.rotulo("Senha"), g);
        g.gridx = 1; g.insets = new Insets(0, 0, 2, 0);
        form.add(Estilo.rotulo("Confirmar senha"), g);
        g.gridy = 5; g.gridx = 0; g.insets = new Insets(0, 0, 10, 8);
        form.add(txtSenha, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtConfirma, g);

        g.gridwidth = 2;
        g.gridy = 6; g.gridx = 0; g.insets = new Insets(0, 0, 2, 0);
        form.add(Estilo.rotulo("Nível de acesso"), g);
        g.gridy = 7; g.insets = new Insets(0, 0, 14, 0);
        form.add(cmbNivel, g);
        g.gridy = 8; g.insets = new Insets(0, 0, 0, 0);
        form.add(botoes, g);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(form, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(centro, BorderLayout.CENTER);
    }

    /** Limpa os campos e recalcula os níveis de acesso permitidos. Chamar sempre que a tela for exibida. */
    public void preparar() {
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        txtConfirma.setText("");
        try {
            perfis = controller.perfisPermitidos(criador);
        } catch (RuntimeException e) {
            perfis = new String[]{UsuarioController.OPERADOR};
            Estilo.erro(this, e);
        }
        cmbNivel.removeAllItems();
        for (String p : perfis) cmbNivel.addItem(UsuarioController.ADMIN.equals(p) ? "Administrador" : "Operador");
        cmbNivel.setEnabled(perfis.length > 1);
        txtNome.requestFocusInWindow();
    }

    private void salvar() {
        try {
            String perfil = perfis[Math.max(0, cmbNivel.getSelectedIndex())];
            Usuario criado = controller.cadastrar(txtNome.getText(), txtLogin.getText(),
                    new String(txtSenha.getPassword()), new String(txtConfirma.getPassword()),
                    perfil, criador);
            JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!\nNível de acesso: "
                    + (UsuarioController.ADMIN.equals(criado.getPerfil()) ? "Administrador" : "Operador"),
                    "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
            aoConcluir.accept(criado.getLogin());
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
