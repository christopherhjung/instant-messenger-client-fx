package com.htwsaar.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates the table structure of SQLite-database.
 * <p>
 * A SQLite database will be automatically created by the connection attempt
 * if there is no database. In that case the table structure will be created.
 *
 * @author Matthias Gessner
 * @version 2.0
 */
public class SQLDatabase
{
    private static final String CREATE_TABLE_ERROR = "A missing table could not been created.";

    private static final HashMap<String, String> TABLES = new HashMap<String, String>()
    {
        {
            put("Users", "CREATE TABLE IF NOT EXISTS Users (" +
                    "ID INTEGER PRIMARY KEY, " +
                    "Name TEXT NOT NULL UNIQUE);");
            put("Groups", "CREATE TABLE IF NOT EXISTS Groups (" +
                    "ID INTEGER PRIMARY KEY, " +
                    "Name TEXT NULL, " +
                    "Creator INTEGER NOT NULL, " +
                    "FOREIGN KEY (Creator) REFERENCES User(ID));");
            put("Messages", "CREATE TABLE IF NOT EXISTS Messages (" +
                    "ID INTEGER PRIMARY KEY, " +
                    "Origin INTEGER NULL, " +
                    "Destination INTEGER NULL, " +
                    "Message TEXT NULL, " +
                    "Timestamp Timestamp NULL, " +
                    "MessageType INTEGER NULL)");
            put("Members", "CREATE TABLE IF NOT EXISTS Members (" +
                    "Chat_ID INTEGER, " +
                    "User_ID INTEGER, " +
                    "PRIMARY KEY (Chat_ID, User_ID));");
        }
    };

    /**
     * Checks if all TABLES in the HashMap 'TABLES' exists.
     * If a table is missing it will be created.
     *
     * @throws SQLException if database connection is lost or a missing table could not been created
     */
    public static void checkConsistency(Connection connection) throws SQLException
    {
        for (Map.Entry<String, String> entry : TABLES.entrySet())
        {
            boolean exists = SQLHandler

                    .sql("SELECT name FROM sqlite_master WHERE type='table' and name = ?;", entry.getKey())
                    .executeQuery(ResultSet::next);

            if (!exists && !SQLHandler.sql(entry.getValue()).execute())
            {
                throw new SQLException(CREATE_TABLE_ERROR);
            }
        }
    }
}