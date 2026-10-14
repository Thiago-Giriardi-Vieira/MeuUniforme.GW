package br.com.estoque.view;

import br.com.estoque.controller.UsuarioController;
import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

/** Tela "Login" do protótipo: logo à esquerda, formulário à direita. */
public class LoginPainel extends JPanel {

    private static final String CHAVE_LOGIN = "ultimoLogin";

    private final Preferences prefs = Preferences.userNodeForPackage(LoginPainel.class);
    private final UsuarioController controller;
    private final Consumer<Usuario> aoEntrar;

    private final JTextField txtUsuario = Estilo.campo(new JTextField(20));
    private final JPasswordField txtSenha = Estilo.campo(new JPasswordField(20));
    private final JCheckBox chkLembrar = new JCheckBox("Lembrar-me");

    public LoginPainel(UsuarioController controller, Consumer<Usuario> aoEntrar, Runnable aoCriarUsuario) {
        this.controller = controller;
        this.aoEntrar = aoEntrar;

        setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout());

        JPanel esquerda = new JPanel(new GridBagLayout());
        esquerda.setOpaque(false);
        esquerda.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 10));
        esquerda.add(Estilo.logo(190));
        add(esquerda, BorderLayout.WEST);

        JPanel direita = new JPanel(new GridBagLayout());
        direita.setOpaque(false);
        direita.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
        direita.add(montarFormulario(aoCriarUsuario));
        add(direita, BorderLayout.CENTER);

        chkLembrar.setOpaque(false);
        chkLembrar.setFont(Estilo.fonte(Font.PLAIN, 11f));
        txtUsuario.addActionListener(e -> txtSenha.requestFocusInWindow());
        txtSenha.addActionListener(e -> entrar());

        String lembrado = prefs.get(CHAVE_LOGIN, "");
        if (!lembrado.isEmpty()) {
            txtUsuario.setText(lembrado);
            chkLembrar.setSelected(true);
        }
    }

    private JPanel montarFormulario(Runnable aoCriarUsuario) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JLabel titulo = new JLabel("Bem-vindo!", SwingConstants.CENTER);
        titulo.setFont(Estilo.fonte(Font.BOLD, 26f));
        JLabel sub = new JLabel("Faça seu login para continuar", SwingConstants.CENTER);
        sub.setFont(Estilo.fonte(Font.PLAIN, 11f));
        JLabel lblUsuario = new JLabel("Usuário", SwingConstants.CENTER);
        lblUsuario.setFont(Estilo.fonte(Font.BOLD, 13f));
        JLabel lblSenha = new JLabel("Senha", SwingConstants.CENTER);
        lblSenha.setFont(Estilo.fonte(Font.BOLD, 13f));

        JButton esqueci = Estilo.link("Esqueci minha senha");
        esqueci.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Para redefinir sua senha, procure o administrador do sistema.",
                "Esqueci minha senha", JOptionPane.INFORMATION_MESSAGE));

        JPanel lembrarEsqueci = new JPanel(new BorderLayout());
        lembrarEsqueci.setOpaque(false);
        lembrarEsqueci.add(chkLembrar, BorderLayout.WEST);
        lembrarEsqueci.add(esqueci, BorderLayout.EAST);

        JButton entrar = Estilo.botaoPrimario("Entrar");
        entrar.setFont(Estilo.fonte(Font.BOLD, 14f));
        entrar.addActionListener(e -> entrar());

        JButton criar = Estilo.link("Criar novo usuário");
        criar.addActionListener(e -> aoCriarUsuario.run());

        int y = 0;
        adicionar(p, titulo, y++, new Insets(0, 0, 2, 0));
        adicionar(p, sub, y++, new Insets(0, 0, 14, 0));
        adicionar(p, lblUsuario, y++, new Insets(0, 0, 3, 0));
        adicionar(p, txtUsuario, y++, new Insets(0, 0, 10, 0));
        adicionar(p, lblSenha, y++, new Insets(0, 0, 3, 0));
        adicionar(p, txtSenha, y++, new Insets(0, 0, 4, 0));
        adicionar(p, lembrarEsqueci, y++, new Insets(0, 0, 10, 0));
        adicionar(p, entrar, y++, new Insets(0, 0, 6, 0));
        adicionar(p, criar, y, new Insets(0, 0, 0, 0));
        return p;
    }

    private void adicionar(JPanel p, JComponent c, int linha, Insets insets) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linha;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = insets;
        p.add(c, g);
    }

    /** Usado depois de criar um usuário: já deixa o login preenchido. */
    public void preencherLogin(String login) {
        txtUsuario.setText(login);
        txtSenha.setText("");
        txtSenha.requestFocusInWindow();
    }

    private void entrar() {
        try {
            Usuario u = controller.autenticar(txtUsuario.getText(), new String(txtSenha.getPassword()));
            if (chkLembrar.isSelected()) prefs.put(CHAVE_LOGIN, u.getLogin());
            else prefs.remove(CHAVE_LOGIN);
            txtSenha.setText("");
            aoEntrar.accept(u);
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
            txtSenha.setText("");
            txtSenha.requestFocusInWindow();
        }
    }
}
