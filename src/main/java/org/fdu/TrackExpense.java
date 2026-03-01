package org.fdu;

import java.util.LinkedList;

public class TrackExpense {
    LinkedList<Expense> expenseRecurring;
    LinkedList<Expense> expenseIncidental;

    public TrackExpense() {
        this.expenseRecurring   = new LinkedList<>();
        this.expenseIncidental  = new LinkedList<>();
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    public Expense addExpenseRecurring(String name, float amount, boolean isMonthly) {
        Expense newExpense = new Expense(name, amount, isMonthly);
        expenseRecurring.add(newExpense);
        return newExpense;
    }

    public Expense addExpenseIncidental(String name, float amount, boolean isMonthly) {
        Expense newExpense = new Expense(name, amount, isMonthly);
        expenseIncidental.add(newExpense);
        return newExpense;
    }

    // ── Delete (by object reference — timestamp makes each unique) ────────────

    public void deleteExpenseRecurring(Expense expense) {
        expenseRecurring.remove(expense);
    }

    public void deleteExpenseIncidental(Expense expense) {
        expenseIncidental.remove(expense);
    }

    // ── Totals (weekly) ───────────────────────────────────────────────────────

    /** Total weekly expenses across all entries. */
    public float totalWeeklyExpense() {
        float total = 0f;
        for (Expense e : expenseRecurring)   total += e.weeklyAmount();
        for (Expense e : expenseIncidental)  total += e.weeklyAmount();
        return total;
    }

    /** Legacy method — keeps existing code working. */
    public float totalExpense() {
        return totalWeeklyExpense();
    }

    // ── Print ─────────────────────────────────────────────────────────────────

    public void printExpenseRecurring() {
        System.out.println("── Recurring Expenses ────────────────");
        if (expenseRecurring.isEmpty()) { System.out.println("  (none)"); return; }
        for (Expense e : expenseRecurring) {
            System.out.printf("  %s%n  → Weekly: $%.2f%n%n", e, e.weeklyAmount());
        }
    }

    public void printExpenseIncidental() {
        System.out.println("── Incidental Expenses ───────────────");
        if (expenseIncidental.isEmpty()) { System.out.println("  (none)"); return; }
        for (Expense e : expenseIncidental) {
            System.out.printf("  %s%n  → Weekly: $%.2f%n%n", e, e.weeklyAmount());
        }
    }

    public void printExpenseAll() {
        printExpenseRecurring();
        System.out.println();
        printExpenseIncidental();
        System.out.println();
        System.out.printf("  Total Weekly Expenses: $%.2f%n%n", totalWeeklyExpense());
    }
}