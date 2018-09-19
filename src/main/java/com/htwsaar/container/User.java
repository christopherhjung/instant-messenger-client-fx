package com.htwsaar.container;

/**
 * Stores User information
 *
 * @author Matthias Gessner & Friedemann Lipphardt
 * @version 2.0
 */
public class User
{
    private int id;
    private String name;
    private String password;

    public static User ME;

    public User()
    {
    }

    /**
     * only for login
     *
     * @param name
     * @param password
     */
    public User(String name, String password)
    {
        this.name = name;
        this.password = password;
    }

    /**
     * Used after logged in
     *
     * @param id
     * @param name
     */
    public User(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
