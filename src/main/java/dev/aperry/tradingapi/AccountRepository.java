package dev.aperry.tradingapi;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
    private final String url = "jdbc:sqlite:trading.db";

    public void initialize() throws SQLException{
        try(Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()){
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "balance TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS positions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_id INTEGER NOT NULL, " +
                    "ticker TEXT NOT NULL, " +
                    "shares INTEGER NOT NULL, " +
                    "avg_cost TEXT NOT NULL, " +
                    "FOREIGN KEY (account_id) REFERENCES accounts(id))");
        }
    }
    public long createAccount(String username, String first_name,
                              String last_name, int age, BigDecimal balance) throws SQLException{
        String sql = "INSERT INTO accounts (username, first_name, last_name, age, balance) " +
                "VALUES (?,?,?,?,?)";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, first_name);
            ps.setString(3, last_name);
            ps.setInt(4, age);
            ps.setString(5, balance.toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getLong(1);
        }
    }
    public boolean usernameExists(String username) throws SQLException{
        String sql = "SELECT id FROM accounts WHERE username = ?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
    public TradingAccount loadAccount(String username) throws SQLException{
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                return null;
            }
            long id = rs.getLong("id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            int age = rs.getInt("age");
            BigDecimal balance = new BigDecimal(rs.getString("balance"));
            return new TradingAccount(id, firstName, lastName, age, balance);
        }
    }
    public void savePositions(long accountId, Collection<Position> positions) throws SQLException{
        try(Connection conn  = DriverManager.getConnection(url)){
            try(PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM positions WHERE account_id = ?")){
                del.setLong(1, accountId);
                del.executeUpdate();
            }
            try(PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO positions (account_id, ticker, shares, avg_cost) VALUES (?,?,?,?)")){
                for(Position p : positions){
                    ins.setLong(1, accountId);
                    ins.setString(2, p.getTicker());
                    ins.setInt(3, p.getShares());
                    ins.setString(4, p.getAvgCost().toString());
                    ins.executeUpdate();
                }

            }

        }
    }
    public void loadPositions(TradingAccount account) throws SQLException{
        String sql = "SELECT * FROM positions WHERE account_id = ?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement ps = conn.prepareStatement(sql)){
           ps.setLong(1, account.getId());
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
               String ticker = rs.getString("ticker");
               int shares = rs.getInt("shares");
               BigDecimal avg = new BigDecimal(rs.getString("avg_cost"));
               account.addPosition(new Position(ticker,shares,avg));
           }
        }
    }
    public void updateBalance(long accountId, BigDecimal balance) throws SQLException{
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try(Connection conn = DriverManager.getConnection(url);
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, balance.toString());
            ps.setLong(2, accountId);
            ps.executeUpdate();
        }
    }
}
