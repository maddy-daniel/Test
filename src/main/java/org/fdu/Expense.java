package org.fdu;

import java.time.LocalDateTime;

public class Expense {
    String expenseName;
    float expenseAmount;  // the amount as entered (weekly OR monthly)
    boolean isMonthly;    // true = user entered a monthly figure
    LocalDateTime timestamp;

    public Expense(String expenseName, float expenseAmount, boolean isMonthly) {
        this.expenseName   = expenseName;
        this.expenseAmount = expenseAmount;
        this.isMonthly     = isMonthly;
        this.timestamp     = LocalDateTime.now();
    }

    /**
     * Returns the weekly equivalent of this expense.
     * Monthly → weekly: (amount × 12) / 52
     */
    public float weeklyAmount() {
        return isMonthly ? (expenseAmount * 12f) / 52f : expenseAmount;
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f/%s  [added %s]",
                expenseName, expenseAmount,
                isMonthly ? "month" : "week",
                timestamp.toLocalTime().toString().substring(0, 8));
    }
}