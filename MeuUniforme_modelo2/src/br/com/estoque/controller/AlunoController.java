package br.com.estoque.controller;

import br.com.estoque.dao.AlunoDAO;
import br.com.estoque.model.Aluno;
import br.com.estoque.model.Turma;

import java.util.List;

public class AlunoController {

    private final AlunoDAO alunoDAO = new AlunoDAO();

    public List<Aluno> listar() {
        return alunoDAO.listar();
    }

    public List<Aluno> listarPorTurma(int idTurma) {
        return alunoDAO.listarPorTurma(idTurma);
    }

    public Aluno salvar(Integer id, String nome, String matricula, Turma turma,
                        String responsavel, String telefone) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Informe o nome do aluno.");
        if (matricula == null || matricula.isBlank())
            throw new IllegalArgumentException("Informe a matrícula.");
        if (turma == null)
            throw new IllegalArgumentException("Selecione a turma do aluno.");

        Aluno mesmaMatricula = alunoDAO.buscarPorMatricula(matricula.trim());
        if (mesmaMatricula != null && (id == null || mesmaMatricula.getIdAluno() != id))
            throw new IllegalArgumentException("Já existe um aluno com esta matrícula.");

        if (id == null) {
            Aluno novo = new Aluno(0, nome.trim(), matricula.trim(), turma,
                    vazioParaNull(responsavel), vazioParaNull(telefone));
            alunoDAO.inserir(novo);
            return novo;
        }
        Aluno existente = alunoDAO.buscarPorId(id);
        if (existente == null)
            throw new IllegalArgumentException("Este aluno não existe mais. Atualize a lista.");
        existente.setNome(nome.trim());
        existente.setMatricula(matricula.trim());
        existente.setTurma(turma);
        existente.setNomeResponsavel(vazioParaNull(responsavel));
        existente.setTelefone(vazioParaNull(telefone));
        alunoDAO.atualizar(existente);
        return existente;
    }

    public void excluir(int id) {
        alunoDAO.excluir(id);
    }

    private String vazioParaNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
