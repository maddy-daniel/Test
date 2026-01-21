package org.fdu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class GraphicalUI extends Application {

    //Storing data from user input from graphical UI
    private TrackIncome trackIncome = new TrackIncome();
    private TrackExpense trackExpense = new TrackExpense();

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Green Light Red Light");

        //Adding a Background Layer
        StackPane bkg = new StackPane();
        bkg.setStyle("-fx-background-image: url('/background.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-position: center;");

        //Creating a grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        grid.setMaxWidth(450);
        grid.setMaxHeight(350);
        //Set a layer of white over background
        grid.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 20;");

        //Add text for user to see
        Text sceneTitle = new Text("Green Light Red Light");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0,0,2,1);

        // Income vs Expenses - RadioButtons
        Label typeLabel = new Label("Type: ");
        grid.add(typeLabel, 0, 1);

        RadioButton incomeRadio = new RadioButton("Income");
        RadioButton expenseRadio = new RadioButton("Expenses");
        ToggleGroup typeGroup = new ToggleGroup();
        incomeRadio.setToggleGroup(typeGroup);
        expenseRadio.setToggleGroup(typeGroup);
        incomeRadio.setSelected(true); // Default to Income

        HBox typeBox = new HBox(10);
        typeBox.getChildren().addAll(incomeRadio, expenseRadio);
        grid.add(typeBox, 1, 1);

        // Recurring checkbox
        CheckBox recurringCheckBox = new CheckBox("Recurring");
        grid.add(recurringCheckBox, 0, 2, 2, 1);

        //Creating labels and text field for user to enter name
        Label Name = new Label("Name: ");
        grid.add(Name, 0, 3);

        TextField userTypeName = new TextField();
        grid.add(userTypeName, 1, 3);

        Label Amount = new Label("Amount: ");
        grid.add(Amount, 0, 4);

        TextField userTypeAmount = new TextField();
        grid.add(userTypeAmount, 1, 4);

        //Creating a button
        Button btn = new Button("Enter");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 7);

        // VBox to hold all income entries
        VBox incomeListDisplay = new VBox(5);
        incomeListDisplay.setPadding(new Insets(10, 5, 0, 0));
        grid.add(incomeListDisplay,0, 8);

        VBox expenseListDisplay = new VBox(5);
        expenseListDisplay.setPadding(new Insets(10, 5, 0, 0));
        grid.add(expenseListDisplay,1, 8);

        //Creates action when button is clicked on or Enter is pressed
        EventHandler<ActionEvent> handleEntry = new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userInputName = userTypeName.getText();
                String userInputAmount = userTypeAmount.getText();

                if (userInputName.isEmpty() || userInputAmount.isEmpty()) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please enter a name and amount!");
                } else {
                    try {
                        float amount = Float.parseFloat(userInputAmount);
                        String type = incomeRadio.isSelected() ? "Income" : "Expenses";
                        String frequency = recurringCheckBox.isSelected() ? "Recurring" : "Incidental";

                        // Add to correct LinkedList using classes
                        if (incomeRadio.isSelected() && recurringCheckBox.isSelected()) {
                            trackIncome.addIncomeRecurring(userInputName, amount);
                            Label incomeEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            incomeListDisplay.getChildren().add(incomeEntry);
                        } else if (incomeRadio.isSelected() && !recurringCheckBox.isSelected()) {
                            trackIncome.addIncomeIncidental(userInputName, amount);
                            Label incomeEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            incomeListDisplay.getChildren().add(incomeEntry);
                        } else if (expenseRadio.isSelected() && recurringCheckBox.isSelected()) {
                            trackExpense.addExpenseRecurring(userInputName, amount);
                            Label expenseEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            expenseListDisplay.getChildren().add(expenseEntry);

                        } else {
                            trackExpense.addExpenseIncidental(userInputName, amount);
                            Label expenseEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            expenseListDisplay.getChildren().add(expenseEntry);

                        }

                        // Display in UI
                        //Label incomeEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                        //incomeListDisplay.getChildren().add(incomeEntry);
                        //Label expenseEntry = new Label(type + " - " + userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                        //expenseListDisplay.getChildren().add(expenseEntry);

                        // Clear inputs
                        userTypeName.clear();
                        userTypeAmount.clear();
                        recurringCheckBox.setSelected(false);
                        actiontarget.setText("");

                    } catch (NumberFormatException e) {
                        actiontarget.setFill(Color.RED);
                        actiontarget.setText("Please enter a valid number for amount!");
                    }
                }
            }
        };

        // Assign handler to button and text fields
        btn.setOnAction(handleEntry);
        userTypeName.setOnAction(handleEntry);
        userTypeAmount.setOnAction(handleEntry);

        //Attaching background to grid
        bkg.getChildren().add(grid);
        Scene scene = new Scene(bkg, 600, 700);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
