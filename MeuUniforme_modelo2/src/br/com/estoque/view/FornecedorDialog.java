package br.com.estoque.view;

import br.com.estoque.controller.ProdutoController;
import br.com.estoque.model.Fornecedor;

import javax.swing.*;
import java.awt.*;

/** Janela rápida "Novo fornecedor", aberta pelo botão + do cadastro de uniforme. */
public class FornecedorDialog extends DialogoBase {

    private final ProdutoController controller;
    private final JTextField txtRazao = Estilo.campoComDica("Razão social", 22);
    private final JTextField txtCnpj = Estilo.campoComDica("00.000.000/0000-00", 22);
    private final JTextField txtTelefone = Estilo.campoComDica("(00) 0000-0000", 22);
    private final JTextField txtEmail = Estilo.campoComDica("contato@empresa.com", 22);
    private Fornecedor criado;

    public FornecedorDialog(Frame dono, ProdutoController controller) {
        super(dono, "Novo fornecedor");
        this.controller = controller;
        Estilo.linhaForm(form, 0, "Razão social *", txtRazao);
        Estilo.linhaForm(form, 1, "CNPJ *", txtCnpj);
        Estilo.linhaForm(form, 2, "Telefone", txtTelefone);
        Estilo.linhaForm(form, 3, "E-mail", txtEmail);
        montar();
    }

    /** Fornecedor recém-cadastrado, ou null se a janela foi cancelada. */
    public Fornecedor getCriado() {
        return criado;
    }

    @Override
    protected void aoSalvar() {
        try {
            criado = controller.criarFornecedor(txtRazao.getText(), txtCnpj.getText(),
                    txtTelefone.getText(), txtEmail.getText());
            concluir();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
