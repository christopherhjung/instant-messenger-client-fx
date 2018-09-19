package com.htwsaar.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides methods to run sql-Queries and statements
 *
 * @author Christopher Jung und Matthias Gessner
 * @version 1.4
 */
public class SQLHandler
{
    /**
     * A static method(factory method pattern) that calls
     * the private constructor of the class.
     *
     * @param sql    sql statement that should be executed
     * @param params variable number of sql statement parameters
     * @return Object of SQLHandler
     */
    public static SQLHandler sql(String sql, Object... params)
    {
        return new SQLHandler(sql, params);
    }

    /**
     * Declares an functional interface
     * to implement the usage of lambda expressions
     *
     * @param <R> Return value of the implemented method
     */
    @FunctionalInterface
    public interface SQLFunction<R>
    {
        R apply(ResultSet resultSet) throws SQLException;
    }

    private Connection connection;
    private final Object[] params;
    private final String sql;

    /**
     * Private constructor of the class. Called by {@link #sql(String sql, Object... params) sql}
     *
     * @param sql    sql Statement that want to be executed
     * @param params SQl statement parameters of the sql statement
     */
    private SQLHandler(String sql, Object[] params)
    {
        this.sql = sql;
        this.params = params;
    }

    public SQLHandler setConnection(Connection connection)
    {
        this.connection = connection;
        return this;
    }

    /**
     * Builds an PreparedStatement from {@link SQLHandler#sql} and {@link SQLHandler#params}
     *
     * @return PreparedStatement
     * @throws SQLException database connection lost
     */
    private PreparedStatement buildStatement() throws SQLException
    {
        PreparedStatement statement = null;

        if (connection == null)
        {
            connection = SQLConnection.INSTANCE.getConnection();
        }

        if (sql != null)
        {
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++)
            {
                statement.setObject(i + 1, params[i]);
            }
        }
        return statement;
    }

    /**
     * Executes an sql-Query (Select)
     *
     * @param sqlResultFunction functional interface {@link SQLFunction}
     * @param <R>               Return value of of implemented {@link SQLFunction#apply(ResultSet)}
     * @return ResultSet of executed sql-Query
     * @throws SQLException database connection lost, wrong sql-Syntax
     */
    public <R> R executeQuery(SQLFunction<R> sqlResultFunction)
    {
        try
        {
            PreparedStatement statement = buildStatement();
            ResultSet resultSet = statement.executeQuery();
            R result = sqlResultFunction.apply(resultSet);
            statement.close(); //noetig? -> Scope

            return result;
        } catch (SQLException e)
        {
            return null;
        }
    }

    /**
     * Overloads {@link SQLHandler#executeQuery(SQLFunction)} just in case
     * it called without a further lambda expression.
     *
     * @return true if the execution was successful otherwise it returns false
     * @throws SQLException database connection lost, wrong sql-Syntax
     */
    public boolean executeQuery() throws SQLException
    {
        return executeQuery(rs -> true) != null;
    }

    /**
     * Executes an sql-Statement (Insert, Delete, Update, ...)
     *
     * @param sqlFunction functional interface {@link SQLFunction}
     * @param <T>         Return value of of implemented {@link SQLFunction#apply(ResultSet)}
     * @return Returns the affected Keys of the sql-Statement.
     * @throws SQLException database connection lost, wrong sql-Syntax
     */
    public <T> T execute(SQLFunction<T> sqlFunction)
    {

        try (PreparedStatement statement = buildStatement())
        {
            connection.setAutoCommit(false);
            statement.execute();
            T result = sqlFunction.apply(statement.getGeneratedKeys());

            connection.commit();
            connection.setAutoCommit(true);
            return result;

        } catch (SQLException e)
        {
            try
            {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e2)
            {
                //If rollback fails, the connection will be closed
                //and so are the changes rollbacked too.
                SQLConnection.INSTANCE.close();
            }
        }

        return null;
    }

    /**
     * Overloads {@link SQLHandler#execute(SQLFunction)} just in case
     * it called without a further lambda expression.
     *
     * @return true if the execution was successful otherwise it returns false
     * @throws SQLException database connection lost, wrong sql-Syntax
     */
    public boolean execute()
    {
        return execute(rs -> true) != null;
    }
}
