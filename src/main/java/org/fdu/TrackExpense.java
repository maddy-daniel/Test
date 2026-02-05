package org.fdu;
import java.util.LinkedList;

public class TrackExpense {
    LinkedList<Expense>ExpenseRecurring;
    LinkedList<Expense>ExpenseIncidental;
    float totalExpenseIncidental;
    float totalExpenseRecurring;
    float totalIncome;

    public TrackExpense(){
        this.ExpenseRecurring = new LinkedList<>();
        this.ExpenseIncidental = new LinkedList<>();
    }
    public Expense addExpenseRecurring(String expenseName, float expenseAmount){
        Expense newExpense = new Expense(expenseName, expenseAmount);
        ExpenseRecurring.add(newExpense);
        return newExpense;
    }

    public Expense addExpenseIncidental(String expenseName, float expenseAmount){
        Expense newExpense = new Expense(expenseName, expenseAmount);
        ExpenseIncidental.add(newExpense);
        return newExpense;
    }

    public void deleteExpenseRecurring(Expense expense) {
        ExpenseRecurring.remove(expense);
    }

    public void deleteExpenseIncidental(Expense expense) {
        ExpenseIncidental.remove(expense);
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

    public float totalExpense() {
        totalExpenseIncidental = 0F;
        totalExpenseRecurring = 0F;
        totalIncome = 0F;

        for (Expense expense : ExpenseIncidental) {
            totalExpenseIncidental = totalExpenseIncidental + expense.expenseAmount;
        }
        for (Expense expense : ExpenseRecurring) {
            totalExpenseRecurring = totalExpenseRecurring + expense.expenseAmount;
        }
        totalIncome = totalExpenseIncidental + totalExpenseRecurring;
        return totalIncome;
    }
}
