package com.htwsaar.logic;

import com.htwsaar.container.Message;
import com.htwsaar.sql.SQLHandler;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides methods to interact with the database table ClientMessage.
 *
 * @author Matthias Gessner
 * @version 1.2
 */
public class MessageLogic
{
    public final static MessageLogic INSTANCE = new MessageLogic();

    /**
     * Private constructor (singleton pattern)
     * called by MessageLogic INSTANCE
     */
    private MessageLogic()
    {
    }

    /**
     * Loads ClientMessage information from database table ClientMessage in a ClientMessage class object.
     *
     * @param messageID Identifier (primary key)
     * @return ClientMessage class object if ID exists in table User otherwise null
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public Message selectMessage(int messageID) throws SQLException
    {
        return SQLHandler
                .sql("SELECT * FROM Messages WHERE ID = ?;", messageID)
                .executeQuery(rs -> !rs.next() ? null :
                        new Message(rs.getInt("ID"),
                                rs.getInt("Origin"),
                                rs.getInt("Destination"),
                                rs.getString("Message"),
                                rs.getTimestamp("Timestamp"),
                                rs.getInt("messageType")));
    }

    /**
     * Load all Messages from a specific group from the database table ClientMessage.
     *
     * @param groupID Group Identifier (foreign key)
     * @return ArrayList of Messages
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public ArrayList<Message> getMessageFromGroup(int groupID)
    {
        ArrayList<Message> tmp = new ArrayList<>();

        return SQLHandler
                .sql("SELECT * FROM Messages WHERE destination = ? AND messageType = 1;", groupID)
                .executeQuery(rs ->
                {
                    while (rs.next())
                    {
                        tmp.add(new Message(rs.getInt("ID"),
                                rs.getInt("Origin"),
                                rs.getInt("Destination"),
                                rs.getString("Message"),
                                rs.getTimestamp("Timestamp"),
                                rs.getInt("messageType")));
                    }
                    return tmp;
                });
    }

    /**
     * Load all Messages from a specific User from the database table ClientMessage.
     *
     * @param userID User identifier (primary key)
     * @return ArrayList of Messages
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public ArrayList<Message> getMessagesFromUser(int userID)
    {
        ArrayList<Message> tmp = new ArrayList<>();

        return SQLHandler
                .sql("SELECT * FROM Messages WHERE (origin = ? OR destination = ?) AND messageType = 0;", userID, userID)
                .executeQuery(rs ->
                {
                    while (rs.next())
                    {
                        tmp.add(new Message(rs.getInt("ID"),
                                rs.getInt("Origin"),
                                rs.getInt("Destination"),
                                rs.getString("Message"),
                                rs.getTimestamp("Timestamp"),
                                rs.getInt("messageType")));
                    }
                    return tmp;
                });
    }

    /**
     * Insert a new entry in database table ClientMessage.
     *
     * @param message ClientMessage object
     * @return ID of inserted row if successful otherwise -1
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public long insertMessage(Message message) throws SQLException
    {
        return SQLHandler
                .sql("INSERT INTO Messages (Origin, Destination, Message,Timestamp," +
                                " MessageType) " +
                                "VALUES (?, ?, ?, ?, ?); ",
                        message.getOrigin(),
                        message.getDestination(),
                        message.getMessage(),
                        message.getTimestamp(),
                        message.getMessageType())
                .execute(rs -> rs.next() ? rs.getLong(1) : -1L);
    }

    /**
     * Deletes an ClientMessage object from database table ClientMessage.
     *
     * @param messageID identifier (primary key) of table entry that want to be deleted
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteMessage(int messageID) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Messages WHERE ID = ?;", messageID)
                .execute();
    }

    /**
     * Deletes all Messages from a specific group from database table ClientMessage.
     *
     * @param chatID identifier (foreign key) of the group that want to be cleared
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteChatHistory(int chatID) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Messages WHERE Chat_ID = ?;", chatID)
                .execute();
    }

    //TODO Was macht Status denn nun? Wenn das jemand weiß, bitte Kommentar anpassen

    /**
     * Updates the status flag of a message entry from database table ClientMessage.
     *
     * @param messageID identifier (primary key) of a message entry.
     * @param status
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean updateMessageType(int messageID, int status) throws SQLException
    {
        return SQLHandler
                .sql("UPDATE Messages SET status = ? WHERE ID = ?;", status, messageID)
                .execute();
    }
}
