package br.com.estoque.controller;

import br.com.estoque.dao.CategoriaDAO;
import br.com.estoque.dao.FornecedorDAO;
import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.model.Categoria;
import br.com.estoque.model.Fornecedor;
import br.com.estoque.model.Produto;

import java.util.List;

/**
 * Regras do cadastro e da consulta de uniformes.
 * Erros de validação saem como IllegalArgumentException com mensagem pronta para a tela.
 */
public class ProdutoController {

    public static final String[] TIPOS = {"Camisa", "Camiseta", "Jaqueta", "Agasalho", "Calça",
            "Bermuda", "Short-saia", "Meia", "Boné", "Outro"};
    public static final String[] TAMANHOS = {"PP", "P", "M", "G", "GG", "XG",
            "2", "4", "6", "8", "10", "12", "14", "16"};

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();

    public List<Produto> buscar(String texto, Integer idCategoria, String tamanho) {
        return produtoDAO.buscar(texto, idCategoria, tamanho);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorDAO.listar();
    }

    public Categoria criarCategoria(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Informe o nome da categoria.");
        Categoria c = new Categoria(0, nome.trim(), null);
        categoriaDAO.inserir(c);
        return c;
    }

    public Fornecedor criarFornecedor(String razaoSocial, String cnpj, String telefone, String email) {
        if (razaoSocial == null || razaoSocial.isBlank())
            throw new IllegalArgumentException("Informe a razão social do fornecedor.");
        if (cnpj == null || cnpj.isBlank())
            throw new IllegalArgumentException("Informe o CNPJ do fornecedor.");
        Fornecedor f = new Fornecedor(0, razaoSocial.trim(), cnpj.trim(),
                vazioParaNull(telefone), vazioParaNull(email));
        fornecedorDAO.inserir(f);
        return f;
    }

    /** Cria (id == null) ou atualiza (id != null) um uniforme. */
    public Produto salvar(Integer id, String tipo, String descricao, String tamanho,
                          String precoTxt, String quantidadeTxt, String minimoTxt,
                          Categoria categoria, Fornecedor fornecedor, String observacoes) {
        if (tipo == null || tipo.isBlank())
            throw new IllegalArgumentException("Selecione o tipo de uniforme.");
        if (tamanho == null || tamanho.isBlank())
            throw new IllegalArgumentException("Selecione o tamanho.");
        if (categoria == null)
            throw new IllegalArgumentException("Selecione a categoria (use o botão + para criar uma).");
        if (fornecedor == null)
            throw new IllegalArgumentException("Selecione o fornecedor (use o botão + para cadastrar um).");

        double preco = lerDouble(precoTxt, "Preço");
        int quantidade = lerInt(quantidadeTxt, "Quantidade em estoque");
        int minimo = lerInt(minimoTxt, "Estoque mínimo");
        if (preco < 0) throw new IllegalArgumentException("O preço não pode ser negativo.");
        if (quantidade < 0) throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        if (minimo < 0) throw new IllegalArgumentException("O estoque mínimo não pode ser negativo.");

        if (id == null) {
            Produto novo = new Produto(0, tipo.trim(), vazioParaNull(descricao), tamanho.trim(), null,
                    preco, quantidade, minimo, categoria, fornecedor, vazioParaNull(observacoes));
            produtoDAO.inserir(novo);
            return novo;
        }

        Produto existente = produtoDAO.buscarPorId(id);
        if (existente == null)
            throw new IllegalArgumentException("Este uniforme não existe mais. Volte e atualize a lista.");
        existente.setNome(tipo.trim());
        existente.setDescricao(vazioParaNull(descricao));
        existente.setTamanho(tamanho.trim());
        existente.setPreco(preco);
        existente.setQuantidadeEstoque(quantidade);
        existente.setEstoqueMinimo(minimo);
        existente.setCategoria(categoria);
        existente.setFornecedor(fornecedor);
        existente.setObservacoes(vazioParaNull(observacoes));
        produtoDAO.atualizar(existente);
        return existente;
    }

    public void excluir(int id) {
        produtoDAO.excluir(id);
    }

    // ---------------------------------------------------------------- auxiliares

    private double lerDouble(String txt, String campo) {
        try {
            return Double.parseDouble(txt.trim().replace(',', '.'));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(campo + " inválido. Use apenas números (ex.: 45,50).");
        }
    }

    private int lerInt(String txt, String campo) {
        try {
            return Integer.parseInt(txt.trim());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(campo + " inválido. Use apenas números inteiros.");
        }
    }

    private String vazioParaNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
