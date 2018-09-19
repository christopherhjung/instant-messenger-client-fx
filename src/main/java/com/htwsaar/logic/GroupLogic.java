package com.htwsaar.logic;


import com.htwsaar.container.Group;
import com.htwsaar.container.User;
import com.htwsaar.sql.SQLHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides methods to interact with the database table Group.
 *
 * @author Matthias Gessner
 * @version 1.2
 */
public class GroupLogic
{
    public final static GroupLogic INSTANCE = new GroupLogic();

    /**
     * Private constructor (singleton pattern)
     * called by GroupLogic INSTANCE
     */
    private GroupLogic()
    {

    }

    /**
     * Checks if an user is in a group
     *
     * @param destination name of the group
     * @param userID      ID of the user to be checked
     * @return True if the user is in that group otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean isGroup(String destination, int userID) throws SQLException
    {
        return SQLHandler
                .sql("SELECT Name FROM Groups " +
                                "JOIN Member WHERE ID = Chat_ID AND User_ID = ? AND Name = ?;",
                        userID, destination)
                .executeQuery(rs -> rs.next());
    }

    /**
     * Loads Group information from database table Groups in an group class object.
     *
     * @param ID Identifier (primary key)
     * @return Group class object if ID exists in table Groups otherwise null
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public Group selectGroup(int ID)
    {
        return SQLHandler
                .sql("SELECT ID, Name, Creator FROM Groups WHERE ID = ?;", ID)
                .executeQuery(rs -> !rs.next() ? null :
                        new Group(rs.getInt("ID"),
                                rs.getString("Name"),
                                rs.getInt("Creator")));
    }


    /**
     * Loads Group information from database table Groups in an group class object.
     *
     * @param name Identifier (unique column)
     * @return Group class object if name exists in table Group otherwise null
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    //FIXME @OPTIONAL (very low priority) can we fix this so group names can be reusable?
    public Group selectGroup(String name)
    {
        return SQLHandler
                .sql("SELECT ID, Name, Creator FROM Groups WHERE name = ?;", name)
                .executeQuery(rs -> !rs.next() ? null :
                        new Group(rs.getInt("ID"),
                                rs.getString("Name"),
                                rs.getInt("Creator")));
    }

    /**
     * Loads all User from a specific Group.
     *
     * @param chatID Group Identifier
     * @return ArrayList of User objects
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public ArrayList<User> selectUserFromGroup(int chatID) throws SQLException
    {
        ArrayList<User> tmp = new ArrayList<>();
        return SQLHandler
                .sql("SELECT User.ID, User.Name FROM User JOIN Member WHERE ID = ? AND User_ID = ID;",
                        chatID)
                .executeQuery(rs ->
                {
                    while (rs.next())
                    {
                        tmp.add(new User(rs.getInt("ID"), rs.getString("Name")));
                    }
                    return tmp;
                });
    }

    /**
     * Loads all Groups of a specific User.
     *
     * @return ArrayList of group objects
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public ArrayList<Group> selectAllGroups()
    {
        ArrayList<Group> tmp = new ArrayList<>();
        return SQLHandler
                .sql("SELECT * FROM Groups ")
                .executeQuery(rs ->
                {
                    while (rs.next())
                    {
                        tmp.add(new Group(rs.getInt("ID"),
                                rs.getString("Name"),
                                rs.getInt("Creator")));
                    }
                    return tmp;
                });
    }

    /**
     * Inserts a new row in the Groups table.
     *
     * @param creatorID User ID of creator
     * @param name      Name of the group
     * @return groupID if successfully inserted, otherwise -1
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean insertGroup(int groupID, int creatorID, String name)
    {
        return SQLHandler
                .sql("INSERT INTO Groups (Id, Name, Creator) VALUES (?, ?, ?);",
                        groupID, name, creatorID)
                .execute();
    }

    /**
     * Deletes a Group from database table Groups.
     *
     * @param chatID ID (primary key) of the Group that want to be deleted.
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteGroup(int chatID) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Groups WHERE ID = ?;", chatID)
                .execute();
    }

    /**
     * Updates the creator column of a Group table entry.
     *
     * @param chatID       ID (primary key) of the Group that want to be changed.
     * @param newCreatorID User ID of the new creator
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean updateGroupCreator(int chatID, int newCreatorID) throws SQLException
    {
        return SQLHandler
                .sql("UPDATE CHAT SET Creator = ? WHERE ID = ?;", newCreatorID, chatID)
                .execute();
    }

    /**
     * Updates the name column of a Group table entry.
     *
     * @param chatID ID (primary key) of the Group that want to be changed.
     * @param name   new name
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error, duplicate entry
     */
    public boolean updateGroupName(int chatID, String name) throws SQLException
    {
        return SQLHandler
                .sql("UPDATE Groups SET Name = ? WHERE ID = ?;", name, chatID)
                .execute();
    }

    /**
     * Adds new User to a Group.
     * Inserts a new relationship between a group and an user in Member table.
     *
     * @param chatID chatID
     * @param userID new userID in Group
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error, duplicate entry
     */
    public boolean insertMember(int chatID, int userID) throws SQLException
    {
        return SQLHandler
                .sql("INSERT INTO Member (Chat_ID, User_ID) VALUES (?,?);", chatID, userID)
                .execute();
    }

    /**
     * Deletes an User from Group.
     * Deletes the relationship between a group and an user in Member table.
     *
     * @param chatID chatID
     * @param userID new userID in Group
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteMember(int chatID, int userID) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Member WHERE chat_ID = ? AND user_ID = ?;", chatID, userID)
                .execute();
    }

    /**
     * Checks if the group with the passed ID exists.
     *
     * @param id ID of the group
     * @return true if the group exists otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean groupExists(int id) throws SQLException
    {
        return SQLHandler
                .sql("SELECT * FROM Groups WHERE ID = ?", id)
                .executeQuery(rs -> rs.next());
    }
}
