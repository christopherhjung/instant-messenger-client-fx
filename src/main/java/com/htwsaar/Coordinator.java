package com.htwsaar;

import com.htwsaar.container.Group;
import com.htwsaar.container.Message;
import com.htwsaar.container.User;
import com.htwsaar.controller.ChatController;
import com.htwsaar.controller.CoordinatorController;
import com.htwsaar.logic.GroupLogic;
import com.htwsaar.logic.MessageLogic;
import com.htwsaar.logic.UserLogic;
import com.htwsaar.server.ServerConnection;
import com.htwsaar.sql.SQLConnection;
import com.htwsaar.utils.Resources;
import com.htwsaar.utils.Run;
import com.htwsaar.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Coordinator
{
    private static int INIT_WIDTH = 1200;
    private static int INIT_HEIGHT = 800;

    private static int LOGIN = 0;
    private static int CHAT = 1;

    private int currentDestination = -1;
    private int currentMessageType = -1;

    public Stage stage;
    private CoordinatorController currentController;

    private int page = -1;

    public void start(Stage stage)
    {
        this.stage = stage;

        Parent parent = load("login.fxml");

        if (parent != null)
        {
            stage.setTitle("Wave Messenger");
            stage.setScene(new Scene(parent, INIT_WIDTH, INIT_HEIGHT));
            stage.show();
            page = LOGIN;
        }
    }

    private Parent load(String name)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(Resources.get(name));
            Parent root = loader.load();

            currentController = loader.getController();
            currentController.setCoordinator(this);

            return root;

        } catch (Exception e)
        {
            return null;
        }
    }

    public void login(String name, String password)
    {
        try
        {
            ServerConnection.INSTANCE.connect(name, password);
            SQLConnection.INSTANCE.connect(name);

            ServerConnection.INSTANCE.getUserFromName(name, user -> {
                User.ME = user;
                switchToChatWindow(this::init);
            });
        } catch (Exception e)
        {
            logout();
        }
    }

    public void create(String user, String password)
    {
        User created = ServerConnection.INSTANCE.createUser(user, password);
        if (created != null)
        {
            login(created.getName(), password);
        }
    }

    public void logout()
    {
        ServerConnection.INSTANCE.close();
        SQLConnection.INSTANCE.close();
        switchToLoginWindow();
    }

    void switchToLoginWindow()
    {
        SceneSwitcher.switchScene(stage.getScene().getRoot(), load("login.fxml"), event -> {
            page = LOGIN;
        });
    }

    void switchToChatWindow(Runnable runnable)
    {
        SceneSwitcher.switchScene(stage.getScene().getRoot(), load("chat.fxml"), event -> {
            page = CHAT;
            runnable.run();
        });
    }

    void init()
    {
        ServerConnection.INSTANCE.subscribeMessage(this::onNewMessages);
        ServerConnection.INSTANCE.subscribeMessageConfirmation(message -> onNewMessages(new Message[]{message}));
        ServerConnection.INSTANCE.subscribeGroups(group -> checkGroup(group.getID()));

        updateRecipients();
    }

    public void updateRecipients()
    {
        ChatController controller = (ChatController) currentController;

        controller.setUserName(User.ME.getName());

        List<User> friends = Run.safe(UserLogic.INSTANCE::selectAllUsers);
        List<Group> groups = Run.safe(GroupLogic.INSTANCE::selectAllGroups);

        List<Object> recipients = new ArrayList<>();
        recipients.addAll(friends);
        recipients.addAll(groups);
        Platform.runLater(() -> controller.setRecipients(recipients));
    }

    public void send(Message message)
    {
        ServerConnection.INSTANCE.sendMessage(message);
    }

    public void selectRecipient(int destination, int messageType)
    {
        this.currentDestination = destination;
        this.currentMessageType = messageType;

        List<Message> messages;
        if (messageType == 0)
        {
            messages = MessageLogic.INSTANCE.getMessagesFromUser(destination);
        }
        else if (messageType == 1)
        {
            messages = MessageLogic.INSTANCE.getMessageFromGroup(destination);
        }
        else
        {
            return;
        }

        ChatController controller = (ChatController) currentController;
        controller.setMessages(messages);
    }

    public void onNewMessages(Message[] messages)
    {
        ChatController controller = (ChatController) currentController;

        for (Message message : messages)
        {
            try
            {
                MessageLogic.INSTANCE.selectMessage(message.getId());
                MessageLogic.INSTANCE.insertMessage(message);

                checkUser(message.getOrigin());
                if (message.getMessageType() == 1)
                {
                    checkGroup(message.getDestination());
                }

                if ((message.getOrigin() == User.ME.getID() || message.getOrigin() == currentDestination || message.getDestination() == currentDestination) && message.getMessageType() == currentMessageType)
                {
                    Platform.runLater(() -> controller.addMessage(message));
                }
                ServerConnection.INSTANCE.sendConfirmation(message.getId());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addRecipient(String name)
    {
        getUser(name, user -> {
            if (user != null)
            {
                updateRecipients();
            }
        });
    }

    public void createGroup(String name)
    {
        ServerConnection.INSTANCE.createGroup(name, group -> {
            GroupLogic.INSTANCE.insertGroup(group.getID(), group.getCreator(), group.getName());
            updateRecipients();
        });
    }

    public void addMember(String name)
    {
        if (currentMessageType == 0)
        {
            getGroup(name, group -> {
                if (group != null)
                {
                    ServerConnection.INSTANCE.addUserToGroup(currentDestination, group.getID());
                }
            });
        }
        else if (currentMessageType == 1)
        {
            getUser(name, user -> {
                if (user != null)
                {
                    ServerConnection.INSTANCE.addUserToGroup(user.getID(), currentDestination);
                }
            });
        }

    }

    private void checkUser(int userID)
    {
        User user = UserLogic.INSTANCE.selectUser(userID);

        if (user == null && userID != User.ME.getID())
        {
            ServerConnection.INSTANCE.getUserFromID(userID, fetchedUser -> {
                UserLogic.INSTANCE.insertUser(fetchedUser.getID(), fetchedUser.getName());
                updateRecipients();
            });
        }
    }

    private void getUser(int userID, Consumer<User> callback)
    {
        User user = UserLogic.INSTANCE.selectUser(userID);

        if (user == null)
        {
            ServerConnection.INSTANCE.getUserFromID(userID, fetchedUser -> {
                UserLogic.INSTANCE.insertUser(fetchedUser.getID(), fetchedUser.getName());
                callback.accept(fetchedUser);
            });
        }
        else
        {
            callback.accept(user);
        }
    }

    private void getUser(String name, Consumer<User> callback)
    {
        User user = UserLogic.INSTANCE.selectUser(name);

        if (user == null)
        {
            ServerConnection.INSTANCE.getUserFromName(name, fetchedUser -> {
                UserLogic.INSTANCE.insertUser(fetchedUser.getID(), fetchedUser.getName());
                callback.accept(fetchedUser);
            });
        }
        else
        {
            callback.accept(user);
        }
    }

    private void checkGroup(int groupID)
    {
        Group group = GroupLogic.INSTANCE.selectGroup(groupID);

        if (group == null)
        {
            ServerConnection.INSTANCE.getGroupByID(groupID, fetchedGroup -> {
                GroupLogic.INSTANCE.insertGroup(groupID, fetchedGroup.getCreator(), fetchedGroup.getName());
                updateRecipients();
            });
        }
    }

    private void getGroup(int groupID, Consumer<Group> callback)
    {
        Group group = GroupLogic.INSTANCE.selectGroup(groupID);

        if (group == null)
        {
            ServerConnection.INSTANCE.getGroupByID(groupID, fetchedGroup -> {
                GroupLogic.INSTANCE.insertGroup(groupID, fetchedGroup.getCreator(), fetchedGroup.getName());
                callback.accept(fetchedGroup);
            });
        }
        else
        {
            callback.accept(group);
        }
    }

    private void getGroup(String name, Consumer<Group> callback)
    {
        Group group = GroupLogic.INSTANCE.selectGroup(name);

        if (group == null)
        {
            ServerConnection.INSTANCE.getGroupByName(name, fetchedGroup -> {
                GroupLogic.INSTANCE.insertGroup(fetchedGroup.getID(), fetchedGroup.getCreator(), fetchedGroup.getName());
                callback.accept(fetchedGroup);
            });
        }
        else
        {
            callback.accept(group);
        }
    }

    private static class Worker extends Thread
    {
        @Override
        public void run()
        {

        }
    }
}
