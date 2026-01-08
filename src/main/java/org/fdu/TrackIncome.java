package org.fdu;
import java.util.LinkedList;

public class TrackIncome {
    LinkedList<Income>IncomeRecurring;
    LinkedList<Income>IncomeIncidental;

    public TrackIncome(){
        this.IncomeRecurring = new LinkedList<>();
        this.IncomeIncidental = new LinkedList<>();
    }

    public void addIncomeRecurring(String incomeName, float incomeAmount){
        IncomeRecurring.add(new Income(incomeName,incomeAmount));
    }

    public float getIncomeRecurring(String incomeName){
        for (int i = 0; i < IncomeRecurring.size(); i++) {
            if (IncomeRecurring.get(i).incomeName.equals(incomeName)){
                return (float) IncomeRecurring.get(i).incomeAmount;
            }
        }
        return 0F;
    }

    public void addIncomeIncidental(String incomeName, float incomeAmount){
        IncomeIncidental.add(new Income(incomeName, incomeAmount));
    }

    public void printIncomeRecurring() {
        System.out.println("Recurring Income: ");
        for(Income income: IncomeRecurring){
            System.out.printf("%s: $%.2f\n\n", income.incomeName, income.incomeAmount);
        }
    }
    public void printIncomeIncidental(){
        System.out.println("Incidental Income: ");
        for(Income income: IncomeIncidental) {
            System.out.printf("%s: $%.2f\n\n", income.incomeName, income.incomeAmount);
        }

    }

    public void printIncomeAll(){
        printIncomeRecurring();
        System.out.println("\n");
        printIncomeIncidental();
    }

}