package br.com.estoque.view;

import br.com.estoque.controller.TurmaController;
import br.com.estoque.model.Turma;

import javax.swing.*;
import java.awt.*;

/** Janela "Nova turma" / "Editar turma". */
public class TurmaDialog extends DialogoBase {

    private final TurmaController controller;
    private final Turma existente;
    private final JTextField txtNome = Estilo.campoComDica("Ex.: 7º A", 18);
    private final JTextField txtAnoSerie = Estilo.campoComDica("Ex.: 7º ano", 18);

    public TurmaDialog(Frame dono, TurmaController controller, Turma existente) {
        super(dono, existente == null ? "Nova turma" : "Editar turma");
        this.controller = controller;
        this.existente = existente;

        Estilo.linhaForm(form, 0, "Turma *", txtNome);
        Estilo.linhaForm(form, 1, "Ano/Série", txtAnoSerie);
        if (existente != null) {
            txtNome.setText(existente.getNome());
            txtAnoSerie.setText(existente.getAnoSerie() == null ? "" : existente.getAnoSerie());
        }
        montar();
    }

    @Override
    protected void aoSalvar() {
        try {
            controller.salvar(existente == null ? null : existente.getIdTurma(),
                    txtNome.getText(), txtAnoSerie.getText());
            concluir();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
