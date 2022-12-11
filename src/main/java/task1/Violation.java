package task1;

import java.math.BigDecimal;

public class Violation {

    private String type;
    private BigDecimal amount;

    public Violation() {
    }

    public Violation(String type, BigDecimal amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

