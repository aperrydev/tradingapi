package dev.aperry.tradingapi;
import java.math.BigDecimal;


public class Account {

    private String firstName;
    private String lastName;
    private int age;
    private BigDecimal balance = BigDecimal.ZERO;

    Account() {
    }

    Account(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
    Account(String firstName, String lastName, int age, BigDecimal balance){
        this(firstName, lastName, age);
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal viewBalance(){
        return balance;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deposit must be positive.");
        }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Withdraw amount can not be negative.");

        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Withdraw amount must be above 0.");

        }
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");

        }
        balance = balance.subtract(amount);
    }
}

