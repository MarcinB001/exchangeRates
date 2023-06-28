package org.agh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CurrencyApp();
            } catch (IOException e) {
                logger.error("Wystąpił błąd podczas uruchamiania aplikacji.", e);
                JOptionPane.showMessageDialog(null, "Wystąpił błąd podczas uruchamiania aplikacji.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}