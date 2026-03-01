package org.fdu;

import java.util.Scanner;

public class UI {
    private Scanner      scanner;
    private TrackIncome  incomeObj;
    private TrackExpense expenseObj;

    public UI() {
        this.scanner    = new Scanner(System.in);
        this.incomeObj  = new TrackIncome();
        this.expenseObj = new TrackExpense();
    }

    public void run_program() {
        boolean keepRunning = true;
        System.out.println("Welcome To Green Light or Red Light");
        displayMainMenu();
        String input = scanner.nextLine().trim().toLowerCase();

        while (keepRunning) {
            switch (input) {
                case "add":
                    handleAdd();
                    System.out.println("Enter 'yes' to add another entry, 'no' for main menu:");
                    if (scanner.nextLine().trim().equalsIgnoreCase("no")) {
                        displayWeeklySummary();
                        displayMainMenu();
                        input = scanner.nextLine().trim().toLowerCase();
                    }
                    break;

                case "print":
                    printCommand();
                    displayMainMenu();
                    input = scanner.nextLine().trim().toLowerCase();
                    break;

                case "quit":
                    keepRunning = false;
                    System.out.println("Thank you! Goodbye!");
                    break;

                default:
                    System.out.println("Please enter a valid option.\n");
                    displayMainMenu();
                    input = scanner.nextLine().trim().toLowerCase();
            }
        }
        scanner.close();
    }

    // ── Add flow ──────────────────────────────────────────────────────────────

    private void handleAdd() {
        // 1. Income or Expense?
        System.out.println("Type 'income' or 'expense':");
        String type = scanner.nextLine().trim().toLowerCase();
        if (!type.equals("income") && !type.equals("expense")) {
            System.out.println("Error: must be 'income' or 'expense'."); return;
        }

        // 2. Recurring or Incidental?
        System.out.println("Type 'recurring' or 'incidental':");
        String occurrence = scanner.nextLine().trim().toLowerCase();
        if (!occurrence.equals("recurring") && !occurrence.equals("incidental")) {
            System.out.println("Error: must be 'recurring' or 'incidental'."); return;
        }

        // 3. Name
        System.out.println("Enter a name (alphanumeric, no spaces):");
        String name = scanner.nextLine().trim();
        if (!name.matches("[a-zA-Z0-9 ]+")) {
            System.out.println("Error: name must be alphanumeric."); return;
        }

        // 4. For income: flat amount or hourly?
        if (type.equals("income")) {
            System.out.println("Enter 'hourly' to calculate from hours + rate, or 'flat' for a fixed amount:");
            String mode = scanner.nextLine().trim().toLowerCase();

            if (mode.equals("hourly")) {
                addHourlyIncome(name, occurrence);
            } else {
                addFlatIncome(name, occurrence);
            }
        } else {
            addExpense(name, occurrence);
        }
    }

    private void addHourlyIncome(String name, String occurrence) {
        // Pay frequency
        System.out.println("Select pay frequency:");
        System.out.println("  1 - Weekly");
        System.out.println("  2 - Bi-Weekly");
        System.out.println("  3 - Semi-Monthly (twice a month)");
        System.out.println("  4 - Monthly");
        int freqChoice;
        try {
            freqChoice = Integer.parseInt(scanner.nextLine().trim());
            if (freqChoice < 1 || freqChoice > 4) { System.out.println("Error: choose 1–4."); return; }
        } catch (NumberFormatException e) { System.out.println("Error: enter a number."); return; }

        PayFrequency freq = PayFrequency.values()[freqChoice - 1];

        // Hourly rate
        System.out.printf("Enter hourly rate ($):  ");
        double rate;
        try {
            rate = Double.parseDouble(scanner.nextLine().trim());
            if (rate <= 0) { System.out.println("Error: rate must be positive."); return; }
        } catch (NumberFormatException e) { System.out.println("Error: enter a valid number."); return; }

        // Hours per pay period
        System.out.printf("Enter hours worked per %s pay period:  ", freq);
        double hours;
        try {
            hours = Double.parseDouble(scanner.nextLine().trim());
            if (hours <= 0) { System.out.println("Error: hours must be positive."); return; }
        } catch (NumberFormatException e) { System.out.println("Error: enter a valid number."); return; }

        // Calculate and confirm
        double avgWeeklyHrs = freq.weeklyHours(hours);
        double otHrs        = Math.max(avgWeeklyHrs - 40.0, 0);
        double weeklyGross  = (Math.min(avgWeeklyHrs, 40.0) * rate) + (otHrs * rate * 1.5);
        double weeklyNet    = TaxCalculator.weeklyTakeHome(weeklyGross);

        System.out.println("\n── Calculated ─────────────────────────────");
        System.out.printf("  Avg weekly hrs : %.1f  (%.1f regular + %.1f OT)%n",
                avgWeeklyHrs, Math.min(avgWeeklyHrs, 40.0), otHrs);
        System.out.printf("  Weekly gross   : $%.2f%n", weeklyGross);
        System.out.printf("  Weekly net     : $%.2f (after NJ+Federal+FICA)%n", weeklyNet);
        System.out.printf("  Annual gross   : $%.2f%n", weeklyGross * 52);
        System.out.printf("  Annual net     : $%.2f%n", weeklyNet * 52);
        System.out.println("───────────────────────────────────────────\n");

        System.out.println("Weekly 401K / pre-tax deduction ($)? (press Enter to skip):");
        double weeklyK401 = 0;
        String k401Input = scanner.nextLine().trim();
        if (!k401Input.isEmpty()) {
            try { weeklyK401 = Double.parseDouble(k401Input); }
            catch (NumberFormatException e) { System.out.println("Invalid amount, assuming $0."); }
        }

        if (occurrence.equals("recurring")) {
            incomeObj.addIncomeRecurringHourly(name, rate, hours, freq, weeklyK401);
        } else {
            incomeObj.addIncomeIncidentalHourly(name, rate, hours, freq, weeklyK401);
        }
        System.out.printf("Added hourly income '%s' to %s income.%n%n", name, occurrence);
    }

