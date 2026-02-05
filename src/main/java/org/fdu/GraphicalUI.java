package org.fdu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import java.time.format.DateTimeFormatter;

public class GraphicalUI extends Application {

    private TrackIncome trackIncome = new TrackIncome();
    private TrackExpense trackExpense = new TrackExpense();
    private double totalIncome = 0.0;
    private double totalExpense = 0.0;

    private Label incomeTotal;
    private Label expenseTotal;
    private Label netBalance;
    private Label balanceStatus;
    private Rectangle greenRect;
    private Rectangle redRect;
    private Polygon triangle;
    private VBox incomeListDisplay;
    private VBox expenseListDisplay;

    private static final double BAR_HALF_WIDTH = 260;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Green Light Red Light");

        StackPane bkg = new StackPane();
        bkg.setStyle("-fx-background-image: url('/background.jpg');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: center;");

        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(25));

        GridPane grid = createInputForm();
        VBox balanceSection = createBalanceSection();
        HBox resultsContainer = createResultsSection();

        mainContainer.getChildren().addAll(grid, balanceSection, resultsContainer);
        bkg.getChildren().add(mainContainer);

        Scene scene = new Scene(bkg, 600, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createInputForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));
        grid.setMaxWidth(450);
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Text title = new Text("Green Light Red Light");
        title.setFont(Font.font("Tahoma", 20));
        grid.add(title, 0, 0, 2, 1);

        RadioButton incomeRadio = new RadioButton("Income");
        RadioButton expenseRadio = new RadioButton("Expenses");
        ToggleGroup tg = new ToggleGroup();
        incomeRadio.setToggleGroup(tg);
        expenseRadio.setToggleGroup(tg);
        incomeRadio.setSelected(true);

        grid.add(new Label("Type:"), 0, 1);
        grid.add(new HBox(10, incomeRadio, expenseRadio), 1, 1);

        CheckBox recurring = new CheckBox("Recurring");
        TextField name = new TextField();
        TextField amount = new TextField();
        Button btn = new Button("Enter");
        Text msg = new Text();

        grid.add(recurring, 0, 2, 2, 1);
        grid.add(new Label("Name:"), 0, 3);
        grid.add(name, 1, 3);
        grid.add(new Label("Amount:"), 0, 4);
        grid.add(amount, 1, 4);
        grid.add(btn, 1, 5);
        grid.add(msg, 1, 6);

        EventHandler<ActionEvent> handler =
                e -> handleEntryAction(name, amount, incomeRadio, recurring, msg);

        btn.setOnAction(handler);
        name.setOnAction(handler);
        amount.setOnAction(handler);

        return grid;
    }

    private VBox createBalanceSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10;");

        Label title = new Label("Financial Balance");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        incomeTotal = new Label("Income: $0.00");
        incomeTotal.setTextFill(Color.GREEN);

        expenseTotal = new Label("Expenses: $0.00");
        expenseTotal.setTextFill(Color.RED);

        HBox totals = new HBox(30, expenseTotal, incomeTotal);
        totals.setAlignment(Pos.CENTER);

        redRect = new Rectangle(260, 40, Color.RED);
        greenRect = new Rectangle(260, 40, Color.LIME);

        redRect.setArcWidth(25);
        redRect.setArcHeight(25);
        greenRect.setArcWidth(25);
        greenRect.setArcHeight(25);

        HBox bar = new HBox(redRect, greenRect);
        bar.setStyle("-fx-border-color: black; -fx-border-width: 5;");

        triangle = new Polygon(0, 0, -25, 35, 25, 35);
        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.WHITE);
        triangle.setStrokeWidth(2);

        StackPane indicator = new StackPane(triangle);

        VBox barContainer = new VBox(-25, bar, indicator);
        barContainer.setAlignment(Pos.CENTER);

        balanceStatus = new Label("Balanced");
        netBalance = new Label("Net: $0.00");
        netBalance.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

        box.getChildren().addAll(title, totals, barContainer, balanceStatus, netBalance);
        return box;
    }

    private HBox createResultsSection() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.TOP_CENTER);

        incomeListDisplay = new VBox(5);
        expenseListDisplay = new VBox(5);

        ScrollPane incomeScroll = new ScrollPane(incomeListDisplay);
        ScrollPane expenseScroll = new ScrollPane(expenseListDisplay);

        incomeScroll.setPrefSize(200, 200);
        expenseScroll.setPrefSize(200, 200);

        box.getChildren().addAll(
                new VBox(new Label("Income"), incomeScroll),
                new VBox(new Label("Expenses"), expenseScroll)
        );

        return box;
    }

    private void handleEntryAction(TextField name, TextField amount,
                                   RadioButton incomeRadio, CheckBox recurring,
                                   Text msg) {
        try {
            if (name.getText().isEmpty() || amount.getText().isEmpty()) {
                msg.setFill(Color.RED);
                msg.setText("Enter name and amount");
                return;
            }

            float value = Float.parseFloat(amount.getText());
            boolean income = incomeRadio.isSelected();
            boolean isRecurring = recurring.isSelected();

            Object entryObject = null;  // Will hold Income or Expense object

            if (income) {
                if (isRecurring) {
                    entryObject = trackIncome.addIncomeRecurring(name.getText(), value);
                } else {
                    entryObject = trackIncome.addIncomeIncidental(name.getText(), value);
                }
            } else {
                if (isRecurring) {
                    entryObject = trackExpense.addExpenseRecurring(name.getText(), value);
                } else {
                    entryObject = trackExpense.addExpenseIncidental(name.getText(), value);
                }
            }

            addEntryToDisplay(name.getText(), value, income, isRecurring, entryObject);
            updateDisplay();

            name.clear();
            amount.clear();
            recurring.setSelected(false);
            msg.setText("");

        } catch (NumberFormatException e) {
            msg.setFill(Color.RED);
            msg.setText("Invalid amount");
        }
    }

    private void addEntryToDisplay(String name, float amount, boolean isIncome,
                                   boolean isRecurring, Object entryObject) {
        HBox entryBox = new HBox(10);
        entryBox.setAlignment(Pos.CENTER_LEFT);

        String type = isRecurring ? "Recurring" : "Incidental";

        Label entryLabel = new Label(
                name + ": $" + String.format("%.2f", amount) + " (" + type + ")"
        );

        Button deleteBtn = new Button("-");
        deleteBtn.setPrefSize(20, 20);
        deleteBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: red;" +
                        "-fx-font-weight: bold;"
        );

        deleteBtn.setOnAction(e -> {
            (isIncome ? incomeListDisplay : expenseListDisplay).getChildren().remove(entryBox);

            // Remove the exact object from data structures
            if (isIncome) {
                Income income = (Income) entryObject;
                if (isRecurring) {
                    trackIncome.deleteIncomeRecurring(income);
                } else {
                    trackIncome.deleteIncomeIncidental(income);
                }
            } else {
                Expense expense = (Expense) entryObject;
                if (isRecurring) {
                    trackExpense.deleteExpenseRecurring(expense);
                } else {
                    trackExpense.deleteExpenseIncidental(expense);
                }
            }
            updateDisplay();
        });

        entryBox.getChildren().addAll(deleteBtn, entryLabel);

        (isIncome ? incomeListDisplay : expenseListDisplay).getChildren().add(entryBox);
    }

    private void updateDisplay() {
        totalIncome = trackIncome.totalIncome();
        totalExpense = trackExpense.totalExpense();

        incomeTotal.setText(String.format("Income: $%.2f", totalIncome));
        expenseTotal.setText(String.format("Expenses: $%.2f", totalExpense));

        double net = totalIncome - totalExpense;
        netBalance.setText(String.format("Net: $%.2f", net));
        netBalance.setTextFill(net >= 0 ? Color.GREEN : Color.RED);

        if (totalIncome + totalExpense == 0) {
            triangle.setTranslateX(0);
            balanceStatus.setText("Balanced");
            balanceStatus.setTextFill(Color.GRAY);
            return;
        }
        double normalized;

        double total = totalIncome + totalExpense;

        // proportional balance (-1 .. 1)
        normalized = (total == 0) ? 0 : (totalIncome - totalExpense) / total;

        // snap at extremes
        if (normalized > 0.7) normalized = 1;
        if (normalized < -0.7) normalized = -1;

        // safety clamp
        normalized = Math.max(-1, Math.min(1, normalized));

        triangle.setTranslateX(normalized * BAR_HALF_WIDTH);

        if (normalized < 0) {
            balanceStatus.setText("Red Light!!");
            balanceStatus.setTextFill(Color.RED);
        }
        else if (normalized > 0) {
            balanceStatus.setText("Green Light!!");
            balanceStatus.setTextFill(Color.GREEN);
        }
        else {
            balanceStatus.setText("Balanced");
            balanceStatus.setTextFill(Color.ORANGE);
        }

    }
}
