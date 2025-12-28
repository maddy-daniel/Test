package org.fdu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String type = "";
        String occurrence = "";
        String name = "";
        String amountStr = "";
        float amount = 0.00F;
        int numInput = 0;

        Scanner scanner = new Scanner(System.in);
        TrackIncome incomeObj = new TrackIncome();
        TrackExpense expenseObj = new TrackExpense();

        boolean keepRunning = true;

        while(keepRunning) {
            name = "";

            System.out.println("\nOptions:");
            System.out.println("1. Add entry: Income/Expense Recurring/Incidental Name Amount");
            System.out.println("2. Type 'print' to display all entries");
            System.out.println("3. Type 'quit' to exit");
            System.out.print("Enter command: ");

            String input = scanner.nextLine().trim();

            if(input.equals("print")) {
                System.out.println("Enter 1 for All Income Recurring");
                System.out.println("Enter 2 for All Income Incidental");
                System.out.println("Enter 3 for All Expense Recurring");
                System.out.println("Enter 4 for All Expense Incidental");
                System.out.println("Enter 5 for All Incomes and Expenses");
                String printInput = scanner.nextLine();

                try {
                    numInput = Integer.parseInt(printInput);
                    if (numInput < 0) {
                        System.out.println("Error: Number MUST be positive. Please re-enter Number.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Number MUST be a number value. Please re-enter Number.");
                    continue;
                }
                switch(numInput)
                {
                    case 1:
                        incomeObj.printIncomeRecurring();
                        break;
                    case 2:
                        incomeObj.printIncomeIncidental();
                        break;
                    case 3:
                        expenseObj.printExpenseRecurring();
                        break;
                    case 4:
                        expenseObj.printExpenseIncidental();
                        break;
                    case 5:
                        incomeObj.printIncomeAll();
                        expenseObj.printExpenseAll();
                        break;
                }
                continue;
            }

            if(input.equals("quit")) {
                keepRunning = false;
                System.out.println("Exiting program. Goodbye!");
                continue;
            }

            String[] commands = input.split(" ");
            if (commands.length < 4) {
                System.out.println("Error: Not enough arguments. Please try again.\n");
                continue;
            }

            type = commands[0];
            if (!type.equals("Income") && !type.equals("Expense")) {
                System.out.println("Error: Please Enter Income or Expense");
                continue;
            }

            occurrence = commands[1];
            if (!occurrence.equals("Recurring") && !occurrence.equals("Incidental")) {
                System.out.println("Error: Please Enter Recurring or Incidental");
                continue;
            }

            boolean validName = true;
            for (int i = 2; i < commands.length - 1; i++) {
                if (!commands[i].matches("[a-zA-Z0-9]+")) {
                    System.out.println("Error: Name must be alphanumeric only");
                    validName = false;
                    break;
                }
                name += commands[i];
                if (i < commands.length - 2) {
                    name += " ";
                }
            }
            if (!validName) {
                continue;
            }

            amountStr = commands[commands.length - 1];
            try {
                amount = Float.parseFloat(amountStr);
                if (amount < 0) {
                    System.out.println("Error: Amount MUST be positive. Please re-enter amount.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Amount MUST be a number value. Please re-enter amount.");
                continue;
            }

            System.out.println("Valid Commands Entered!");
            System.out.println("Type: " + type);
            System.out.println("Occurrence: " + occurrence);
            System.out.println("Name: " + name);
            System.out.printf("Amount: $%.2f\n", amount);

            if (type.equals("Income") && occurrence.equals("Recurring")) {
                incomeObj.addIncomeRecurring(name, amount);
                System.out.println("✓ Added to Recurring Income");
            }
            else if (type.equals("Income") && occurrence.equals("Incidental")) {
                incomeObj.addIncomeIncidental(name, amount);
                System.out.println("✓ Added to Incidental Income");
            }
            else if (type.equals("Expense") && occurrence.equals("Recurring")) {
                expenseObj.addExpenseRecurring(name, amount);
                System.out.println("✓ Added to Recurring Expense");
            }
            else if (type.equals("Expense") && occurrence.equals("Incidental")) {
                expenseObj.addExpenseIncidental(name, amount);
                System.out.println("✓ Added to Incidental Expense");
            }
        }
        scanner.close();
    }
}