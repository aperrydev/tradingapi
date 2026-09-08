package dev.aperry.tradingapi;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class StockFetcher {
        public StockFetcher() {
        }
        public static BigDecimal getPrice(String symbol) throws Exception {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            String apiKey = props.getProperty("API_KEY");
            String urlString = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            JSONObject json = new JSONObject(response.toString());
            if(!json.has("Global Quote")){
                throw new IllegalStateException("Bad response for " + symbol + " (probably rate-limited): " +json);
            }
            JSONObject quote = json.getJSONObject("Global Quote");
            if(!quote.has("05. price")){
                throw new IllegalArgumentException("'" + symbol + "' isn't a valid ticker.");
            }
            BigDecimal price = quote.getBigDecimal("05. price");
            return price;
        }
    }
