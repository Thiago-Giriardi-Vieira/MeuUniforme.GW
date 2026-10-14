package br.com.estoque.controller;

import br.com.estoque.dao.UsuarioDAO;
import br.com.estoque.model.Usuario;
import br.com.estoque.util.SenhaUtil;

import java.util.Arrays;

/**
 * Regras de login e de cadastro de usuário.
 * Erros de validação saem como IllegalArgumentException com mensagem pronta para a tela.
 */
public class UsuarioController {

    public static final String ADMIN = "ADMIN";
    public static final String OPERADOR = "OPERADOR";

    private static final int MIN_SENHA = 4;
    private static final int MIN_LOGIN = 3;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Retorna o usuário autenticado ou lança IllegalArgumentException. */
    public Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isEmpty())
            throw new IllegalArgumentException("Informe o usuário e a senha.");

        Usuario u = usuarioDAO.buscarPorLogin(login.trim());
        // Mesma mensagem para login inexistente e senha errada (não revela qual deles falhou)
        if (u == null || !SenhaUtil.verificar(senha, u.getSenha()))
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        return u;
    }

    /**
     * Perfis que podem ser escolhidos no cadastro:
     *  - ainda não há usuários: só ADMIN (o primeiro usuário administra o sistema);
     *  - quem cadastra é um ADMIN: ADMIN ou OPERADOR;
     *  - nos demais casos (cadastro pela tela de login): só OPERADOR.
     */
    public String[] perfisPermitidos(Usuario criador) {
        if (usuarioDAO.listar().isEmpty()) return new String[]{ADMIN};
        if (criador != null && ADMIN.equals(criador.getPerfil())) return new String[]{ADMIN, OPERADOR};
        return new String[]{OPERADOR};
    }

    public Usuario cadastrar(String nome, String login, String senha, String confirmacao,
                             String perfil, Usuario criador) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Informe o nome completo.");
        if (login == null || login.trim().length() < MIN_LOGIN || login.trim().contains(" "))
            throw new IllegalArgumentException("O login deve ter ao menos " + MIN_LOGIN + " caracteres, sem espaços.");
        if (senha == null || senha.length() < MIN_SENHA)
            throw new IllegalArgumentException("A senha deve ter ao menos " + MIN_SENHA + " caracteres.");
        if (!senha.equals(confirmacao))
            throw new IllegalArgumentException("A confirmação da senha não confere.");
        if (!Arrays.asList(perfisPermitidos(criador)).contains(perfil))
            throw new IllegalArgumentException("Você não tem permissão para criar um usuário com este nível de acesso.");
        if (usuarioDAO.buscarPorLogin(login.trim()) != null)
            throw new IllegalArgumentException("Este login já está em uso. Escolha outro.");

        Usuario novo = new Usuario(0, nome.trim(), login.trim(), SenhaUtil.gerarHash(senha), perfil);
        usuarioDAO.inserir(novo);
        return novo;
    }
}
