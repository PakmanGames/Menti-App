package com.example.menti;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

   @FXML
   public Pane activitiesPane;
   public Pane sleepPane;
   public Pane meditationPane;
   public Pane check_list;

   @FXML
   public LineChart LineChart;
   public BarChart BarChart;

   @FXML
   public CategoryAxis x;

   @FXML
   public NumberAxis y;

   @FXML
   public void initialize(URL url, ResourceBundle rb) {
      XYChart.Series series = new XYChart.Series();
      series.setName("Data");

      series.getData().add(new XYChart.Data("M", 24));
      series.getData().add(new XYChart.Data("T", 50));
      series.getData().add(new XYChart.Data("W", 10));
      series.getData().add(new XYChart.Data("T", 20));
      series.getData().add(new XYChart.Data("F", 1));

      LineChart.getData().addAll(series);
      BarChart.getData().addAll(series);
   }

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
   public void addList(ActionEvent e) {
      CheckBox checkBox = new CheckBox("New Checkbox");
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
   }

   private double calculateYPosition(int index) {
      double spacing = 25.0;
      return Math.max(index * spacing, 0);
   }

   @FXML
   public void startButton(ActionEvent event) {
      Stage stage = new Stage();

      MeditationGUI meditationGUI = new MeditationGUI();

      try {
         meditationGUI.start(stage);
      } catch (Exception e) {
         System.out.println("Error");
      }
   }
}
