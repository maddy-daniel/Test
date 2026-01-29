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
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;

public class GraphicalUI extends Application {

    //Storing data from user input from graphical UI
    private TrackIncome trackIncome = new TrackIncome();
    private TrackExpense trackExpense = new TrackExpense();

    private double totalIncome = 0.0;
    private double totalExpense = 0.0;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Green Light Red Light");

        //Adding a Background Layer
        StackPane bkg = new StackPane();
        bkg.setStyle("-fx-background-image: url('/background.jpg'); " +
                "-fx-background-size: cover; " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-position: center;");

        //Main Container to have form and results added into this container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(25));

        //Creating a grid for the form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        grid.setMaxWidth(450);
        grid.setMinHeight(350);
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

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 7);

        //Balance Indicator Section
        VBox balanceSection = new VBox(10);
        balanceSection.setAlignment(Pos.CENTER);
        balanceSection.setPadding((new Insets(15)));
        balanceSection.setStyle("-fx-background-color: RGBA(255, 255, 255, 0.9); " + "-fx-background-radius: 10;");
        balanceSection.setMaxWidth(550);

        //Labels for totals
        Label balanceLabel = new Label("Financial Balance");
        balanceLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        HBox totalsBox = new HBox(30);
        totalsBox.setAlignment(Pos.CENTER);

