package com.htwsaar;

import com.htwsaar.container.Group;
import com.htwsaar.container.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class RecipientItem extends ListCell<Object>
{
    @FXML
    private Node container;

    @FXML
    private Label name;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Object recipient, boolean empty)
    {
        super.updateItem(recipient, empty);

        if (empty || recipient == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            if (mLLoader == null)
            {
                mLLoader = new FXMLLoader(Resources.get("recipient.fxml"));
                mLLoader.setController(this);

                try
                {
                    mLLoader.load();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

            if (recipient instanceof User)
            {
                User user = (User) recipient;

                this.name.setText(user.getName());
            }
            else if (recipient instanceof Group)
            {
                Group user = (Group) recipient;

                this.name.setText(user.getName());
            }

            setText(null);
            setGraphic(this.container);
        }

    }
}
