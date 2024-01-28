package com.example.menti;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.*;

public class MeditationGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a Swing-based GUI instance
        ScrollBack swingGUI = new ScrollBack();

        // Create a SwingNode to embed the Swing content in JavaFX
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(swingGUI);

        BorderPane root = new BorderPane(swingNode);
        Scene scene = new Scene(root, 1400, 700);

        primaryStage.setTitle("Meditation Space");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
