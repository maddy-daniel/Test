package org.fdu;
import java.util.LinkedList;

public class TrackExpense {
    LinkedList<Expense>ExpenseRecurring;
    LinkedList<Expense>ExpenseIncidental;
    float totalExpenseIncidental = 0F;
    float totalExpenseRecurring = 0F;
    float totalIncome = 0F;

    public TrackExpense(){
        this.ExpenseRecurring = new LinkedList<>();
        this.ExpenseIncidental = new LinkedList<>();
    }

    public void addExpenseRecurring(String expenseName, float expenseAmount){
        ExpenseRecurring.add(new Expense(expenseName,expenseAmount));
    }

    public void addExpenseIncidental(String expenseName, float expenseAmount){
        ExpenseIncidental.add(new Expense(expenseName, expenseAmount));
    }

    public void printExpenseRecurring(){
        System.out.println("Recurring Expenses");
        for(Expense expense: ExpenseRecurring){
            System.out.printf("%s: $%.2f\n", expense.expenseName, expense.expenseAmount);
        }
    }

    public void printExpenseIncidental() {
        System.out.println("Incidental Expenses");
        for(Expense expense: ExpenseIncidental){
            System.out.printf("%s: $%.2f\n", expense.expenseName, expense.expenseAmount);
        }
    }

    public void printExpenseAll(){
        printExpenseRecurring();
        System.out.println("\n");
        printExpenseIncidental();
    }

    public float totalExpense(){
        for(Expense expense: ExpenseIncidental){
            totalExpenseIncidental = totalExpenseIncidental + expense.expenseAmount;
        }
        for(Expense expense: ExpenseRecurring){
            totalExpenseRecurring = totalExpenseRecurring + expense.expenseAmount;
        }
        totalIncome = totalExpenseIncidental + totalExpenseRecurring;
        return totalIncome;
    }
}
