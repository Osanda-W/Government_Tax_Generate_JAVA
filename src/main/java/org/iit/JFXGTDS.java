package org.iit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Government Tax Department System.
 * This class sets up the JavaFX application and loads the main FXML file.
 */

public class JFXGTDS extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file that defines the user interface
        FXMLLoader loader = new  FXMLLoader(JFXGTDS.class.getResource("main.fxml"));
        Parent root = loader.load();

        // Set up the primary stage
        primaryStage.setTitle("Government Tax Department System");
        primaryStage.setScene(new Scene(root, 850, 575));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    } // Launch the JavaFX application
}