package com.htwsaar.container;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores Group information
 *
 * @author Matthias Gessner
 * @version 1.0
 */
public class Group
{
    private int id;
    private String name;
    private List<User> members;
    private int creator;

    public Group()
    {
    }

    public Group(int groupID, String name, int creator)
    {
        this.id = groupID;
        this.name = name;
        this.creator = creator;
    }

    public String getName()
    {
        return name;
    }

    public int getCreator()
    {
        return creator;
    }

    public List<User> getMembers()
    {
        return members;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCreator(int id)
    {
        this.creator = id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getID()
    {
        return id;
    }

    public void setMembers(ArrayList<User> members)
    {
        this.members = members;
    }
}