    private void addFlatIncome(String name, String occurrence) {
        System.out.println("Enter amount ($):");
        float amount;
        try {
            amount = Float.parseFloat(scanner.nextLine().trim());
            if (amount < 0) { System.out.println("Error: amount must be positive."); return; }
        } catch (NumberFormatException e) { System.out.println("Error: enter a valid number."); return; }

        System.out.println("Is this a monthly amount? (yes/no):");
        boolean isMonthly = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (occurrence.equals("recurring")) {
            incomeObj.addIncomeRecurring(name, amount, isMonthly);
        } else {
            incomeObj.addIncomeIncidental(name, amount, isMonthly);
        }

        float weekly = isMonthly ? (amount * 12f / 52f) : amount;
        System.out.printf("Added '%s' $%.2f/%s → $%.2f/wk to %s income.%n%n",
                name, amount, isMonthly ? "month" : "week", weekly, occurrence);
    }

    private void addExpense(String name, String occurrence) {
        System.out.println("Enter amount ($):");
        float amount;
        try {
            amount = Float.parseFloat(scanner.nextLine().trim());
            if (amount < 0) { System.out.println("Error: amount must be positive."); return; }
        } catch (NumberFormatException e) { System.out.println("Error: enter a valid number."); return; }

        System.out.println("Is this a monthly amount? (yes/no):");
        boolean isMonthly = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (occurrence.equals("recurring")) {
            expenseObj.addExpenseRecurring(name, amount, isMonthly);
        } else {
            expenseObj.addExpenseIncidental(name, amount, isMonthly);
        }

        float weekly = isMonthly ? (amount * 12f / 52f) : amount;
        System.out.printf("Added '%s' $%.2f/%s → $%.2f/wk to %s expenses.%n%n",
                name, amount, isMonthly ? "month" : "week", weekly, occurrence);
    }

    // ── Print command ─────────────────────────────────────────────────────────

    private void printCommand() {
        System.out.println("1 - Recurring Income");
        System.out.println("2 - Incidental Income");
        System.out.println("3 - Recurring Expenses");
        System.out.println("4 - Incidental Expenses");
        System.out.println("5 - All (with tax breakdown)");
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            switch (n) {
                case 1: incomeObj.printIncomeRecurring();    break;
                case 2: incomeObj.printIncomeIncidental();   break;
                case 3: expenseObj.printExpenseRecurring();  break;
                case 4: expenseObj.printExpenseIncidental(); break;
                case 5:
                    incomeObj.printIncomeAll();
                    expenseObj.printExpenseAll();
                    displayWeeklySummary();
                    break;
                default: System.out.println("Error: choose 1–5.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: enter a number 1–5.");
        }
    }

    // ── Summary ───────────────────────────────────────────────────────────────

    private void displayWeeklySummary() {
        float   weeklyGross   = incomeObj.totalWeeklyGross();
        double  weeklyNet     = incomeObj.totalWeeklyTakeHome();
        float   weeklyExpense = expenseObj.totalWeeklyExpense();
        double  netTakeHome   = weeklyNet - weeklyExpense;

        System.out.println("\n════ Weekly Summary ════════════════════════");
        System.out.printf("  Income (Gross):       $%.2f/wk%n", weeklyGross);
        System.out.printf("  Income (Take-Home):   $%.2f/wk%n", weeklyNet);
        System.out.printf("  Expenses:             $%.2f/wk%n", weeklyExpense);
        System.out.println("  ──────────────────────────────────────────");
        System.out.printf("  Net Take-Home:        $%.2f/wk%n", netTakeHome);
        System.out.println(netTakeHome >= 0 ? "  ✓  GREEN LIGHT" : "  ✗  RED LIGHT");
        System.out.println("════════════════════════════════════════════\n");
    }

    private void displayMainMenu() {
        System.out.println("\nOptions:");
        System.out.println("  'add'   — Add an income or expense");
        System.out.println("  'print' — Display all entries");
        System.out.println("  'quit'  — Exit");
        System.out.print("Enter command: ");
    }
}