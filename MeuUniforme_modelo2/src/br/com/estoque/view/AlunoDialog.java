package br.com.estoque.view;

import br.com.estoque.controller.AlunoController;
import br.com.estoque.model.Aluno;
import br.com.estoque.model.Turma;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Janela "Novo aluno" / "Editar aluno". */
public class AlunoDialog extends DialogoBase {

    private final AlunoController controller;
    private final Aluno existente;
    private final JTextField txtNome = Estilo.campoComDica("Nome do aluno", 22);
    private final JTextField txtMatricula = Estilo.campoComDica("Número da matrícula", 22);
    private final JComboBox<Turma> cmbTurma = Estilo.combo(new JComboBox<Turma>());
    private final JTextField txtResponsavel = Estilo.campoComDica("Nome do responsável", 22);
    private final JTextField txtTelefone = Estilo.campoComDica("(00) 00000-0000", 22);

    /**
     * @param turmas       turmas disponíveis para escolher
     * @param existente    aluno a editar, ou null para cadastrar
     * @param turmaPadrao  turma já selecionada ao cadastrar (pode ser null)
     */
    public AlunoDialog(Frame dono, AlunoController controller, List<Turma> turmas,
                       Aluno existente, Turma turmaPadrao) {
        super(dono, existente == null ? "Novo aluno" : "Editar aluno");
        this.controller = controller;
        this.existente = existente;

        for (Turma t : turmas) cmbTurma.addItem(t);

        Estilo.linhaForm(form, 0, "Nome *", txtNome);
        Estilo.linhaForm(form, 1, "Matrícula *", txtMatricula);
        Estilo.linhaForm(form, 2, "Turma *", cmbTurma);
        Estilo.linhaForm(form, 3, "Responsável", txtResponsavel);
        Estilo.linhaForm(form, 4, "Telefone", txtTelefone);

        Turma selecionar = existente != null ? existente.getTurma() : turmaPadrao;
        if (selecionar != null) selecionarTurma(selecionar.getIdTurma());
        if (existente != null) {
            txtNome.setText(existente.getNome());
            txtMatricula.setText(existente.getMatricula());
            txtResponsavel.setText(existente.getNomeResponsavel() == null ? "" : existente.getNomeResponsavel());
            txtTelefone.setText(existente.getTelefone() == null ? "" : existente.getTelefone());
        }
        montar();
    }

    private void selecionarTurma(int idTurma) {
        for (int i = 0; i < cmbTurma.getItemCount(); i++) {
            if (cmbTurma.getItemAt(i).getIdTurma() == idTurma) {
                cmbTurma.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected void aoSalvar() {
        try {
            controller.salvar(existente == null ? null : existente.getIdAluno(),
                    txtNome.getText(), txtMatricula.getText(), (Turma) cmbTurma.getSelectedItem(),
                    txtResponsavel.getText(), txtTelefone.getText());
            concluir();
        } catch (RuntimeException e) {
            Estilo.erro(this, e);
        }
    }
}
