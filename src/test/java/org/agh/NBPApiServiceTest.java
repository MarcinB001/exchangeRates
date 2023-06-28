package org.agh;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

class NBPApiServiceTest {

    @Test
    public void getCurrencyRates() throws IOException {
        NBPApiService apiService = new NBPApiService();

        String currencyCode = "EUR";
        Integer last = 5;
        String currencyRates = apiService.getCurrencyRates(currencyCode, last);

        Assertions.assertNotNull(currencyRates);

        JSONObject json = new JSONObject(currencyRates);
        String actualCurrencyCode = json.getString("code");
        JSONArray ratesArray = json.getJSONArray("rates");

        Assertions.assertEquals(currencyCode, actualCurrencyCode);
        Assertions.assertEquals(last, ratesArray.length());
    }

    @Test
    public void getAllCurrencies() throws IOException {
        List<String> currencies = NBPApiService.getAllCurrencies();

        Assertions.assertNotNull(currencies);

        Assertions.assertTrue(currencies.contains("USD"));
        Assertions.assertTrue(currencies.contains("EUR"));
        Assertions.assertTrue(currencies.contains("GBP"));
        Assertions.assertTrue(currencies.contains("CAD"));
        Assertions.assertTrue(currencies.contains("GOLD"));
    }

}