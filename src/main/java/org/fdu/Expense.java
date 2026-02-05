package org.fdu;
import java.time.LocalDateTime;

class Expense{
    String expenseName;
    float expenseAmount;
    LocalDateTime timestamp;

    public Expense(String expenseName, float expenseAmount){
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.timestamp = LocalDateTime.now();
    }
}
