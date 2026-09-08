package dev.aperry.tradingapi;
import java.math.BigDecimal;

public class Position {
    private final String ticker;
    private int shares;
    private BigDecimal avgCost;

    Position(String ticker, int shares, BigDecimal avgCost){
        this.ticker = ticker;
        this.shares = shares;
        this.avgCost = avgCost;
    }
    public String getTicker(){
        return ticker;
    }
    public int getShares(){
        return shares;
    }
    public BigDecimal getAvgCost(){
        return avgCost;
    }
    public void setShares(int shares) {
        this.shares = shares;
    }
    public void setAvgCost(BigDecimal avgCost) {
        this.avgCost = avgCost;
    }
}