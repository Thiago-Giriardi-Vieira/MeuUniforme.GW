package br.com.estoque.model;

public class Turma {
    private int idTurma;
    private String nome;       // ex.: 7º A
    private String anoSerie;   // ex.: 7º ano

    public Turma(int idTurma, String nome, String anoSerie) {
        this.idTurma = idTurma;
        this.nome = nome;
        this.anoSerie = anoSerie;
    }

    public int getIdTurma() { return idTurma; }
    public void setIdTurma(int idTurma) { this.idTurma = idTurma; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getAnoSerie() { return anoSerie; }
    public void setAnoSerie(String anoSerie) { this.anoSerie = anoSerie; }

    @Override
    public String toString() {
        return (anoSerie == null || anoSerie.isBlank()) ? nome : nome + " (" + anoSerie + ")";
    }
}
