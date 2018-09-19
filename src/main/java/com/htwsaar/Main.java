package com.htwsaar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Coordinator coordinator = new Coordinator();
        coordinator.start(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
