package com.htwsaar;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneSwitcher
{
    public static void switchScene(Node oldParent, Parent newScene, EventHandler<ActionEvent> value){
        FadeTransition transition = new FadeTransition();
        transition.setNode(oldParent);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setDuration(Duration.millis(1000));

        transition.setOnFinished(event -> {
            value.handle(event);
            Scene oldScene = oldParent.getScene();
            Stage stage = (Stage) oldScene.getWindow();

            newScene.setOpacity(0);

            FadeTransition transition2 = new FadeTransition();
            transition2.setNode(newScene);
            transition2.setFromValue(0);
            transition2.setToValue(1);
            transition2.setDuration(Duration.millis(1000));
            transition2.play();

            stage.setScene(new Scene(newScene,oldScene.getWidth(),oldScene.getHeight()));
            stage.show();


        });
        transition.play();
    }
}
