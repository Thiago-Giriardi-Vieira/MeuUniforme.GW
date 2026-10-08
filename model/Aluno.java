package br.com.estoque.model;

public class Aluno {
    private int idAluno;
    private String nome;
    private String matricula;
    private String turma;
    private String nomeResponsavel;
    private String telefone;

    public Aluno(int idAluno, String nome, String matricula, String turma,
                 String nomeResponsavel, String telefone) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.matricula = matricula;
        this.turma = turma;
        this.nomeResponsavel = nomeResponsavel;
        this.telefone = telefone;
    }

    public int getIdAluno() { return idAluno; }
    public void setIdAluno(int idAluno) { this.idAluno = idAluno; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }
    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return idAluno + " - " + nome + " | Matrícula: " + matricula;
    }
}
