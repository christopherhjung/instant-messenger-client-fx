package com.htwsaar;

import com.htwsaar.container.Message;
import com.htwsaar.container.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatItem extends ListCell<Message>
{
    @FXML
    private Label messageText;

    @FXML
    private VBox message;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Message message, boolean empty)
    {
        super.updateItem(message, empty);

        if (empty || message == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            if (mLLoader == null)
            {
                mLLoader = new FXMLLoader(Resources.get("chatMessage.fxml"));
                mLLoader.setController(this);

                try
                {
                    mLLoader.load();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

            this.messageText.setText(message.getMessage());

            Pos pos;
            if (message.getOrigin() == User.ME.getID())
            {
                pos = Pos.CENTER_RIGHT;
            }
            else
            {
                pos = Pos.CENTER_LEFT;
            }

            this.message.alignmentProperty().setValue(pos);

            setText(null);
            setGraphic(this.message);
        }

    }
}
