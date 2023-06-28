package org.agh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CurrencyAppService implements Runnable {
    private final String currencyData;
    private final String selectedCurrency;
    private final Integer selectedLast;
    private final JTable table;

    private static final Logger logger = LogManager.getLogger(CurrencyAppService.class);

    public CurrencyAppService(String currencyData, String selectedCurrency, Integer selectedLast, JTable table) {
        this.currencyData = currencyData;
        this.selectedCurrency = selectedCurrency;
        this.selectedLast = selectedLast;
        this.table = table;
    }

    public JTable getTable() {
        return table;
    }

    @Override
    public void run() {
        NBPApiService apiService = new NBPApiService();

        logger.info("Generowanie wykresu dla waluty: " + selectedCurrency + ", w ciągu " + selectedLast + " ostatnich dni.");
        try{
            DefaultCategoryDataset dataset = processData(currencyData);

            JFreeChart chart = ChartFactory.createLineChart(
                    "Wykres kursu " + selectedCurrency, //tytuł z nazwą waluty
                    "Data",
                    "Kurs",
                    dataset
            );
            CategoryPlot plot = chart.getCategoryPlot();
            ValueAxis yAxis = plot.getRangeAxis();

            double minRate = Double.MAX_VALUE;
            double maxRate = Double.MIN_VALUE;

            for (int series = 0; series < dataset.getRowCount(); series++) {
                for (int item = 0; item < dataset.getColumnCount(); item++) {
                    double rate = dataset.getValue(series, item).doubleValue();
                    minRate = Math.min(minRate, rate);
                    maxRate = Math.max(maxRate, rate);
                }
            }

            double rangePadding = (maxRate - minRate) * 0.1; //margines na gorze i na dole zakresu

            yAxis.setRange(minRate - rangePadding, maxRate + rangePadding);

            ChartFrame frame = new ChartFrame("Wykres kursów walut", chart);
            frame.pack();
            frame.setVisible(true);

            updateTable(currencyData);
        }catch(Exception e){
            logger.error("blad parsowania.",e);
            JOptionPane.showMessageDialog(null, "Wystąpił błąd podczas parsowania danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }

    }

    public DefaultCategoryDataset processData(String currencyData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        logger.debug("Przetwarzanie danych kursów walut.");

        if (selectedCurrency.equals("GOLD")) {
            JSONArray jsonArray = new JSONArray(currencyData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rateObject = jsonArray.getJSONObject(i);
                String date = rateObject.getString("data");
                double rate = rateObject.getDouble("cna");
                dataset.addValue(rate, "GOLD", date);
            }
        } else {
            JSONObject json = new JSONObject(currencyData);
            JSONArray ratesArray = json.getJSONArray("rates");
            for (int i = 0; i < ratesArray.length(); i++) {
                JSONObject rateObject = ratesArray.getJSONObject(i);
                String date = rateObject.getString("effectiveDate");
                double rate = rateObject.getDouble("mid");
                dataset.addValue(rate, selectedCurrency, date);
            }
        }

        return dataset;
    }

    public void updateTable(String currencyData) {
        logger.info("Aktualizacja tabeli kursów walut.");

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Data");
        model.addColumn("Kurs");

        if (selectedCurrency.equals("GOLD")) {
            JSONArray jsonArray = new JSONArray(currencyData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rateObject = jsonArray.getJSONObject(i);
                String date = rateObject.getString("data");
                double rate = rateObject.getDouble("cena");
                model.addRow(new Object[]{date, rate});
            }
        } else {
            JSONObject json = new JSONObject(currencyData);
            JSONArray ratesArray;
            if (json.has("rates")) {
                ratesArray = json.getJSONArray("rates");
            } else {
                ratesArray = new JSONArray().put(json);
            }

            for (int i = 0; i < ratesArray.length(); i++) {
                JSONObject rateObject = ratesArray.getJSONObject(i);
                String date = rateObject.getString("effectiveDate");
                double rate = rateObject.getDouble("mid");
                model.addRow(new Object[]{date, rate});
            }
        }

        SwingUtilities.invokeLater(() -> table.setModel(model));
    }


}
