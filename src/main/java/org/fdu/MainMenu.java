package org.fdu;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Green Light Red Light");

        StackPane root = new StackPane();

        // ── Background gradient ───────────────────────────────────────────────
        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(root.widthProperty());
        bg.heightProperty().bind(root.heightProperty());
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#0f0c29")),
                new Stop(0.5, Color.web("#1a1a2e")),
                new Stop(1.0, Color.web("#0f3460"))));

        // ── Decorative accent circles ─────────────────────────────────────────
        StackPane circles = new StackPane();
        circles.setMouseTransparent(true);
        addCircle(circles,  320, 320, "#34d399", 0.06, -200, -180);
        addCircle(circles,  240, 240, "#f87171", 0.05,  220,  180);
        addCircle(circles,  160, 160, "#6ee7b7", 0.04, -100,  200);

        // ── Center content ────────────────────────────────────────────────────
        VBox content = new VBox(32);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(480);
        content.setPadding(new Insets(60));

        // Title block
        Label subtitle = new Label("NJ BUDGET TRACKER");
        subtitle.setStyle(
                "-fx-text-fill: #34d399;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 4;");

        Label title = new Label("Green Light\nRed Light");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 52px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-alignment: center;" +
                        "-fx-line-spacing: -4;");
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label tagline = new Label("Know your money. Plan your week.");
        tagline.setStyle(
                "-fx-text-fill: #64748b;" +
                        "-fx-font-size: 14px;");

        VBox titleBlock = new VBox(10, subtitle, title, tagline);
        titleBlock.setAlignment(Pos.CENTER);

        // Buttons
        Button startBtn = menuButton("▶  Start", "#34d399", "#0f3460", true);
        Button helpBtn  = menuButton("?   Help",  "transparent", "#34d399", false);

        startBtn.setOnAction(e -> launchApp(primaryStage));
        helpBtn.setOnAction(e  -> showHelp(primaryStage));

        VBox buttons = new VBox(14, startBtn, helpBtn);
        buttons.setAlignment(Pos.CENTER);

        // Version tag
        Label version = new Label("v1.0  ·  Weekly Pay Estimator");
        version.setStyle("-fx-text-fill: #334155; -fx-font-size: 11px;");

        content.getChildren().addAll(titleBlock, buttons, version);

        root.getChildren().addAll(bg, circles, content);

        // ── Fade-in animation ─────────────────────────────────────────────────
        content.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(700), content);
        fade.setFromValue(0); fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(700), content);
        slide.setFromY(24); slide.setToY(0);

        ParallelTransition intro = new ParallelTransition(fade, slide);
        intro.setInterpolator(Interpolator.EASE_OUT);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        intro.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Launch the main app
    // ─────────────────────────────────────────────────────────────────────────
    private void launchApp(Stage stage) {
        FadeTransition out = new FadeTransition(Duration.millis(300),
                stage.getScene().getRoot());
        out.setFromValue(1); out.setToValue(0);
        out.setOnFinished(e -> {
            try {
                new GraphicalUI().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        out.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Help screen
    // ─────────────────────────────────────────────────────────────────────────
    private void showHelp(Stage owner) {
        Stage helpStage = new Stage();
        helpStage.setTitle("How It Works");
        helpStage.initOwner(owner);
        helpStage.setResizable(false);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #1a1a2e;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(36));
        content.setStyle("-fx-background-color: #1a1a2e;");
        content.setMaxWidth(560);

        content.getChildren().addAll(
                helpTitle("How Green Light Red Light Works"),
                helpSection("💡 What Is This?",
                        "This app helps you estimate your weekly take-home pay and track " +
                                "whether your income covers your expenses. A green light means you're " +
                                "in surplus — a red light means you're in deficit."),
                helpSection("💰 Adding Income",
                        "Choose between two modes:\n\n" +
                                "• Flat Amount — enter a fixed weekly or monthly amount. " +
                                "Use this if you already know your net pay. No taxes are applied.\n\n" +
                                "• Hourly + Hours — enter your hourly rate, hours per pay period, " +
                                "and pay frequency. The app calculates your gross pay (including " +
                                "overtime at 1.5× after 40 hrs/week) and estimates all deductions."),
                helpSection("🧾 Tax Calculations (Hourly Only)",
                        "For hourly entries, the app estimates:\n\n" +
                                "• Federal Income Tax  (IRS Pub. 15-T, 2024, standard W-4)\n" +
                                "• NJ State Income Tax  (2024 brackets)\n" +
                                "• Social Security      6.2%\n" +
                                "• Medicare             1.45%\n" +
                                "• NJ SUI               0.425%\n" +
                                "• NJ SDI               0.19%\n" +
                                "• NJ FLI               0.228%\n\n" +
                                "Note: Federal is an estimate assuming a standard W-4 with no " +
                                "extra allowances. Your actual withholding may be slightly lower " +
                                "depending on your W-4 elections."),
                helpSection("📋 Pay Frequency",
                        "• Weekly       — 52 paychecks/year\n" +
                                "• Bi-Weekly    — every 2 weeks, 26 paychecks/year\n" +
                                "• Semi-Monthly — 1st & 15th, 24 paychecks/year\n" +
                                "• Monthly      — 12 paychecks/year\n\n" +
                                "Bi-Weekly and Semi-Monthly may look similar but bi-weekly periods " +
                                "are always exactly 14 days, while semi-monthly periods vary slightly."),
                helpSection("🏦 401K / Pre-Tax Deduction",
                        "If your job offers a 401K or other pre-tax benefit, enter your " +
                                "weekly contribution amount. This reduces your federal taxable income " +
                                "but does not reduce Social Security, Medicare, or NJ taxes."),
                helpSection("💸 Adding Expenses",
                        "Switch to Expense mode and enter any weekly or monthly cost — " +
                                "rent, subscriptions, groceries, etc. Monthly amounts are " +
                                "automatically converted to weekly (× 12 ÷ 52)."),
                helpSection("🟢 Green Light / 🔴 Red Light",
                        "The balance card at the top shows your weekly net take-home after " +
                                "expenses. If it's positive you get a Green Light — your income " +
                                "covers your costs. If it's negative you get a Red Light — " +
                                "time to adjust something.")
        );

        Button closeBtn = menuButton("Close", "#34d399", "#0f3460", true);
        closeBtn.setOnAction(e -> helpStage.close());
        closeBtn.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().add(closeBtn);
        scroll.setContent(content);

        helpStage.setScene(new Scene(scroll, 900, 700));
        helpStage.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private Button menuButton(String text, String bg, String border, boolean filled) {
        Button btn = new Button(text);
        btn.setMaxWidth(320);
        btn.setPrefWidth(320);
        btn.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + (filled ? "white" : "#34d399") + ";" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 14 0;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + border + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-cursor: hand;");

        // Hover effects
        String hoverBg    = filled ? "#22c55e" : "rgba(52,211,153,0.08)";
        String normalBg   = bg;
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle()
                .replace("-fx-background-color: " + normalBg, "-fx-background-color: " + hoverBg)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
                .replace("-fx-background-color: " + hoverBg, "-fx-background-color: " + normalBg)));

        return btn;
    }

    private Label helpTitle(String text) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;");
        l.setWrapText(true);
        return l;
    }

    private VBox helpSection(String heading, String body) {
        Label h = new Label(heading);
        h.setStyle(
                "-fx-text-fill: #34d399;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;");

        Label b = new Label(body);
        b.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                        "-fx-font-size: 13px;");
        b.setWrapText(true);

        VBox section = new VBox(6, h, b);
        section.setPadding(new Insets(14));
        section.setStyle(
                "-fx-background-color: #0f3460;" +
                        "-fx-background-radius: 10;");
        return section;
    }

    private void addCircle(StackPane pane, double w, double h,
                           String color, double opacity, double tx, double ty) {
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(w); r.setArcHeight(h);
        r.setFill(Color.web(color, opacity));
        r.setTranslateX(tx); r.setTranslateY(ty);
        pane.getChildren().add(r);
    }

    public static void main(String[] args) {
        launch(args);
    }
}