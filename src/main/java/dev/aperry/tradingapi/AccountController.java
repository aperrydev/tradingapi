package dev.aperry.tradingapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@RestController
public class AccountController {

    private final AccountRepository repo;

    public AccountController(AccountRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Map<String, String> body) throws SQLException {
        String username = body.get("username");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        int age = Integer.parseInt(body.get("age"));

        if (repo.usernameExists(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "username taken"));
        }

        long id = repo.createAccount(username, firstName, lastName, age, new BigDecimal("10000"));
        return ResponseEntity.status(201).body(Map.of(
                "id", id,
                "username", username,
                "balance", "10000"
        ));
    }
}