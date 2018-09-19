package com.htwsaar.controller;

import com.htwsaar.container.Group;
import com.htwsaar.container.Message;
import com.htwsaar.container.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.sql.Timestamp;
import java.util.List;


public class ChatController extends CoordinatorController
{
    @FXML
    private ListView<Object> recipients;

    @FXML
    private ListView<Message> chat;

    @FXML
    private Button logout;

    @FXML
    private Button send;

    @FXML
    private TextField message;

    @FXML
    private TextField search;

    @FXML
    private Button addRecipient;

    @FXML
    private Button createGroup;

    @FXML
    private Button addMember;

    @FXML
    private Label userName;

    private ObservableList<Message> items;

    private int messageType = 0;
    private int destination = 0;

    private Message generateMessage()
    {
        return new Message(-1, User.ME.getID(), destination, this.message.getText(), new Timestamp(System.currentTimeMillis()), messageType);
    }

    public void initialize()
    {
        send.setOnMouseClicked(event -> {
            getCoordinator().send(generateMessage());
            message.clear();
        });

        addRecipient.setOnMouseClicked(event -> getCoordinator().addRecipient(search.getText()));
        createGroup.setOnMouseClicked(event -> getCoordinator().createGroup(search.getText()));
        addMember.setOnMouseClicked(event -> getCoordinator().addMember(search.getText()));

        logout.setOnMouseClicked(event -> getCoordinator().logout());
        items = FXCollections.observableArrayList();
        chat.setItems(items);
        chat.setCellFactory(param -> new ChatItem());

        message.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                getCoordinator().send(generateMessage());
                message.clear();
            }
        });

        recipients.setCellFactory(param -> new RecipientItem());
        recipients.setOnMouseClicked(event -> {
            Object object = recipients.getSelectionModel().getSelectedItem();

            if (object instanceof Group)
            {
                messageType = 1;
                destination = ((Group) object).getID();
            }
            else if (object instanceof User)
            {
                messageType = 0;
                destination = ((User) object).getID();
            }

            getCoordinator().selectRecipient(destination, messageType);
        });
    }

    public void setUserName(String userName){
        this.userName.setText(userName);
    }

    public void addMessage(Message message)
    {
        items.add(message);
    }

    public void setMessages(List<Message> messages)
    {
        items.clear();
        items.addAll(messages);
    }

    public void setRecipients(List<Object> recipientList)
    {
        ObservableList<Object> observableList = FXCollections.observableArrayList();
        observableList.clear();
        observableList.addAll(recipientList);
        recipients.setItems(observableList);
    }
}
