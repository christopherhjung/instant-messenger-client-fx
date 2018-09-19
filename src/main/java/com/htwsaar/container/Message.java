package com.htwsaar.container;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Stores Message information
 *
 * @author Matthias Gessner
 * @version 1.0
 */
public class Message
{
    private int id;
    private int origin;
    private int destination;
    private String message;
    private Timestamp timestamp;
    /**
     * according to server:
     * USER_MESSAGE = 0;
     * GROUP_MESSAGE = 1;
     */
    private int messageType;

    public Message()
    {
    }

    public int getMessageType()
    {
        return messageType;
    }

    public void setMessageType(int messageType)
    {
        this.messageType = messageType;
    }

    public Message(int id, int origin, int destination, String message, Timestamp timestamp, int messageType)
    {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.message = message;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public int getId()
    {
        return id;
    }

    public int getOrigin()
    {
        return origin;
    }

    public int getDestination()
    {
        return destination;
    }

    public String getMessage()
    {
        return message;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    /**
     * Converts a timestamp to a human readable string
     *
     * @param formatPattern DateTime format
     * @return DateTime string if converting was successful otherwise null
     */
    public String getTime(String formatPattern)
    {
        Date tmp = new Date(timestamp.getTime());
        try
        {
            return new SimpleDateFormat(formatPattern).format(tmp);
        } catch (Exception e)
        {
            return null;
        }
    }


    public String toString()
    {
        return new StringBuilder("ID: " + id + " O:" + origin
                + " D:" + destination + " T:" + timestamp + " M:" + message + " isGrpM:" + messageType).toString();
    }
}
