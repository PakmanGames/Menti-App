package com.example.menti;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.jfoenix.controls.JFXTextArea;

public class Controller {
   private List<String> checklistItems = new ArrayList<>();

   @FXML
   public Pane activitiesPane;
   public Pane sleepPane;
   public Pane meditationPane;
   public Pane check_list;
   @FXML
   public JFXTextArea suggestions;

   @FXML
   public void activityButton(ActionEvent e) {
      activitiesPane.setVisible(true);
      sleepPane.setVisible(false);
      meditationPane.setVisible(false);
   }

   @FXML
   public void sleepButton(ActionEvent e) {
      activitiesPane.setVisible(false);
      sleepPane.setVisible(true);
      meditationPane.setVisible(false);
   }

   @FXML
   public void meditationButton(ActionEvent e) {
      activitiesPane.setVisible(false);
      sleepPane.setVisible(false);
      meditationPane.setVisible(true);
   }
   @FXML
   private void addList(ActionEvent e) {
      // Create dialog box for user to enter info
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Add a new habit to the list");
      dialog.setHeaderText("What do you want to accomplish today?");
      dialog.setContentText("Goal:");

      // Show the dialog and wait for the user's response
      dialog.showAndWait().ifPresent(text -> {
         CheckBox checkBox = new CheckBox(text);
         checkBox.setMaxSize(150, 20);
         checkBox.setLayoutX(25);

         checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (!isSelected) {
               Parent parent = checkBox.getParent();
               if (parent instanceof Pane) {
                  int removedIndex = ((Pane) parent).getChildren().indexOf(checkBox);
                  ((Pane) parent).getChildren().remove(checkBox);
                  for (int i = removedIndex; i < ((Pane) parent).getChildren().size(); i++) {
                     Node node = ((Pane) parent).getChildren().get(i);
                     if (node instanceof CheckBox) {
                        double newYPosition = calculateYPosition(i);
                        ((CheckBox) node).setLayoutY(newYPosition);
                     }
                  }
               }
            }
         });

         double newYPosition = calculateYPosition(check_list.getChildren().size());
         checkBox.setLayoutY(newYPosition);
         check_list.getChildren().add(checkBox);

         // Add the entered text to the list
         checklistItems.add(text);
      });
      // fetch();
      suggestions.appendText("testing placeholder to make sure it works");
   }

   private double calculateYPosition(int index) {
      double spacing = 25.0;
      return Math.max(index * spacing, 0);
   }

   public String[] getChecklistItemsArray() {
      return checklistItems.toArray(new String[0]);
   }

   @FXML
   public void startButton(ActionEvent event) {
      Stage stage = new Stage();

      ScrollBack scrollBack = new ScrollBack();

      try {
         scrollBack.start(stage);
      } catch (Exception e) {
         System.out.println("Error");
      }
   }
   @FXML
   private void fetch() {
      // The input prompt that will be sent to the Spring Boot application
      String prompt = "You are a suggestions assistant that will take data from a mental health habit tracker app and provide feedback as well as suggestions to improve overall well being and mental health.\n" +
              "Give the suggestions in point form, one paragraph for encouragement of the activities listed and another for areas of improvement and suggestions. Limit your response in each point to be a maximum of 30 words. \n" +
              "Base your response on these following habbits: ";

      String[] habits = getChecklistItemsArray();

      try {
         // Endpoint URL
         URL url = new URL("http://localhost:8080/hitOpenaiApi");
         // Creates connection to URL
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         // Sets the request method to be POST
         connection.setRequestMethod("POST");
         // Data will be sent as JSON
         connection.setRequestProperty("Content-Type", "application/json");
         // Allows data to be sent through connection
         connection.setDoOutput(true);

         // Converts prompt into a JSON string
         String jsonInputString = "{\"prompt\": \"" + prompt + "\"" + arrayToJsonString(habits) + "}";

         // Sends data to the backend application
         try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
         }

         // Get the response code and print it
         int responseCode = connection.getResponseCode();
         System.out.println("Response Code: " + responseCode);

         // Read the response body
         if (responseCode == HttpURLConnection.HTTP_OK) { // If response code is ok (200)
            try (InputStream is = connection.getInputStream()) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
               // Collects the response and puts it together as string and prints it
               String response = reader.lines().collect(Collectors.joining("\n"));
               System.out.println("Response Body: " + response);
            }
         } else {
            // If response code is not ok
            try (InputStream errorStream = connection.getErrorStream()) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8));
               // Collects the error response and puts it together as string and prints it
               String errorResponse = reader.lines().collect(Collectors.joining("\n"));
               System.out.println("Error Response: " + errorResponse);
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static String arrayToJsonString(String[] array) {
      StringBuilder jsonString = new StringBuilder("[");
      for (int i = 0; i < array.length; i++) {
         jsonString.append("\"").append(array[i]).append("\"");
         if (i < array.length - 1) {
            jsonString.append(",");
         }
      }
      jsonString.append("]");
      return jsonString.toString();
   }
}
