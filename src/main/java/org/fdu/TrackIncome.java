package org.fdu;
import java.util.LinkedList;

public class TrackIncome {
    LinkedList<Income>IncomeRecurring;
    LinkedList<Income>IncomeIncidental;
    float totalIncomeIncidental;
    float totalIncomeRecurring;
    float totalIncome;

    public TrackIncome(){
        this.IncomeRecurring = new LinkedList<>();
        this.IncomeIncidental = new LinkedList<>();
    }

    public Income addIncomeRecurring(String incomeName, float incomeAmount){
        Income newIncome = new Income(incomeName, incomeAmount);
        IncomeRecurring.add(newIncome);
        return newIncome;
    }
    public Income addIncomeIncidental(String incomeName, float incomeAmount){
        Income newIncome = new Income(incomeName, incomeAmount);
        IncomeIncidental.add(newIncome);
        return newIncome;
    }

    public float getIncomeRecurring(String incomeName){
        for (int i = 0; i < IncomeRecurring.size(); i++) {
            if (IncomeRecurring.get(i).incomeName.equals(incomeName)){
                return (float) IncomeRecurring.get(i).incomeAmount;
            }
        }
        return 0F;
    }

    public void deleteIncomeRecurring(Income income) {
        IncomeRecurring.remove(income);
    }

    public void deleteIncomeIncidental(Income income) {
        IncomeIncidental.remove(income);
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

    public float totalIncome(){
        totalIncomeIncidental = 0F;
        totalIncomeRecurring = 0F;
        totalIncome = 0F;

        for(Income income: IncomeIncidental){
            totalIncomeIncidental = totalIncomeIncidental + income.incomeAmount;
        }
        for(Income income: IncomeRecurring){
            totalIncomeRecurring = totalIncomeRecurring + income.incomeAmount;
        }
        totalIncome = totalIncomeIncidental + totalIncomeRecurring;
        return totalIncome;
    }

}