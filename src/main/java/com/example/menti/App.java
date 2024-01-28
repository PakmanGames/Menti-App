package com.example.menti;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml")); //Load fxml file from scenebuilder
        Scene scene = new Scene(fxmlLoader.load(), 600, 400); //APP size
        stage.setTitle("Menti");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show(); //Display
    }

    public static void main(String[] args) {
        launch(); //Run
    }
}