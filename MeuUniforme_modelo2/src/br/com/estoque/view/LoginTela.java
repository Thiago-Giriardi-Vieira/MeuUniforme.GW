package br.com.estoque.view;

import br.com.estoque.controller.UsuarioController;
import br.com.estoque.model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Porta de entrada do sistema. Alterna entre "Login" e "Cadastro de usuário"
 * e, após o login, abre a janela principal.
 */
public class LoginTela extends JFrame {

    private static final String LOGIN = "login";
    private static final String CADASTRO = "cadastro";

    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);
    private final LoginPainel login;
    private final CadastroUsuarioPainel cadastro;

    public LoginTela() {
        super("MeuUniforme.GW - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        UsuarioController controller = new UsuarioController();
        login = new LoginPainel(controller, this::abrirSistema, this::mostrarCadastro);
        cadastro = new CadastroUsuarioPainel(controller, null, this::mostrarLogin, this::cadastroConcluido);

        raiz.add(login, LOGIN);
        raiz.add(cadastro, CADASTRO);
        add(raiz);

        setSize(760, 440);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void abrirSistema(Usuario usuario) {
        new JanelaPrincipal(usuario).setVisible(true);
        dispose();
    }

    private void mostrarCadastro() {
        cadastro.preparar();
        cartas.show(raiz, CADASTRO);
    }

    private void mostrarLogin() {
        cartas.show(raiz, LOGIN);
    }

    private void cadastroConcluido(String loginCriado) {
        login.preencherLogin(loginCriado);
        mostrarLogin();
    }

    public static void main(String[] args) {
        Estilo.instalarLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginTela().setVisible(true));
    }
}
