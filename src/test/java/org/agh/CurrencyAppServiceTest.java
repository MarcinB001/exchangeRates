package org.agh;

import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
class CurrencyAppServiceTest {
    private CurrencyAppService currencyAppService;

    @Test
    public void processDataWithGold() {
        String currencyData = "[{\"data\":\"2000-01-01\",\"cena\":5.0},{\"data\":\"2023-01-01\",\"cena\":10.0}]";

        currencyAppService = new CurrencyAppService(null, "GOLD", 2, null);

        DefaultCategoryDataset dataset = currencyAppService.processData(currencyData);

        Assertions.assertEquals(5.0, dataset.getValue(0, 0).doubleValue(), 0.001);
        Assertions.assertEquals(10.0, dataset.getValue(0, 1).doubleValue(), 0.001);
        Assertions.assertEquals("GOLD", dataset.getRowKey(0));
    }

    @Test
    public void processDataWithOtherCurrency() {
        currencyAppService = new CurrencyAppService(null, "EUR", 2, null);

        String currencyData = "{\"rates\":[{\"effectiveDate\":\"2000-01-01\",\"mid\":5.0},{\"effectiveDate\":\"2023-01-01\",\"mid\":10.0}]}";

        DefaultCategoryDataset dataset = currencyAppService.processData(currencyData);

        Assertions.assertEquals(5.0, dataset.getValue(0, 0).doubleValue(), 0.001);
        Assertions.assertEquals(10.0, dataset.getValue(0, 1).doubleValue(), 0.001);
        Assertions.assertEquals("EUR", dataset.getRowKey(0));
    }

    @Test
    public void updateTableWithGold() {
        String currencyData = "[{\"data\":\"2000-01-01\",\"cena\":5.0},{\"data\":\"2023-01-01\",\"cena\":10.0}]";

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Data");
        model.addColumn("Kurs");
        model.addRow(new Object[]{"2000-01-01", 5.0});
        model.addRow(new Object[]{"2023-01-01", 10.0});

        JTable table = new JTable(model);
        currencyAppService = new CurrencyAppService(currencyData, "GOLD", 2, table);

        currencyAppService.updateTable(currencyData);
        DefaultTableModel actualModel = (DefaultTableModel) currencyAppService.getTable().getModel();

        Assertions.assertEquals(model.getRowCount(), actualModel.getRowCount());
        Assertions.assertEquals(model.getColumnCount(), actualModel.getColumnCount());
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Assertions.assertEquals(model.getValueAt(row, col), actualModel.getValueAt(row, col));
            }
        }
    }

    @Test
    public void updateTableWithCurrencyEUR() {
        String currencyData = "{\"rates\":[{\"effectiveDate\":\"2000-01-01\",\"mid\":5.0},{\"effectiveDate\":\"2023-01-01\",\"mid\":10.0}]}";

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Data");
        model.addColumn("Kurs");
        model.addRow(new Object[]{"2000-01-01", 5.0});
        model.addRow(new Object[]{"2023-01-01", 10.0});

        JTable table = new JTable(model);
        currencyAppService = new CurrencyAppService(currencyData, "EUR", 2, table);

        currencyAppService.updateTable(currencyData);
        DefaultTableModel actualModel = (DefaultTableModel) currencyAppService.getTable().getModel();

        Assertions.assertEquals(model.getRowCount(), actualModel.getRowCount());
        Assertions.assertEquals(model.getColumnCount(), actualModel.getColumnCount());
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Assertions.assertEquals(model.getValueAt(row, col), actualModel.getValueAt(row, col));
            }
        }
    }


}