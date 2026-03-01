package org.fdu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GraphicalUI extends Application {

    private TrackIncome  trackIncome  = new TrackIncome();
    private TrackExpense trackExpense = new TrackExpense();

    // ── Summary labels ────────────────────────────────────────────────────────
    private Label incomeGrossLabel;
    private Label incomeTakeHomeLabel;
    private Label expenseTotalLabel;
    private Label netTakeHomeLabel;
    private Label netGrossLabel;
    private Label balanceStatusLabel;

    // ── Tax breakdown panel (populated dynamically per hourly entry) ─────────
    private VBox taxCardContent;
    private VBox taxCard;


    // ── Entry list panels ─────────────────────────────────────────────────────
    private VBox incomeListDisplay;
    private VBox expenseListDisplay;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Green Light Red Light");

        ScrollPane root = new ScrollPane();
        root.setFitToWidth(true);
        root.setFitToHeight(false);
        root.setStyle("-fx-background-color: #1a1a2e;");

        VBox main = new VBox(18);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(24));
        main.setStyle("-fx-background-color: #1a1a2e;");
        main.setFillWidth(true);

        main.getChildren().addAll(
                buildHeader(),
                buildBalanceCard(),
                buildTaxCard(),
                buildInputForm(),
                buildEntryLists()
        );

        root.setContent(main);
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Header
    // ─────────────────────────────────────────────────────────────────────────
    private VBox buildHeader() {
        Label sub   = styledLabel("NJ Tax Calculator · Weekly Budget Tracker",
                "#64748b", 11, false);
        Label title = styledLabel("Green Light  Red Light", "white", 28, true);
        VBox box = new VBox(4, sub, title);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Balance card
    // ─────────────────────────────────────────────────────────────────────────
    private VBox buildBalanceCard() {
        VBox card = card("#0f3460");

        Label title = styledLabel("Weekly Balance", "#94a3b8", 11, true);

        netTakeHomeLabel = styledLabel("$0.00", "white", 38, true);
        netGrossLabel    = styledLabel("Pre-tax net: $0.00/wk", "#94a3b8", 12, false);
        balanceStatusLabel = styledLabel("Balanced", "#94a3b8", 13, true);

        incomeGrossLabel    = styledLabel("Income: $0.00/wk",     "#34d399", 13, true);
        incomeTakeHomeLabel = styledLabel("After Tax: $0.00/wk",  "#6ee7b7", 12, false);
        expenseTotalLabel   = styledLabel("Expenses: $0.00/wk",   "#f87171", 13, true);

        VBox incCol = new VBox(3,
                styledLabel("Income", "#64748b", 10, false),
                incomeGrossLabel, incomeTakeHomeLabel);
        VBox expCol = new VBox(3,
                styledLabel("Expenses", "#64748b", 10, false),
                expenseTotalLabel);
        HBox cols = new HBox(40, incCol, expCol);
        cols.setAlignment(Pos.CENTER_LEFT);

        // Bar



        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #34d399;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #34d399;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 14;" +
                        "-fx-cursor: hand;");
        viewDetailsBtn.setOnAction(e -> {
            boolean nowVisible = !taxCard.isVisible();
            taxCard.setVisible(nowVisible);
            taxCard.setManaged(nowVisible);
            viewDetailsBtn.setText(nowVisible ? "Hide Details" : "View Details");
        });
        Button newWeekBtn = new Button("↺  New Week");
        newWeekBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #f87171;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #f87171;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 14;" +
                        "-fx-cursor: hand;");
        newWeekBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("New Week Rollover");
            confirm.setHeaderText("Start a new week?");
            confirm.setContentText(
                    "This will clear all incidental income and expenses.\n" +
                            "Recurring entries will be kept.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Clear incidental lists
                    trackIncome.incomeIncidental.clear();
                    trackExpense.expenseIncidental.clear();
                    // Remove incidental rows from the display
                    rebuildEntryLists();
                    updateSummary();
                }
            });
        });

        Region btnSpacer = new Region();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);
        HBox btnRow = new HBox(10, newWeekBtn, btnSpacer, viewDetailsBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(title, netTakeHomeLabel, netGrossLabel,
                balanceStatusLabel, cols, btnRow);
        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tax card
    // ─────────────────────────────────────────────────────────────────────────
    private VBox buildTaxCard() {
        taxCard = card("#16213e");
        Label title = styledLabel("Pay Stub Summary  (NJ · Single Filer · 2024)",
                "#64748b", 11, true);

        taxCardContent = new VBox(10);
        Label placeholder = styledLabel("Add an hourly income entry to see your pay stub summary.",
                "#475569", 12, false);
        taxCardContent.getChildren().add(placeholder);

        taxCard.getChildren().addAll(title, taxCardContent);

        // Hidden by default — shown when user clicks View Details
        taxCard.setVisible(false);
        taxCard.setManaged(false);

        return taxCard;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Input form
    // ─────────────────────────────────────────────────────────────────────────
    private VBox buildInputForm() {
        VBox card = card("white");

        Label formTitle = styledLabel("Add Entry", "#1e293b", 14, true);

        // ── Type toggle ───────────────────────────────────────────────────────
        RadioButton incomeRadio  = new RadioButton("Income");
        RadioButton expenseRadio = new RadioButton("Expense");
        ToggleGroup typeTG = new ToggleGroup();
        incomeRadio.setToggleGroup(typeTG);
        expenseRadio.setToggleGroup(typeTG);
        incomeRadio.setSelected(true);
        styleRadio(incomeRadio); styleRadio(expenseRadio);

        // ── Occurrence + Monthly ──────────────────────────────────────────────
        CheckBox recurringCheck = new CheckBox("Recurring");
        CheckBox monthlyCheck   = new CheckBox("Monthly amount (converts to weekly)");
        recurringCheck.setSelected(true);
        styleCheck(recurringCheck); styleCheck(monthlyCheck);

        // ── Mode toggle: flat vs hourly ───────────────────────────────────────
        RadioButton flatRadio   = new RadioButton("Flat Amount");
        RadioButton hourlyRadio = new RadioButton("Hourly + Hours");
        ToggleGroup modeTG = new ToggleGroup();
        flatRadio.setToggleGroup(modeTG);
        hourlyRadio.setToggleGroup(modeTG);
        flatRadio.setSelected(true);
        styleRadio(flatRadio); styleRadio(hourlyRadio);

        // ── Flat panel ────────────────────────────────────────────────────────
        TextField nameField   = styledTextField("e.g. Salary, Rent…");
        TextField amountField = styledTextField("0.00");

        GridPane flatGrid = new GridPane();
        flatGrid.setHgap(12); flatGrid.setVgap(10);
        colPercents(flatGrid, 50, 50);
        flatGrid.add(labeled("Name",       nameField),   0, 0);
        flatGrid.add(labeled("Amount ($)", amountField), 1, 0);

        VBox flatPanel = new VBox(10, flatGrid, monthlyCheck);

        // ── Hourly panel ──────────────────────────────────────────────────────
        ComboBox<PayFrequency> freqBox = new ComboBox<>();
        freqBox.getItems().addAll(PayFrequency.values());
        freqBox.setValue(PayFrequency.BIWEEKLY);
        freqBox.setStyle("-fx-font-size: 13; -fx-background-radius: 8;");
        freqBox.setMaxWidth(Double.MAX_VALUE);

        TextField hNameField  = styledTextField("e.g. Part-time Job");
        TextField rateField   = styledTextField("0.00");
        TextField hoursField  = styledTextField("e.g. 80");
        TextField k401Field   = styledTextField("e.g. 18.65  (optional)");

        // Live preview labels
        Label previewGross = styledLabel("Weekly gross: —", "#059669", 12, true);
        Label previewNet   = styledLabel("Weekly net:   —", "#6ee7b7", 12, false);
        Label previewOT    = styledLabel("", "#fbbf24", 11, false);

        // Update preview whenever any hourly field changes
        Runnable updatePreview = () -> {
            try {
                double rate  = Double.parseDouble(rateField.getText().trim());
                double hrs   = Double.parseDouble(hoursField.getText().trim());
                PayFrequency f = freqBox.getValue();
                double wkHrs = f.weeklyHours(hrs);
                double otHrs = Math.max(wkHrs - 40.0, 0);
                double gross = (Math.min(wkHrs, 40.0) * rate) + (otHrs * rate * 1.5);
                double k401  = k401Field.getText().trim().isEmpty() ? 0
                        : Double.parseDouble(k401Field.getText().trim());
                double net   = TaxCalculator.weeklyTakeHome(gross, k401);
                previewGross.setText(String.format("Weekly gross: $%.2f", gross));
                previewNet.setText(String.format(  "Weekly net:   $%.2f  (after NJ+Federal+FICA)", net));
                previewOT.setText(otHrs > 0
                        ? String.format("OT: %.1f hrs/wk avg @ $%.2f/hr (1.5×)", otHrs, rate * 1.5)
                        : "No overtime");
            } catch (NumberFormatException ignored) {
                previewGross.setText("Weekly gross: —");
                previewNet.setText("Weekly net:   —");
                previewOT.setText("");
            }
        };

        rateField.setOnKeyReleased(e -> updatePreview.run());
        hoursField.setOnKeyReleased(e -> updatePreview.run());
        freqBox.setOnAction(e -> updatePreview.run());
        k401Field.setOnKeyReleased(e -> updatePreview.run());

        GridPane hourlyGrid = new GridPane();
        hourlyGrid.setHgap(12); hourlyGrid.setVgap(10);
        colPercents(hourlyGrid, 50, 50);
        hourlyGrid.add(labeled("Name",            hNameField), 0, 0);
        hourlyGrid.add(labeled("Pay Frequency",   freqBox),    1, 0);
        hourlyGrid.add(labeled("Hourly Rate ($)", rateField),  0, 1);
        hourlyGrid.add(labeled("Hours per Period", hoursField), 1, 1);

        // 401K optional field
        Label k401Label = styledLabel("Weekly 401K / Pre-Tax Deduction ($)", "#64748b", 11, true);
        Label k401Note  = styledLabel("Reduces federal taxable income. Leave blank if none.", "#94a3b8", 10, false);
        VBox k401Box = new VBox(4, k401Label, k401Field, k401Note);

        VBox previewBox = new VBox(3, previewGross, previewNet, previewOT);
        previewBox.setPadding(new Insets(8, 12, 8, 12));
        previewBox.setStyle("-fx-background-color: #f0fdf4; -fx-background-radius: 8;");

        VBox hourlyPanel = new VBox(10, hourlyGrid, k401Box, previewBox);

        // ── Toggle panel visibility ───────────────────────────────────────────
        hourlyPanel.setVisible(false);
        hourlyPanel.setManaged(false);
        monthlyCheck.setVisible(true);
        monthlyCheck.setManaged(true);

        flatRadio.setOnAction(e -> {
            flatPanel.setVisible(true);   flatPanel.setManaged(true);
            hourlyPanel.setVisible(false); hourlyPanel.setManaged(false);
            monthlyCheck.setVisible(true); monthlyCheck.setManaged(true);
        });
        hourlyRadio.setOnAction(e -> {
            flatPanel.setVisible(false);  flatPanel.setManaged(false);
            hourlyPanel.setVisible(true); hourlyPanel.setManaged(true);
            monthlyCheck.setVisible(false); monthlyCheck.setManaged(false);
        });

        // Hide mode toggle when Expense is selected
        HBox modeRow = new HBox(16, flatRadio, hourlyRadio);
        Label modeLabel = styledLabel("Entry Mode:", "#64748b", 11, true);
        VBox modeSection = new VBox(6, modeLabel, modeRow);

        incomeRadio.setOnAction(e -> {
            modeSection.setVisible(true); modeSection.setManaged(true);
        });
        expenseRadio.setOnAction(e -> {
            modeSection.setVisible(false); modeSection.setManaged(false);
            flatRadio.setSelected(true);
            flatPanel.setVisible(true);   flatPanel.setManaged(true);
            hourlyPanel.setVisible(false); hourlyPanel.setManaged(false);
            monthlyCheck.setVisible(true); monthlyCheck.setManaged(true);
        });

        // ── Add button ────────────────────────────────────────────────────────
        Label msgLabel = styledLabel("", "#ef4444", 12, false);
        Button addBtn  = new Button("+ Add Entry");
        styleAddBtn(addBtn, "#10b981");
        addBtn.setMaxWidth(Double.MAX_VALUE);

        incomeRadio.setOnAction(e -> {
            modeSection.setVisible(true); modeSection.setManaged(true);
            styleAddBtn(addBtn, "#10b981");
        });
        expenseRadio.setOnAction(e -> {
            modeSection.setVisible(false); modeSection.setManaged(false);
            flatRadio.setSelected(true);
            flatPanel.setVisible(true);   flatPanel.setManaged(true);
            hourlyPanel.setVisible(false); hourlyPanel.setManaged(false);
            monthlyCheck.setVisible(true); monthlyCheck.setManaged(true);
            styleAddBtn(addBtn, "#ef4444");
        });

        addBtn.setOnAction(e -> handleAdd(
                incomeRadio, recurringCheck, monthlyCheck,
                flatRadio, hourlyRadio,
                nameField, amountField,
                hNameField, rateField, hoursField, freqBox, k401Field,
                msgLabel, addBtn
        ));

        card.getChildren().addAll(
                formTitle,
                new HBox(16, incomeRadio, expenseRadio),
                new HBox(16, recurringCheck),
                modeSection,
                flatPanel,
                hourlyPanel,
                msgLabel,
                addBtn
        );
        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Handle Add button click
    // ─────────────────────────────────────────────────────────────────────────
    private void handleAdd(
            RadioButton incomeRadio, CheckBox recurringCheck, CheckBox monthlyCheck,
            RadioButton flatRadio, RadioButton hourlyRadio,
            TextField nameField, TextField amountField,
            TextField hNameField, TextField rateField, TextField hoursField,
            ComboBox<PayFrequency> freqBox, TextField k401Field,
            Label msgLabel, Button addBtn) {

        msgLabel.setText("");
        boolean isIncome    = incomeRadio.isSelected();
        boolean isRecurring = recurringCheck.isSelected();
        boolean isHourly    = hourlyRadio.isSelected() && isIncome;

        if (isHourly) {
            // ── Hourly income ─────────────────────────────────────────────────
            String name = hNameField.getText().trim();
            if (name.isEmpty()) { msgLabel.setText("⚠ Enter a name."); return; }

            double rate, hours;
            try { rate  = Double.parseDouble(rateField.getText().trim());  }
            catch (NumberFormatException ex) { msgLabel.setText("⚠ Enter a valid hourly rate."); return; }
            try { hours = Double.parseDouble(hoursField.getText().trim()); }
            catch (NumberFormatException ex) { msgLabel.setText("⚠ Enter valid hours."); return; }
            if (rate <= 0 || hours <= 0) { msgLabel.setText("⚠ Rate and hours must be positive."); return; }

            PayFrequency freq = freqBox.getValue();
            double weeklyK401 = 0;
            try {
                String k401Text = k401Field.getText().trim();
                if (!k401Text.isEmpty()) weeklyK401 = Double.parseDouble(k401Text);
            } catch (NumberFormatException ex) {
                msgLabel.setText("⚠ Enter a valid 401K amount or leave blank.");
                return;
            }

            Income entry = isRecurring
                    ? trackIncome.addIncomeRecurringHourly(name, rate, hours, freq, weeklyK401)
                    : trackIncome.addIncomeIncidentalHourly(name, rate, hours, freq, weeklyK401);

            addEntryRow(name, entry, true, isRecurring);
            hNameField.clear(); rateField.clear(); hoursField.clear(); k401Field.clear();

        } else {
            // ── Flat amount ───────────────────────────────────────────────────
            String name = nameField.getText().trim();
            if (name.isEmpty()) { msgLabel.setText("⚠ Enter a name."); return; }

            float value;
            try { value = Float.parseFloat(amountField.getText().trim()); }
            catch (NumberFormatException ex) { msgLabel.setText("⚠ Enter a valid amount."); return; }
            if (value < 0) { msgLabel.setText("⚠ Amount must be positive."); return; }

            boolean isMonthly = monthlyCheck.isSelected();

            if (isIncome) {
                Income entry = isRecurring
                        ? trackIncome.addIncomeRecurring(name, value, isMonthly)
                        : trackIncome.addIncomeIncidental(name, value, isMonthly);
                addEntryRow(name, entry, true, isRecurring);
            } else {
                Expense entry = isRecurring
                        ? trackExpense.addExpenseRecurring(name, value, isMonthly)
                        : trackExpense.addExpenseIncidental(name, value, isMonthly);
                addExpenseRow(name, entry, isRecurring);
            }
            nameField.clear(); amountField.clear(); monthlyCheck.setSelected(false);
        }

        updateSummary();
        msgLabel.setText("");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry rows
    // ─────────────────────────────────────────────────────────────────────────
    private void addEntryRow(String name, Income entry, boolean isIncome, boolean isRecurring) {
        double weekly = entry.weeklyGross();
        String sub;
        if (entry.isHourly) {
            double wkHrs = entry.payFrequency.weeklyHours(entry.hoursPerPeriod);
            double otHrs = Math.max(wkHrs - 40.0, 0);
            sub = String.format("$%.2f/hr · %.0f hrs/%s · %s%s",
                    entry.hourlyRate, entry.hoursPerPeriod, entry.payFrequency,
                    isRecurring ? "Recurring" : "Incidental",
                    otHrs > 0 ? String.format(" · %.1f OT hrs/wk", otHrs) : "");
        } else {
            sub = (isRecurring ? "Recurring" : "Incidental")
                    + (entry.isMonthly ? " · Monthly → weekly" : " · Weekly");
        }

        String amtText = String.format("$%.2f/wk", weekly);
        // Flat entries are already net — only show after-tax label for hourly
        String netText = entry.isHourly
                ? String.format("Net: $%.2f/wk", TaxCalculator.weeklyTakeHome(weekly))
                : null;
        String netColor = entry.isHourly ? "#6ee7b7" : null;

        HBox row = buildRow(name, sub, amtText, netText, "#34d399", netColor,
                "rgba(52,211,153,0.10)", e -> {
                    if (isRecurring) trackIncome.deleteIncomeRecurring(entry);
                    else             trackIncome.deleteIncomeIncidental(entry);
                }
        );
        incomeListDisplay.getChildren().add(row);
    }

    private void addExpenseRow(String name, Expense entry, boolean isRecurring) {
        float weekly = entry.weeklyAmount();
        String sub = (isRecurring ? "Recurring" : "Incidental")
                + (entry.isMonthly ? " · Monthly → weekly" : " · Weekly");
        String amtText = String.format("$%.2f/wk", weekly);

        HBox row = buildRow(name, sub, amtText, null, "#f87171", null,
                "rgba(248,113,113,0.10)", e -> {
                    if (isRecurring) trackExpense.deleteExpenseRecurring(entry);
                    else             trackExpense.deleteExpenseIncidental(entry);
                }
        );
        expenseListDisplay.getChildren().add(row);
    }

    /** Generic row builder shared by income and expense rows. */
    private HBox buildRow(String name, String sub, String amtText, String netText,
                          String amtColor, String netColor, String bgColor,
                          javafx.event.EventHandler<ActionEvent> onDelete) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-radius: 8; -fx-background-color: " + bgColor + ";");

        Button del = new Button("×");
        del.setStyle("-fx-background-color: " + amtColor + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 50; " +
                "-fx-min-width: 24; -fx-min-height: 24; -fx-max-width: 24; -fx-max-height: 24;");
        del.setOnAction(e -> {
            onDelete.handle(e);
            ((VBox) row.getParent()).getChildren().remove(row);
            updateSummary();
        });

        Label nameL = styledLabel(name, "white", 13, true);
        Label subL  = styledLabel(sub,  "#cbd5e1", 11, false);
        VBox info   = new VBox(2, nameL, subL);

        Label amtL = styledLabel(amtText, amtColor, 13, true);
        VBox amtBox;
        if (netText != null && netColor != null) {
            Label netL = styledLabel(netText, netColor, 11, false);
            amtBox = new VBox(2, amtL, netL);
        } else {
            amtBox = new VBox(amtL);
        }
        amtBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(del, info, spacer, amtBox);
        return row;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry list panels
    // ─────────────────────────────────────────────────────────────────────────
    private HBox buildEntryLists() {
        incomeListDisplay  = new VBox(6);
        expenseListDisplay = new VBox(6);

        ScrollPane iScroll = styledScroll(incomeListDisplay);
        ScrollPane eScroll = styledScroll(expenseListDisplay);

        VBox iCol = new VBox(8, styledLabel("💰 Income",   "#34d399", 12, true), iScroll);
        VBox eCol = new VBox(8, styledLabel("💸 Expenses", "#f87171", 12, true), eScroll);
        HBox.setHgrow(iCol, Priority.ALWAYS);
        HBox.setHgrow(eCol, Priority.ALWAYS);

        HBox box = new HBox(16, iCol, eCol);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    /** Redraws both entry list displays from the current TrackIncome/TrackExpense data. */
    private void rebuildEntryLists() {
        incomeListDisplay.getChildren().clear();
        expenseListDisplay.getChildren().clear();

        for (Income i  : trackIncome.incomeRecurring)    addEntryRow(i.incomeName,  i,  true,  true);
        for (Income i  : trackIncome.incomeIncidental)   addEntryRow(i.incomeName,  i,  true,  false);
        for (Expense e : trackExpense.expenseRecurring)  addExpenseRow(e.expenseName, e, true);
        for (Expense e : trackExpense.expenseIncidental) addExpenseRow(e.expenseName, e, false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Rebuild pay stub summary cards (one per hourly income entry)
    // ─────────────────────────────────────────────────────────────────────────
    private void rebuildTaxCard() {
        taxCardContent.getChildren().clear();

        // Collect all hourly entries from both lists
        java.util.List<Income> hourlyEntries = new java.util.ArrayList<>();
        for (Income i : trackIncome.incomeRecurring)  if (i.isHourly) hourlyEntries.add(i);
        for (Income i : trackIncome.incomeIncidental) if (i.isHourly) hourlyEntries.add(i);

        if (hourlyEntries.isEmpty()) {
            taxCardContent.getChildren().add(
                    styledLabel("Add an hourly income entry to see your pay stub summary.",
                            "#475569", 12, false));
            return;
        }

        for (Income entry : hourlyEntries) {
            double periodGross = entry.hoursPerPeriod * entry.hourlyRate;

            // Overtime: figure out weekly hours, then scale OT back to period
            double avgWeeklyHrs = entry.payFrequency.weeklyHours(entry.hoursPerPeriod);
            double otHrsPerWeek = Math.max(avgWeeklyHrs - 40.0, 0);
            double regHrsPerWeek = Math.min(avgWeeklyHrs, 40.0);
            // Per-period gross with OT
            double weeksPerPeriod = entry.payFrequency.weeksPerPeriod();
            double regPay = regHrsPerWeek * weeksPerPeriod * entry.hourlyRate;
            double otPay  = otHrsPerWeek  * weeksPerPeriod * entry.hourlyRate * 1.5;
            periodGross   = regPay + otPay;

            // Per-period deductions (scale from weekly)
            double weeklyGross = entry.weeklyGross();
            double preTax      = entry.weeklyPreTax * weeksPerPeriod;
            double federal     = TaxCalculator.federalWithholdingWeekly(weeklyGross, entry.weeklyPreTax) * weeksPerPeriod;
            double nj          = TaxCalculator.njStateTax(weeklyGross * 52)  / 52 * weeksPerPeriod;
            double ss          = weeklyGross * 0.0620 * weeksPerPeriod;
            double medicare    = weeklyGross * 0.0145 * weeksPerPeriod;
            double sui         = weeklyGross * 0.00425 * weeksPerPeriod;
            double sdi         = weeklyGross * 0.00190 * weeksPerPeriod;
            double fli         = weeklyGross * 0.00228 * weeksPerPeriod;
            double periodNet   = periodGross - preTax - federal - nj - ss - medicare - sui - sdi - fli;

            // Build entry card
            VBox entryCard = new VBox(6);
            entryCard.setPadding(new Insets(10, 12, 10, 12));
            entryCard.setStyle("-fx-background-color: rgba(255,255,255,0.05); " +
                    "-fx-background-radius: 8;");

            // Header: name + pay period
            Label nameLabel = styledLabel(
                    entry.incomeName + "  ·  " + entry.payFrequency + " Pay",
                    "white", 13, true);

            // Hours line
            String hoursLine = String.format("%.1f hrs @ $%.2f/hr", entry.hoursPerPeriod, entry.hourlyRate);
            if (otHrsPerWeek > 0) {
                hoursLine += String.format("  (incl. %.1f OT hrs/wk avg @ $%.2f/hr)",
                        otHrsPerWeek, entry.hourlyRate * 1.5);
            }
            Label hoursLabel = styledLabel(hoursLine, "#94a3b8", 11, false);

            // Deductions grid
            int rowIdx = 0;
            GridPane g = new GridPane();
            g.setHgap(16); g.setVgap(4);
            addStubRow(g, rowIdx++, "Gross Pay:",         periodGross, "white");
            if (preTax > 0)
                addStubRow(g, rowIdx++, "401K (pre-tax):", -preTax,    "#818cf8");
            addStubRow(g, rowIdx++, "Federal Tax:",       -federal,    "#f87171");
            addStubRow(g, rowIdx++, "NJ State Tax:",      -nj,         "#fb923c");
            addStubRow(g, rowIdx++, "Social Security:",   -ss,         "#fbbf24");
            addStubRow(g, rowIdx++, "Medicare:",          -medicare,   "#a78bfa");
            addStubRow(g, rowIdx++, "NJ SUI:",            -sui,        "#94a3b8");
            addStubRow(g, rowIdx++, "NJ SDI:",            -sdi,        "#94a3b8");
            addStubRow(g, rowIdx++, "NJ FLI:",            -fli,        "#94a3b8");

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #334155;");

            // Net pay line
            HBox netRow = new HBox();
            netRow.setAlignment(Pos.CENTER_LEFT);
            Label netLbl = styledLabel("Net Pay:", "#94a3b8", 13, true);
            Label netVal = styledLabel(String.format("$%.2f", periodNet), "#34d399", 14, true);
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            netRow.getChildren().addAll(netLbl, sp, netVal);

            entryCard.getChildren().addAll(nameLabel, hoursLabel, g, sep, netRow);
            taxCardContent.getChildren().add(entryCard);

            // Separator between entries if multiple
            if (hourlyEntries.indexOf(entry) < hourlyEntries.size() - 1) {
                Separator entrySep = new Separator();
                entrySep.setStyle("-fx-background-color: #1e293b;");
                taxCardContent.getChildren().add(entrySep);
            }
        }
    }

    private void addStubRow(GridPane g, int row, String label, double amount, String color) {
        Label lbl = styledLabel(label, "#64748b", 12, false);
        Label val = styledLabel(String.format("%s$%.2f",
                amount < 0 ? "-" : "", Math.abs(amount)), color, 12, true);
        g.add(lbl, 0, row);
        g.add(val, 1, row);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Update all summary labels + balance bar
    // ─────────────────────────────────────────────────────────────────────────
    private void updateSummary() {
        float   wkGross   = trackIncome.totalWeeklyGross();
        double  wkNet     = trackIncome.totalWeeklyTakeHome();
        float   wkExpense = trackExpense.totalWeeklyExpense();
        double  netGross  = wkGross  - wkExpense;
        double  netTH     = wkNet    - wkExpense;

        incomeGrossLabel.setText(String.format("Income: $%.2f/wk", wkGross));
        incomeTakeHomeLabel.setText(String.format("After Tax: $%.2f/wk", wkNet));
        expenseTotalLabel.setText(String.format("Expenses: $%.2f/wk",  wkExpense));
        netGrossLabel.setText(String.format("Pre-tax net: $%.2f/wk", netGross));
        netTakeHomeLabel.setText(String.format("$%.2f", netTH));
        netTakeHomeLabel.setStyle("-fx-font-size: 38; -fx-font-weight: bold; -fx-text-fill: "
                + (netTH >= 0 ? "#34d399" : "#f87171") + ";");

        if (netTH > 0) {
            balanceStatusLabel.setText("🟢 Green Light!");
            balanceStatusLabel.setStyle("-fx-text-fill: #34d399; -fx-font-size: 13; -fx-font-weight: bold;");
        } else if (netTH < 0) {
            balanceStatusLabel.setText("🔴 Red Light!");
            balanceStatusLabel.setStyle("-fx-text-fill: #f87171; -fx-font-size: 13; -fx-font-weight: bold;");
        } else {
            balanceStatusLabel.setText("⚖ Balanced");
            balanceStatusLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13; -fx-font-weight: bold;");
        }

        // Rebuild pay stub summary — one card per hourly entry
        rebuildTaxCard();

    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private VBox card(String bg) {
        VBox c = new VBox(12);
        c.setPadding(new Insets(18));
        c.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 14;");
        c.setMaxWidth(Double.MAX_VALUE);
        return c;
    }

    private Label styledLabel(String text, String color, int size, boolean bold) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + ";"
                + (bold ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-radius: 8; -fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 8; -fx-padding: 8; -fx-font-size: 13;");
        return tf;
    }

    private VBox labeled(String labelText, javafx.scene.Node field) {
        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #64748b;");
        VBox b = new VBox(5, l, field);
        VBox.setVgrow(field, Priority.ALWAYS);
        return b;
    }

    private void colPercents(GridPane g, double... pcts) {
        for (double p : pcts) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(p);
            g.getColumnConstraints().add(cc);
        }
    }

    private ScrollPane styledScroll(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setPrefHeight(220); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");
        content.setPadding(new Insets(8));
        content.setStyle("-fx-background-color: #16213e;");
        return sp;
    }


    private void styleRadio(RadioButton rb) {
        rb.setStyle("-fx-font-size: 13;");
    }

    private void styleCheck(CheckBox cb) {
        cb.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");
    }

    private void styleAddBtn(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14; " +
                "-fx-background-radius: 10; -fx-padding: 10 20;");
    }
}