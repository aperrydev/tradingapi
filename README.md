# Trading API

A Spring Boot REST API for a stock trading simulator — live market prices,
average-cost basis tracking, realized P&L, and SQLite persistence.

Backend continuation of my console-based [TradingSimulator](https://github.com/aperrydev/TradingSimulator):
the domain layer (cost-basis math, BigDecimal money handling, JDBC repository)
was built and unit-tested there, then migrated here behind REST endpoints.

## Endpoints

| Method | Path                    | Description                              |
|--------|-------------------------|------------------------------------------|
| GET    | `/ping`                 | Health check                             |
| POST   | `/accounts`             | Create an account (starts with $10,000)  |
| GET    | `/portfolio/{username}` | Current positions with average cost      |
| POST   | `/orders`               | Place a buy or sell order at live price  |

### Create an account

```bash
curl -X POST localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"username":"tony","firstName":"Anthony","lastName":"Perry","age":"19"}'
```

Returns `201 Created`:

```json
{"id":1,"username":"tony","balance":"10000"}
```

### Place an order

```bash
curl -X POST localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"username":"tony","ticker":"AAPL","shares":"5","side":"buy"}'
```

Sell orders return realized P&L computed against the position's average cost:

```json
{"status":"filled","side":"sell","ticker":"AAPL","shares":2,
 "price":"319.9700","realizedPnL":"0.2700","balance":"7441.5900"}
```

### View a portfolio

```bash
curl localhost:8080/portfolio/tony
```

```json
[{"ticker":"AAPL","shares":8,"avgCost":319.8350}]
```

Errors use standard HTTP semantics: `404` for unknown accounts, `400` for
invalid orders (insufficient funds, unknown tickers, oversells).

## Design notes

* **Exact monetary math** — all money is `BigDecimal` end to end and stored
  as text in SQLite, so no floating-point drift ever touches a balance.
* **Average-cost basis** — buys update a weighted average per position;
  sells compute realized P&L against it.
* **Stateless request handling** — every order loads account state fresh,
  mutates, and persists; no session state lives on the server.
* **Layered architecture** — controllers → domain (`TradingAccount`) →
  repository (parameterized JDBC). The domain layer throws on invalid
  operations; controllers translate to HTTP status codes.

## Setup

1. Get a free API key at [alphavantage.co](https://www.alphavantage.co/)
2. Create `config.properties` in the project root:
```
   API_KEY=your_key_here
```
3. Run `TradingapiApplication` — the SQLite database (`trading.db`) and
   schema are created automatically on startup.

## Built with

Java · Spring Boot · SQLite (JDBC) · Alpha Vantage API · Maven

## Roadmap

* [ ] Authentication with Spring Security
* [ ] Controller-layer tests
* [ ] Quote caching to respect API rate limits
* [ ] PostgreSQL + cloud deployment