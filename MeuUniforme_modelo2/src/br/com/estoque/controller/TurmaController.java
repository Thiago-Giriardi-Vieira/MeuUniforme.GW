package br.com.estoque.controller;

import br.com.estoque.dao.AlunoDAO;
import br.com.estoque.dao.TurmaDAO;
import br.com.estoque.model.Turma;

import java.util.List;
import java.util.Map;

public class TurmaController {

    private final TurmaDAO turmaDAO = new TurmaDAO();
    private final AlunoDAO alunoDAO = new AlunoDAO();

    public List<Turma> listar() {
        return turmaDAO.listar();
    }

    public Map<Integer, Integer> contarAlunosPorTurma() {
        return turmaDAO.contarAlunosPorTurma();
    }

    public Turma salvar(Integer id, String nome, String anoSerie) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Informe o nome da turma (ex.: 7º A).");
        String serie = (anoSerie == null || anoSerie.isBlank()) ? null : anoSerie.trim();

        if (id == null) {
            Turma nova = new Turma(0, nome.trim(), serie);
            turmaDAO.inserir(nova);
            return nova;
        }
        Turma existente = turmaDAO.buscarPorId(id);
        if (existente == null)
            throw new IllegalArgumentException("Esta turma não existe mais. Atualize a lista.");
        existente.setNome(nome.trim());
        existente.setAnoSerie(serie);
        turmaDAO.atualizar(existente);
        return existente;
    }

    public void excluir(int id) {
        int alunos = alunoDAO.listarPorTurma(id).size();
        if (alunos > 0)
            throw new IllegalArgumentException("Não é possível excluir: a turma possui " + alunos
                    + " aluno(s). Mude os alunos de turma ou exclua-os antes.");
        turmaDAO.excluir(id);
    }
}
