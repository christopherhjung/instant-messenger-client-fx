package com.htwsaar.sql;

import com.htwsaar.utils.Run;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages a SQLite connection. This includes open,
 * close and store the connection. (Singleton pattern)
 *
 * @author Matthias Gessner
 * @version 2.1
 */
public class SQLConnection
{
    private static final String NO_DATABASE_ERROR = "No database name selected.";
    public static final SQLConnection INSTANCE = new SQLConnection();

    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DB_LOCATION = "resources/";

    private String dbName;
    private Connection con;

    static
    {
        try
        {

            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Private constructor
     */
    private SQLConnection()
    {
    }


    public boolean isConnected()
    {
        return con != null && !Run.safe(() -> con.isClosed(), false);
    }

    /**
     * Returns the current connection. If there is no
     * connection, a new will be created.
     *
     * @return current connection object
     * @throws SQLException if there is no database name
     *                      set when {@Link #newConnection()} is called.
     */
    public Connection getConnection()
    {
        return con;
    }

    /**
     * Establish a new connection to the SQLite database.
     * If there is no database, an empty database will
     * be created by SQLite itself.
     *
     * @return new Connection-Object
     * @throws SQLException if there is no database name set.
     */
    public boolean connect(String dbName)
    {
        try
        {
            if (isConnected())
            {
                close();
            }

            con = DriverManager.getConnection("jdbc:sqlite:" + DB_LOCATION + dbName);

            SQLDatabase.checkConsistency(con);
            return !con.isClosed();
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the current connection
     *
     * @throws SQLException if connection could not be closed
     */
    public void close()
    {
        Run.safe(() -> {
            con.close();
            con = null;
        });
    }
}
