package org.iit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JFXGTDS extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use your actual package name here
        FXMLLoader loader = new  FXMLLoader(JFXGTDS.class.getResource("main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Government Tax Department System");
        primaryStage.setScene(new Scene(root, 850, 575));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}