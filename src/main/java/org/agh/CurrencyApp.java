package org.agh;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CurrencyApp extends JFrame {
    private JButton generateButton;
    private JTable table;
    private JComboBox<String> currencyComboBox;
    private JComboBox<Integer> lastComboBox;
    private List<String> availableCurrencies;

    private static final Logger logger = LogManager.getLogger(CurrencyApp.class);

    public CurrencyApp() throws IOException {

        logger.info("Tworzenie okna.");
        setTitle("Aplikacja kursów walut");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        generateButton = new JButton("Generuj wykres");
        table = new JTable();
        currencyComboBox = new JComboBox<>();
        lastComboBox = new JComboBox<>();

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel currencyLabel = new JLabel("Waluta:");
        JLabel lastLabel = new JLabel("Ilość ostatnich dni:");
        buttonPanel.add(currencyLabel);
        buttonPanel.add(currencyComboBox);
        buttonPanel.add(lastLabel);
        buttonPanel.add(lastComboBox);
        buttonPanel.add(generateButton);
        add(buttonPanel, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);


        for(int i = 255;i>1;i--){
            lastComboBox.addItem(i);
        }


        generateButton.addActionListener(e -> {
            String selectedCurrency = (String) currencyComboBox.getSelectedItem();
            Integer selectedLast = (Integer) lastComboBox.getSelectedItem();

            if (selectedCurrency == null) {
                logger.error("Nie wybrano waluty");
                JOptionPane.showMessageDialog(this, "Wybierz walutę.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            NBPApiService apiService = new NBPApiService();

            try {
                String currencyData = apiService.getCurrencyRates(selectedCurrency, selectedLast);
                Runnable currencyDataProcessor = new CurrencyAppService(currencyData, selectedCurrency, selectedLast, table);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(currencyDataProcessor);
                executor.shutdown();
            } catch (Exception ex) {
                logger.error("Wystąpił błąd podczas pobierania danych z API NBP.", ex);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas pobierania danych z API NBP.", "Błąd", JOptionPane.ERROR_MESSAGE);
                });
            }
        });


        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        availableCurrencies = NBPApiService.getAllCurrencies();
        //dodawanie waluty do listy comboboxa
        for (String currency : availableCurrencies) {
            currencyComboBox.addItem(currency);
        }
    }
}
