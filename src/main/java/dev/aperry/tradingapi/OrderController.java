package dev.aperry.tradingapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.ResponseEntity.*;


@RestController
public class OrderController {

    private final AccountRepository repo;

    public OrderController(AccountRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, String> body) throws Exception {

        String username = body.get("username");
        String ticker = body.get("ticker");
        int shares = Integer.parseInt(body.get("shares"));
        String side = body.get("side");

        TradingAccount account = repo.loadAccount(username);
        if (account == null) {
            return status(404).body(Map.of("error", "account not found"));
        }

        repo.loadPositions(account);

        BigDecimal price;
        try {
            price = StockFetcher.getPrice(ticker);
        } catch (Exception e) {
            return badRequest().body(Map.of("error", "couldn't get a price: " + e.getMessage()));
        }

        try {
            if (side == null) {
                return badRequest().body(Map.of("error", "side is required: 'buy' or 'sell'"));
            }
            if (side.equalsIgnoreCase("buy")) {
                account.buyStock(ticker, shares, price);
                repo.savePositions(account.getId(), account.getPositions());
                repo.updateBalance(account.getId(), account.getBalance());
                return ok(Map.of(
                        "status", "filled",
                        "side", "buy",
                        "ticker", ticker.trim().toUpperCase(),
                        "shares", shares,
                        "price", price.toString(),
                        "balance", account.getBalance().toString()
                ));
            } else if (side.equalsIgnoreCase("sell")) {
                BigDecimal pnl = account.sellStock(ticker, shares, price);
                repo.savePositions(account.getId(), account.getPositions());
                repo.updateBalance(account.getId(), account.getBalance());
                return ok(Map.of(
                        "status", "filled",
                        "side", "sell",
                        "ticker", ticker.trim().toUpperCase(),
                        "shares", shares,
                        "price", price.toString(),
                        "realizedPnL", pnl.toString(),
                        "balance", account.getBalance().toString()
                ));
            } else {
                return badRequest().body(Map.of("error", "side must be 'buy' or 'sell'"));
            }
        } catch (IllegalArgumentException e) {
            return badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}