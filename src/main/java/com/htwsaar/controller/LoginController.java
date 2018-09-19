package com.htwsaar.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController extends CoordinatorController
{
    @FXML
    Node container;

    @FXML
    Button login;

    @FXML
    TextField name;

    @FXML
    TextField password;

    public void initialize()
    {
        EventHandler<KeyEvent> onEnter = event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                getCoordinator().login(name.getText(), password.getText());
            }
        };

        login.setOnKeyPressed(onEnter);
        password.setOnKeyPressed(onEnter);

        login.setOnMouseClicked(event -> {
            getCoordinator().login(name.getText(), password.getText());
        });
    }
}
