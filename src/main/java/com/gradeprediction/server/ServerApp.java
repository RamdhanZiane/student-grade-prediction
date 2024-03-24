package com.gradeprediction.server;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ServerApp extends Application implements ServerObserver {

    private int port = 50000;
    private DataServer server;
    private Button trainButton;
    private ProgressBar progressBar = new ProgressBar(0.0);
    private Label progressLabel;
    private VBox schoolsListLabel;
    VBox layout;

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            java.rmi.registry.LocateRegistry.createRegistry(port);
            server = new DataServer();
            server.addObserver(this);
            java.rmi.Naming.rebind("rmi://localhost:" + port + "/DataServer", server);
            System.out.println("Server started...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        Image image = new Image("ESI.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Set the desired width
        imageView.setFitHeight(70); // Set the desired height

        // Create an HBox to hold the image
        HBox logoBox = new HBox(imageView);
        logoBox.setAlignment(Pos.TOP_CENTER); // Align to top left corner

        HBox.setMargin(logoBox, new Insets(10, 10, 10, 10));

        // Create a layout for the CSV input window
        layout = new VBox();
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setSpacing(20); // Increased spacing for better readability
        layout.setBackground(
                new Background(new BackgroundFill(Color.rgb(229, 225, 214, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setPadding(new Insets(50, 20, 0, 20)); // Add padding of 20 pixels to the left and right sides

        // Add the image box to the layout
        layout.getChildren().add(logoBox);

        trainButton = new Button("Train Now");
        trainButton.setDisable(true);
        trainButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        trainButton.setMinWidth(100);
        trainButton.setMinHeight(40);
        trainButton.setCursor(Cursor.HAND); // Set cursor type to hand when hovering over the button

        // Add click animation
        trainButton.setOnMousePressed(e -> {
            ScaleTransition animation = new ScaleTransition(Duration.seconds(0.1), trainButton);
            animation.setToX(0.9);
            animation.setToY(0.9);
            animation.play();
        });

        trainButton.setOnMouseReleased(e -> {
            ScaleTransition animation = new ScaleTransition(Duration.seconds(0.1), trainButton);
            animation.setToX(1.0);
            animation.setToY(1.0);
            animation.play();
        });

        trainButton.setOnAction(e -> {
            try {
                String response = server.train();
                System.out.println(response);
                if (response.equals("model trained")) {
                    Label finished = new Label("Model trained successfully");
                    finished.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    layout.getChildren().add(finished);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create a label for instructions
        Label instructionsLabel = new Label(
                "This application is used to supervise server side for students' grades in the Bac exam.\n\t\tBefore using the application, please upload grades:");
        instructionsLabel.setStyle("-fx-font-size: 16px; ");
        instructionsLabel.setAlignment(Pos.CENTER); // Align instructions to center
        VBox.setMargin(instructionsLabel, new Insets(0, 0, 20, 0)); // Add bottom margin to separate from the button

        progressBar.setMinWidth(400);
        progressBar.setStyle("-fx-accent: #4CAF50; -fx-control-inner-background: #ffffff;");

        progressLabel = new Label();
        progressLabel.setStyle("-fx-font-size: 16px;");
        progressLabel.setText(String.format("Progress: %.0f%%", 0.0));
        // Add components to the layout

        schoolsListLabel = new VBox();
        schoolsListLabel.setAlignment(Pos.CENTER);
        schoolsListLabel.setSpacing(10);
        schoolsListLabel.getChildren().add(new Label("Schools list that submitted data:"));
        layout.getChildren().addAll(instructionsLabel, trainButton, progressLabel, progressBar, schoolsListLabel);

        // Create scene for CSV input window
        Scene csvInputScene = new Scene(layout, 700, 500);

        // Set the scene and show the stage
        primaryStage.setTitle("Bac");
        primaryStage.setScene(csvInputScene);
        primaryStage.show();

    }

    @Override
    public void updateProgressBar(double progress, String school) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            progressBar.setProgress(progress);
            progressLabel.setText(String.format("Progress: %.0f%%", progress * 100));
            if (progress == 1.0) {
                trainButton.setDisable(false);
            }
            schoolsListLabel.getChildren().add(new Label(school));

        }));
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}