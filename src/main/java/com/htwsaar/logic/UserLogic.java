package com.htwsaar.logic;


import com.htwsaar.container.User;
import com.htwsaar.sql.SQLHandler;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Provides methods to interact with the database table User.
 *
 * @author Matthias Gessner
 * @version 1.2
 */
public class UserLogic
{
    public final static UserLogic INSTANCE = new UserLogic();

    /**
     * Private constructor (singleton pattern)
     * called by UserLogic INSTANCE
     */
    private UserLogic()
    {

    }

    /**
     * Loads User information from database table User in an user class object.
     *
     * @param ID Identifier (primary key)
     * @return User class object if ID exists in table User otherwise null
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public User selectUser(int ID)
    {
        return SQLHandler
                .sql("SELECT ID, Name FROM Users WHERE ID = ?;", ID)
                .executeQuery(rs ->
                {
                    if (!rs.next())
                    {
                        return null;
                    }
                    return new User(rs.getInt("ID"), rs.getString("Name"));
                });
    }

    /**
     * Loads User information from database table User in an user class object.
     *
     * @param name name (unique column)
     * @return User class object if name exists in table User otherwise null
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public User selectUser(String name)
    {
        return SQLHandler
                .sql("SELECT ID, Name FROM Users WHERE Name = ?;", name)
                .executeQuery(rs ->
                {
                    if (!rs.next())
                    {
                        return null;
                    }

                    return new User(rs.getInt("ID"), rs.getString("Name"));
                });
    }

    /**
     * Updates an User entry in database table User.
     *
     * @param ID   Identifier (Primary key) of table entry that want to be changed
     * @param name Name new Name of User
     * @return true if update was successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error, user name already exists
     */
    public boolean updateUser(int ID, String name) throws SQLException
    {
        return SQLHandler
                .sql("UPDATE Users SET Name = ? WHERE ID = ?;", name, ID)
                .execute();
    }

    /**
     * Deletes an User object from database table User.
     * BEWARE: Foreign keys in other tables may be lost!
     * //TODO Die User-ID ist Key in anderen Tabellen, wie machen wir das mit dem löschen?
     *
     * @param ID identifier (Primary key) of table entry that want to be deleted
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteUser(int ID) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Users WHERE ID = ?;", ID)
                .execute();
    }

    /**
     * Deletes an User object from database table User.
     * BEWARE:  Foreign keys in other tables may be lost!
     *
     * @param name identifier (unique column) of table entry that want to be deleted
     * @return true if successful otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean deleteUser(String name) throws SQLException
    {
        return SQLHandler
                .sql("DELETE FROM Users WHERE name = ?;", name)
                .execute();
    }

    /**
     * Insert a new entry in database table User.
     *
     * @param name Name of new User entry
     * @return ID of inserted row if successful otherwise -1
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public long insertUser(int ID, String name)
    {
        return SQLHandler
                .sql("INSERT INTO Users (ID, name) VALUES (?, ?);", ID, name)
                .execute(rs -> rs.next() ? rs.getLong(1) : -1L);
    }

    /**
     * Loads all User information from database table User in an user class object.
     *
     * @return ArrayList of type User if local db has entries
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public ArrayList<User> selectAllUsers()
    {
        ArrayList<User> tmp = new ArrayList<>();
        return SQLHandler
                .sql("SELECT * FROM Users")
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
     * Checks if the user exist in database table user.
     *
     * @param name name of the user
     * @return True if the user exists otherwise false
     * @throws SQLException database connection lost, sql-Syntax-Error
     */
    public boolean userExists(String name) throws SQLException
    {
        return SQLHandler
                .sql("SELECT * FROM Users WHERE name = ?", name)
                .executeQuery(rs -> rs.next());
    }
}