        Label incomeTotal = new Label("Income: $0.00");
        incomeTotal.setTextFill(Color.GREEN);
        incomeTotal.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));

        Label expenseTotal = new Label("Expenses: $0.00");
        expenseTotal.setTextFill(Color.RED);
        expenseTotal.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));

        totalsBox.getChildren().addAll(expenseTotal, incomeTotal);

        //Custom Balance Bar (Red-Green gradient with Indicator)
        VBox balanceBarContainer = new VBox(-25);
        balanceBarContainer.setAlignment(Pos.CENTER);
        balanceBarContainer.setPrefWidth(500);
        balanceBarContainer.setPrefHeight(30);

        //Container for Colored Rectangles
        HBox colorBar = new HBox();
        colorBar.setPrefWidth(500);
        colorBar.setPrefHeight(30);

        //Red Rectangle (Left Half)
        Rectangle redRect = new Rectangle(260, 40, Color.RED);
        redRect.setArcWidth(25);
        redRect.setArcHeight(25);

        //Green Rectangle (Right Half);
        Rectangle greenRect = new Rectangle(260, 40, Color.LIME);
        greenRect.setArcWidth(25);
        greenRect.setArcHeight(25);

        colorBar.getChildren().addAll(redRect, greenRect);
        colorBar.setStyle("-fx-border-color: black;" +
                "-fx-border-width: 5;" +
                "-fx-border-height: 20;");

        //The indicator
        StackPane indicatorContainer = new StackPane();
        indicatorContainer.setPrefWidth(500);
        indicatorContainer.setPrefHeight(20);

        //Creating a Triangle as the shape of the indicator
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                0.0, 0.0,   //Top point
                -25.0, 35.0,     //Bottom Left
                25.0, 35.0       //Bottom Right
        );

        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.WHITE);
        triangle.setStrokeWidth(2);

        indicatorContainer.getChildren().add(triangle);

        balanceBarContainer.getChildren().addAll(colorBar, indicatorContainer);

        //Label to show the bar status
        Label balanceStatus = new Label("Balanced");
        balanceStatus.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        //Labe to show the net balance
        Label netBalance = new Label("Net: $0.00");
        netBalance.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        balanceSection.getChildren().addAll(balanceLabel, totalsBox, balanceBarContainer, balanceStatus, netBalance);

        //Separate container for income and expense displays
        HBox resultsContainer = new HBox(20);
        resultsContainer.setAlignment(Pos.TOP_CENTER);
        resultsContainer.setPadding(new Insets(10));
        resultsContainer.setStyle("-fx-background-color: RGBA(255,255,255,0.9); " + "-fx-background-radius: 10;");
        resultsContainer.setMaxWidth(450);

        // VBox to hold all income entries
        VBox incomeListDisplay = new VBox(5);
        incomeListDisplay.setPadding(new Insets(10, 5, 0, 0));

        //ScrollPane with fixed size for income
        ScrollPane incomeScrollPane = new ScrollPane(incomeListDisplay);
        incomeScrollPane. setPrefHeight(200);
        incomeScrollPane.setPrefWidth(200);
        incomeScrollPane.setFitToWidth(true);
        incomeScrollPane.setStyle("-fx-background:white; -fx-border-color:#cccccc;");

        //Header for income
        VBox incomeSection = new VBox(5);
        Label incomeHeader = new Label("Income");
        incomeHeader.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        incomeSection.getChildren().addAll(incomeHeader,incomeScrollPane);

        VBox expenseListDisplay = new VBox(5);
        expenseListDisplay.setPadding(new Insets(10, 5, 0, 0));

        //ScrollPane with fixed size for expense
        ScrollPane expenseScrollPane = new ScrollPane(expenseListDisplay);
        expenseScrollPane.setPrefHeight(200);
        expenseScrollPane.setPrefWidth(200);
        expenseScrollPane.setFitToWidth(true);
        expenseScrollPane.setStyle("-fx-background: white; -fx-broder-color: #cccccc;");

       //Header for expenses
        VBox expenseSection = new VBox(5);
        Label expenseHeader = new Label("Expenses");
        expenseHeader.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        expenseSection.getChildren().addAll(expenseHeader,expenseScrollPane);

        resultsContainer.getChildren().addAll(incomeSection, expenseSection);

        //Creates action when button is clicked on or Enter is pressed
        EventHandler<ActionEvent> handleEntry = new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userInputName = userTypeName.getText();
                String userInputAmount = userTypeAmount.getText();

                if (userInputName.isEmpty() || userInputAmount.isEmpty()) {
                    actionTarget.setFill(Color.RED);
                    actionTarget.setText("Please enter a name and amount!");
                } else {
                    try {
                        float amount = Float.parseFloat(userInputAmount);
                        String type = incomeRadio.isSelected() ? "Income" : "Expenses";
                        String frequency = recurringCheckBox.isSelected() ? "Recurring" : "Incidental";

                        // Add to correct LinkedList using classes
                        if (incomeRadio.isSelected() && recurringCheckBox.isSelected()) {
                            trackIncome.addIncomeRecurring(userInputName, amount);
                            Label incomeEntry = new Label(userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            incomeListDisplay.getChildren().add(incomeEntry);
                        } else if (incomeRadio.isSelected() && !recurringCheckBox.isSelected()) {
                            trackIncome.addIncomeIncidental(userInputName, amount);
                            Label incomeEntry = new Label(userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            incomeListDisplay.getChildren().add(incomeEntry);
                        } else if (expenseRadio.isSelected() && recurringCheckBox.isSelected()) {
                            trackExpense.addExpenseRecurring(userInputName, amount);
                            Label expenseEntry = new Label(userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            expenseListDisplay.getChildren().add(expenseEntry);

                        } else {
                            trackExpense.addExpenseIncidental(userInputName, amount);
                            Label expenseEntry = new Label(userInputName + ": $" + String.format("%.2f", amount) + " (" + frequency + ")");
                            expenseListDisplay.getChildren().add(expenseEntry);

                        }

                        //Update totals
                        totalIncome = trackIncome.totalIncome();
                        totalExpense = trackExpense.totalExpense();

                        //Update Labels
                        incomeTotal.setText(String.format("Income: $%.2f", totalIncome));
                        expenseTotal.setText(String.format("Expense: $%.2f", totalExpense));

                        double netAmount = totalIncome-totalExpense;
                        netBalance.setText(String.format("Net: $%.2f", netAmount));

                        if(netAmount>=0){
                            netBalance.setTextFill(Color.GREEN);
                        }
                        else{
                            netBalance.setTextFill(Color.RED);
                        }

                        //Update Progress Bar sliding indicator
                        if(totalIncome+totalExpense>0){
                            //Calculate ratio: 0 = all expenses, 0.5 = balanced, 1 = all income
                            double ratio = totalIncome/(totalIncome + totalExpense);

                            //Convert ratio to pixel position: -240 (Left) to +240 (Right), 0 = Center
                            double indicatorPos = (ratio - 0.5) *490;
                            triangle.setTranslateX(indicatorPos);

                            //Change bar color based on balance
                            if(ratio<0.5){
                                balanceStatus.setText("Red Light!!");
                                balanceStatus.setTextFill(Color.RED)  ;
                            }
                            else if(ratio>0.5){
                                balanceStatus.setText("Green Light!!");
                                balanceStatus.setTextFill(Color.GREEN);
                            }
                            else {
                                balanceStatus.setText("Caution");
                                balanceStatus.setTextFill(Color.ORANGE);
                            }
                        }
                        else{
                            //No entries have been entered
                            triangle.setTranslateX(0);
                            balanceStatus.setText("Balanced");
                            balanceStatus.setTextFill(Color.GRAY);
                        }

                        // Clear inputs
                        userTypeName.clear();
                        userTypeAmount.clear();
                        recurringCheckBox.setSelected(false);
                        actionTarget.setText("");

                    } catch (NumberFormatException e) {
                        actionTarget.setFill(Color.RED);
                        actionTarget.setText("Please enter a valid number for amount!");
                    }
                }
            }
        };

        // Assign handler to button and text fields
        btn.setOnAction(handleEntry);
        userTypeName.setOnAction(handleEntry);
        userTypeAmount.setOnAction(handleEntry);

        //Add form, Progress bar sliding indicator, and results to main container
        mainContainer.getChildren().addAll(grid, balanceSection, resultsContainer);

        //Attaching main container to background
        bkg.getChildren().add(mainContainer);
        Scene scene = new Scene(bkg, 600, 750);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
