package br.com.estoque.view;

import br.com.estoque.model.Produto;

/** Operações de navegação entre as telas da janela principal. */
public interface Navegacao {
    void irParaMenu();
    void irParaEstoque();
    /** @param produto uniforme a editar, ou null para cadastrar um novo */
    void irParaFormUniforme(Produto produto);
    /** @param abaAlunos true abre direto na aba "Alunos"; false, na aba "Turmas" */
    void irParaTurmas(boolean abaAlunos);
    void irParaEntrega();
    void sair();
}
