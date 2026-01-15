package org.fdu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Green Light Red Light");

        //Creating a grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));

        //Add text for user to see
        Text scenceTitle = new Text("Green Light Red Light");
        scenceTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenceTitle, 0,0,2,1);


        //Creating labels and text field for user to enter income
        Label income = new Label("Income: ");
        grid.add(income, 0,1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1,1);

        //Creating a button
        Button btn = new Button("Enter");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos. BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1,4);

        final Text actiontarget = new Text();
        grid.add(actiontarget,1,6);

        // VBox to hold all income entries
        VBox incomeList = new VBox(5);
        incomeList.setPadding(new Insets(10, 0, 0, 0));
        grid.add(incomeList, 0, 7, 2, 1);

        //Creates action when button is clicked on
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Retrieves user's input
                String userInput = userTextField.getText();

                if(userInput.isEmpty()){
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please enter an income!");
                }
                else{
                    Label incomeEntry = new Label("Income: $" + userInput);
                    incomeList.getChildren().add(incomeEntry);
                    userTextField.clear();
                }
            }
        });

        Scene scene = new Scene(grid, 400,500);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}



/*public class Main {
    public static void main(String[] args) {
        UI ui = new UI();
        ui.run_program();
    }
}*/
