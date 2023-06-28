package org.agh;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NBPApiService {
    private static final Logger logger = LogManager.getLogger(NBPApiService.class);

    private static final String API_URL = "https://api.nbp.pl/api/exchangerates/rates/A/";
    private static final String GOLD_API_URL = "http://api.nbp.pl/api/cenyzlota";
    private static final String CURRENCIES_API_URL = "https://api.nbp.pl/api/exchangerates/tables/a/";

    public String getCurrencyRates(String currencyCode, Integer last) throws IOException {

        logger.info("Pobieranie danych z API NBP dla waluty: " + currencyCode);

        String apiUrl;
        if (currencyCode.equals("GOLD"))
            apiUrl = GOLD_API_URL;
        else
            apiUrl = API_URL + currencyCode;

        apiUrl += "/last/" + last + "/?format=json";

        URL url = new URL(apiUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }

    public static List<String> getAllCurrencies() throws IOException {

        logger.info("Pobieranie wszystkich dostepnych nazw kursow z NBP API.");

        List<String> currencies = new ArrayList<>();

        URL url = new URL(CURRENCIES_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { //sprawdzenie czy kod odpowiedzi jest ok
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                JSONArray ratesArray = jsonObject.getJSONArray("rates");
                for (int i = 0; i < ratesArray.length(); i++) {
                    JSONObject rateObject = ratesArray.getJSONObject(i);
                    String currencyCode = rateObject.getString("code");
                    currencies.add(currencyCode);
                }
            }
        } else {
            logger.error("Błąd podczas pobierania danych z API NBP. Kod: " + responseCode);
        }

        connection.disconnect();
        currencies.add("GOLD");

        return currencies;
    }
}
