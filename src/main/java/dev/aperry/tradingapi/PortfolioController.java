package dev.aperry.tradingapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;

@RestController
public class PortfolioController {

    private final AccountRepository repo;

    public PortfolioController(AccountRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/portfolio/{username}")
    public ResponseEntity<?> getPortfolio(@PathVariable String username) throws SQLException {
        TradingAccount account = repo.loadAccount(username);
        if (account == null) {
            return ResponseEntity.status(404).body(Map.of("error", "account not found"));
        }
        repo.loadPositions(account);
        return ResponseEntity.ok(account.getPositions());
    }
}