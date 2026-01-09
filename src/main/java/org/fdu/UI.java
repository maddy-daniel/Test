package org.fdu;
import java.util.Scanner;

public class UI {
    private Scanner scanner;
    private TrackIncome incomeObj;
    private TrackExpense expenseObj;

    public UI(){
        this.scanner = new Scanner(System.in);
        this.incomeObj = new TrackIncome();
        this.expenseObj = new TrackExpense();
    }

    public void run_program(){
        boolean keepRunning = true;
        System.out.println("Welcome To Green Light or Red Light");
        displayMainMenu();
        String input = scanner.nextLine().trim().toLowerCase();

        while(keepRunning){
            if(input.equals("add")){
                displayMessage("Add entry in the format: Income/Expense Recurring/Incidental Name Amount");
                String inputEntry = scanner.nextLine().trim().toLowerCase();
                addEntryCommand(inputEntry);

                displayMessage("Enter 'yes' if you want to add another entry");
                displayMessage("Enter 'no' to return to main menu");
                String inputContinue = scanner.nextLine().trim().toLowerCase();

                if(inputContinue.equals("no")){
                    displayTotalIncome();
                    displayTotalExpense();
                    System.out.println("Welcome To Green Light or Red Light");
                    displayMainMenu();
                    input = scanner.nextLine().trim().toLowerCase();
                }
                continue;
            }
            else if(input.equals("print")){
                printCommand();
                displayMainMenu();
                input = scanner.nextLine().trim().toLowerCase();
            }
            else if(input.equals("quit")){
                keepRunning = false;
                displayExitMessage();
            }
            else{
                displayMessage("Please enter a valid option\n");
                displayMainMenu();
                input = scanner.nextLine().trim().toLowerCase();
            }
        }
    scanner.close();
    }

    private void displayMainMenu(){
        System.out.println("Options");
        System.out.println("1. Type 'Add' to enter an entry");
        System.out.println("2. Type 'print' to display all entries");
        System.out.println("3. Type 'quit' to exit");
        System.out.print("Enter command: ");
    }
    private void displayEntriesMenu(){
        System.out.println("Enter 1 for All Income Recurring");
        System.out.println("Enter 2 for All Income Incidental");
        System.out.println("Enter 3 for All Expense Recurring");
        System.out.println("Enter 4 for All Expense Incidental");
        System.out.println("Enter 5 for All Incomes and Expenses");
    }
    private void displayExitMessage(){
        System.out.println("Thank you! Goodbye!");
    }
    private void displayVaildEntry(String type, String occurrence , String name, float amount){
        System.out.println("Valid Commands Entered!");
        System.out.println("Type: " + type);
        System.out.println("Occurrence: "+ occurrence);
        System.out.println("Name: "+ name);
        System.out.printf("Amount: $%.2f\n", amount);
    }

    private void displayMessage(String message){
        System.out.println(message);
    }

    private void printCommand(){
        displayEntriesMenu();
        String printInput = scanner.nextLine();

        try {
            int numInput = Integer.parseInt(printInput);
            if (numInput <= 0 || numInput>5) {
                System.out.println("Error: Number MUST be positive and nonzero. Please re-enter Number.");
                return;
            }
            switch(numInput){
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
        }
        catch (NumberFormatException e) {
            displayMessage("Error: Number MUST be a number value. Please re-enter Number.");
        }
    }
    private void addEntryCommand(String input){
        String[] commands = input.split(" ");
        if (commands.length < 4) {
            displayMessage("Error: Not enough arguments. Please try again.\n");
            return;
        }

        String type = commands[0];
        if (!type.equals("income") && !type.equals("expense")) {
            displayMessage("Error: Please Enter Income or Expense");
            return;
        }

       String occurrence = commands[1];
        if (!occurrence.equals("recurring") && !occurrence.equals("incidental")) {
            displayMessage("Error: Please Enter Recurring or Incidental");
            return;
        }

        String name = "";
        boolean validName = true;
        for (int i = 2; i < commands.length - 1; i++) {
            if (!commands[i].matches("[a-zA-Z0-9]+")) {
                displayMessage("Error: Name must be alphanumeric only");
                validName = false;
                break;
            }
            name += commands[i];
            if (i < commands.length - 2) {
                name += " ";
            }
        }
        if (!validName) {
            return;
        }

        String amountStr = commands[commands.length - 1];
        float amount;
        try {
            amount = Float.parseFloat(amountStr);
            if (amount < 0) {
                System.out.println("Error: Amount MUST be positive. Please re-enter amount.");
                return;
            }
        } catch (NumberFormatException e) {
            displayMessage("Error: Amount MUST be a number value. Please re-enter amount.");
            return;
        }
        addEntry(type, occurrence, name, amount);
    }
    private void addEntry(String type, String occurrence, String name, float amount){
        if (type.equals("income") && occurrence.equals("recurring")) {
            incomeObj.addIncomeRecurring(name, amount);
            displayMessage("Added to Recurring Income\n");
        }
        else if (type.equals("income") && occurrence.equals("incidental")) {
            incomeObj.addIncomeIncidental(name, amount);
            displayMessage("Added to Incidental Income\n");
        }
        else if (type.equals("expense") && occurrence.equals("recurring")) {
            expenseObj.addExpenseRecurring(name, amount);
            displayMessage("Added to Recurring Expense\n");
        }
        else if (type.equals("expense") && occurrence.equals("incidental")) {
            expenseObj.addExpenseIncidental(name, amount);
            displayMessage("Added to Incidental Expense\n");
        }
        else{
            displayMessage("Error\n");
        }
    }

    void displayTotalIncome(){
        float totalIncome = incomeObj.totalIncome();
        System.out.printf("Total Income:  $%.2f\n\n", totalIncome);
    }

    void displayTotalExpense(){
        float totalExpense = expenseObj.totalExpense();
        System.out.printf("Total Expenses:  $%.2f\n\n", totalExpense);

    }
}

