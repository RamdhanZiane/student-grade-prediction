package com.gradeprediction.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import com.gradeprediction.common.DataExchangeCode;
import com.gradeprediction.common.DataInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientApp extends Application {

    private File selectedFile;
    DataInterface stub;
    private int port = 50000;

    public static List<List<String>> readCSVFile(String filename) {
        List<List<String>> studentGradesList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split by whitespace
                if (parts.length == 4) { // Assuming each line has exactly four parts: E0, F1, A2, E
                    List<String> grades = Arrays.asList(parts);
                    studentGradesList.add(grades);
                } else {
                    System.err.println("Invalid format in CSV file. Skipping line: " + line);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        return studentGradesList;
    }

    @Override
    public void start(Stage primaryStage)
            throws MalformedURLException, NotBoundException, RemoteException {
        try {
            stub = (DataInterface) Naming.lookup("rmi://localhost:" + port + "/DataServer");

            // Load the image
            Image image = new Image("ESI.png");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100); // Set the desired width
            imageView.setFitHeight(70); // Set the desired height

            // Create an HBox to hold the image
            HBox logoBox = new HBox(imageView);
            logoBox.setAlignment(Pos.TOP_LEFT); // Align to top left corner

            HBox.setMargin(logoBox, new Insets(10, 10, 10, 10));

            // Create a layout for the CSV input window
            VBox csvInputLayout = new VBox();
            csvInputLayout.setAlignment(Pos.CENTER);
            csvInputLayout.setSpacing(20); // Increased spacing for better readability
            csvInputLayout.setBackground(
                    new Background(new BackgroundFill(Color.rgb(229, 225, 214, 0.8), CornerRadii.EMPTY, Insets.EMPTY))); // Set
                                                                                                                         // background
                                                                                                                         // color
                                                                                                                         // with
                                                                                                                         // opacity
            csvInputLayout.setPadding(new Insets(0, 20, 0, 20)); // Add padding of 20 pixels to the left and right sides

            // Add the image box to the layout
            csvInputLayout.getChildren().add(logoBox);

            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV File");

            // Create a label for instructions
            Label instructionsLabel = new Label(
                    "This application is used to predict students' grades in the Bac exam.\n\t\tBefore using the application, please upload grades:");
            instructionsLabel.setStyle("-fx-font-size: 16px; ");
            instructionsLabel.setAlignment(Pos.CENTER); // Align instructions to center
            VBox.setMargin(instructionsLabel, new Insets(0, 0, 20, 0)); // Add bottom margin to separate from the button

            // Create a label to display the selected file name
            Label fileNameLabel = new Label("");

            // Create a button to load a CSV file
            Button loadFileButton = new Button("Upload grades file");

            loadFileButton.setStyle(
                    "-fx-background-color: #2ea44f; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;");
            loadFileButton.setPrefWidth(200); // Increased width for better visibility
            loadFileButton.setOnAction(e -> {
                selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    fileNameLabel.setText("Your file: " + selectedFile.getName());
                }
            });
            loadFileButton.setOnMousePressed(e -> loadFileButton.setStyle(
                    "-fx-background-color: #2ea44f; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6; -fx-translate-y: 2;"));
            loadFileButton.setOnMouseReleased(e -> loadFileButton.setStyle(
                    "-fx-background-color: #2ea44f; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;"));

            // Create a button to send the data
            Button sendDataButton = new Button("Send");
            sendDataButton.setStyle(
                    "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;");
            sendDataButton.setPrefWidth(100); // Set preferred width
            sendDataButton.setPrefHeight(40); // Set preferred height
            sendDataButton.setOnMousePressed(e -> sendDataButton.setStyle(
                    "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6; -fx-translate-y: 2;"));
            sendDataButton.setOnMouseReleased(e -> sendDataButton.setStyle(
                    "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;"));
            sendDataButton.setOnAction(e -> {
                if (selectedFile != null) {
                    // Here you can implement logic to send the file
                    // System.out.println("Sending file: " + selectedFile.getAbsolutePath());
                    // Read grades from the selected file
                    List<List<String>> studentGrades = readCSVFile(String.valueOf(selectedFile));

                    // Call the train function on the server
                    try {
                        // print the name of the file without the .csv extension
                        DataExchangeCode response = stub.receiveGrades(studentGrades,
                                selectedFile.getName().substring(0, selectedFile.getName().length() - 4));
                        System.out.println(response.getDescription());
                        if (response.equals(DataExchangeCode.GRADES_RECEIVED)) {
                            showMainWindow(primaryStage);
                        }
                        selectedFile = null;
                        fileNameLabel.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // showMainWindow(primaryStage); // Show the main window after sending the data
                } else {
                    System.out.println("No file selected.");
                }
            });
            VBox.setMargin(sendDataButton, new Insets(20, 0, 0, 0)); // Add top margin to separate from other components

            // Create a title for the "Send Data" window
            Label sendTitleLabel = new Label("Bac Prediction");
            sendTitleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            VBox.setMargin(sendTitleLabel, new Insets(0, 0, 30, 0)); // Add bottom margin to separate from the button

            // Add components to the layout
            csvInputLayout.getChildren().addAll(sendTitleLabel, instructionsLabel, loadFileButton, fileNameLabel,
                    sendDataButton);

            // Create scene for CSV input window
            Scene csvInputScene = new Scene(csvInputLayout, 700, 500);

            // Set the scene and show the stage
            primaryStage.setTitle("Bac");
            primaryStage.setScene(csvInputScene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainWindow(Stage primaryStage) {

        // Load the image
        Image image = new Image("ESI.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Set the desired width
        imageView.setFitHeight(70); // Set the desired height

        // Create an HBox to hold the image
        HBox logoBox = new HBox(imageView);
        logoBox.setAlignment(Pos.TOP_LEFT); // Align to top left corner

        primaryStage.setTitle("Bac");

        // Create a label for the note
        Label noteLabel = new Label("To predict the student grad in Bac enter the grades of student in 3 semesters");
        noteLabel.setStyle("-fx-font-size: 16px;"); // Double the font size and set text color to yellow

        // Creating labels
        Label firstSemesterLabel = new Label("First Semester:");
        Label secondSemesterLabel = new Label("Second Semester:");
        Label thirdSemesterLabel = new Label("Third Semester:");

        // Setting font size for input fields
        firstSemesterLabel.setStyle("-fx-font-size: 16px;");
        secondSemesterLabel.setStyle("-fx-font-size: 16px;");
        thirdSemesterLabel.setStyle("-fx-font-size: 16px;");

        // Creating text fields
        TextField firstSemesterField = new TextField();
        firstSemesterField.setPrefWidth(200);
        TextField secondSemesterField = new TextField();
        secondSemesterField.setPrefWidth(200);
        TextField thirdSemesterField = new TextField();
        thirdSemesterField.setPrefWidth(200);

        // Creating predict button
        Button predictButton = new Button("Predict");
        predictButton.setStyle(
                "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;");
        predictButton.setPrefWidth(100); // Set preferred width
        predictButton.setPrefHeight(40); // Set preferred height
        predictButton.setOnMousePressed(e -> predictButton.setStyle(
                "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6; -fx-translate-y: 2;"));
        predictButton.setOnMouseReleased(e -> predictButton.setStyle(
                "-fx-background-color: #fb8332; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; -fx-background-radius: 6;"));
        predictButton.setOnAction(e -> {
            // Display a message dialog with the entered values
            String firstSemesterValue = firstSemesterField.getText();
            String secondSemesterValue = secondSemesterField.getText();
            String thirdSemesterValue = thirdSemesterField.getText();

            List<String> grades = new ArrayList<>();
            grades.add(firstSemesterValue);
            grades.add(secondSemesterValue);
            grades.add(thirdSemesterValue);

            try {
                String response = stub.predict(grades);
                if (response == null) {
                    response = "Error: Model not trained yet";
                }
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Predicted Result");
                alert.setHeaderText(null);
                alert.setContentText(response);
                alert.showAndWait();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Creating layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // Adding elements to the layout
        grid.add(firstSemesterLabel, 0, 0);
        grid.add(firstSemesterField, 1, 0);
        grid.add(secondSemesterLabel, 0, 1);
        grid.add(secondSemesterField, 1, 1);
        grid.add(thirdSemesterLabel, 0, 2);
        grid.add(thirdSemesterField, 1, 2);
        grid.add(predictButton, 0, 3, 2, 1);

        // Creating pane for the button
        HBox buttonPane = new HBox();
        buttonPane.getChildren().add(predictButton);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(new Insets(20, 0, 0, 0)); // Add some padding to move it down

        // Creating main layout pane
        VBox mainPane = new VBox();
        mainPane.setAlignment(Pos.CENTER); // Center the main VBox
        mainPane.setSpacing(20); // Adding spacing between elements
        mainPane.setBackground(
                new Background(new BackgroundFill(Color.rgb(229, 225, 214, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        mainPane.setPadding(new Insets(0, 20, 0, 20)); // Add padding of 20 pixels to the left and right sides

        // Add the image box to the layout
        mainPane.getChildren().add(logoBox);

        // Creating title label
        Label titleLabel = new Label("Bac Prediction");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Adding title label to the layout
        mainPane.getChildren().add(titleLabel);

        // Adding the rest of the elements
        mainPane.getChildren().addAll(noteLabel, grid, buttonPane);

        // Creating scene
        Scene scene = new Scene(mainPane, 700, 500);

        // Setting the scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
