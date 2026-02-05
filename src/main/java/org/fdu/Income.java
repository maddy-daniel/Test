package org.fdu;
import java.time.LocalDateTime;

class Income{
    String incomeName;
    float incomeAmount;
    LocalDateTime timestamp;

    public Income(String incomeName, float incomeAmount){
        this.incomeName = incomeName;
        this.incomeAmount = incomeAmount;
        this.timestamp = LocalDateTime.now();
    }
}