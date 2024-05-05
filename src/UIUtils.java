import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class UIUtils {
    /**
     * Este metodo se encarga de crear un modal y añadirle los componentes que se le pasen
     * de forma vertical uno debajo de otro
     * @param components -Los componentes que se mostraran en el modal
     * @return JDialog -El modal creado
     * Uso:
     * JDialog dialog=createModal(new JLabel("Label 1"), new JTextField("Text 1"), new JLabel("Label 2"), new JTextField("Text 2"));
     * dialog.setVisible(true);
     *
     */
    protected static JDialog createModal(JComponent ...components){
        JDialog dialog = new JDialog();
        dialog.setSize(300,300);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        for (JComponent component : components) {
            dialog.add(component);
        }
        return dialog;
    }
    /**
     * Este metodo se encarga de crear un combobox y añadirlo a un panel
     * @param labelText -El texto que se mostrara en el label
     * @param items -Los items que se mostraran en el combobox
     * @param tab -El panel al que se añadira el combobox
     * @param jsonData -Los datos que se mostraran en la tabla
     * @param filterColumns -Los filtros que se aplicaran a la tabla
     * @param filterColumn -La columna por la que se filtrara
     * @param filterFunction -La funcion que se aplicara al filtrar
     * @param empScrollPane -El scrollpane que contendra la tabla
     * @return JPanel -El panel que contiene el combobox
     * Uso:
     * createComboBox("Label", new String[]{"Item 1", "Item 2"}, panel, jsonData, filterColumns, "Columna", filterFunction, empScrollPane);
     */
    protected static JPanel createComboBox(String labelText, String[] items, JComponent tab, JSONArray jsonData, TableFilter[] filterColumns, String filterColumn, FilterInterface filterFunction,JScrollPane empScrollPane) {
        JPanel panel = new JPanel();
        JComboBox<String> comboBox = new JComboBox<>(items);
        JLabel label = new JLabel(labelText);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(comboBox);

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox<?> cb = (JComboBox<?>) e.getSource();
                String selected = (String) cb.getSelectedItem();
                TableFilter filter = new TableFilter(filterColumn, selected);
                filterFunction.applyFilter(filterColumns, filter);
                getTable(tab, jsonData, filterColumns,empScrollPane);
            }
        });

        tab.add(panel);
        return panel;
    }
    /**
     * Este metodo se encarga de crear una tabla y añadirla a un panel
     * @param tab -El panel al que se añadira la tabla
     * @param data -Los datos que se mostraran en la tabla
     * @param filter -Los filtros que se aplicaran a la tabla
     * @param scrollPane -El scrollpane que contendra la tabla
     * Uso:
     * getTable(panel, jsonData, filterColumns, empScrollPane);
     */
    protected static  void getTable(JComponent tab, JSONArray data,TableFilter[] filter,JScrollPane scrollPane) {
        JTable table = new JTable();
        // Crear un DefaultTableModel para el JTable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que todas las celdas sean no editables
                return false;
            }
        };
        //print all filters
        for (TableFilter tableFilter : filter) {
            System.out.println(tableFilter);
        }
        // Asignar el modelo al JTable
        table.setModel(model);
        try {
            JSONArray jsonArray = data;
            JSONObject firstObject = jsonArray.getJSONObject(0);
            Iterator<String> keys = firstObject.keys();
            while (keys.hasNext()) {
                String columnName = keys.next();
                model.addColumn(columnName);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rowData = jsonArray.getJSONObject(i);
                Object[] row = new Object[model.getColumnCount()];
                if (filter.length > 0) {
                    boolean addRow = true;
                    for (TableFilter tableFilter : filter) {

                        //if the filter has value null then skip the filter
                        if (tableFilter.getValue() == null) {
                            continue;
                        }
                        if (!rowData.get(tableFilter.getKey()).equals(tableFilter.getValue())) {
                            addRow = false;
                            break;
                        }
                    }
                    if (!addRow) {
                        continue;
                    }
                }
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row[j] = rowData.get(model.getColumnName(j));
                }
                System.out.println(row[0]);
                model.addRow(row);
            }
            scrollPane.setViewportView(table);
            tab.add(scrollPane);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
