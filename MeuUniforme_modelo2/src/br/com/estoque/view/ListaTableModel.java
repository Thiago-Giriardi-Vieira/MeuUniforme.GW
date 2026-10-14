package br.com.estoque.view;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Modelo de tabela genérico: recebe os títulos das colunas e uma função
 * que diz qual valor mostrar em cada (item, coluna).
 */
public class ListaTableModel<T> extends AbstractTableModel {

    private final String[] colunas;
    private final BiFunction<T, Integer, Object> extrator;
    private List<T> itens = new ArrayList<>();

    public ListaTableModel(String[] colunas, BiFunction<T, Integer, Object> extrator) {
        this.colunas = colunas;
        this.extrator = extrator;
    }

    public void setItens(List<T> novos) {
        this.itens = new ArrayList<>(novos);
        fireTableDataChanged();
    }

    public T getItem(int linha) {
        return itens.get(linha);
    }

    public List<T> getItens() {
        return itens;
    }

    @Override public int getRowCount() { return itens.size(); }
    @Override public int getColumnCount() { return colunas.length; }
    @Override public String getColumnName(int coluna) { return colunas[coluna]; }

    @Override
    public Object getValueAt(int linha, int coluna) {
        return extrator.apply(itens.get(linha), coluna);
    }
}
