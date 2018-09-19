package com.htwsaar;
/**
 * Methoden um Exceptions abzufangen ohne viel Code zu schreiben.
 * @author Christopher Jung
 */
public class Run
{

    public static <T> T safe(ResultBlock<T> block)
    {
        return safe(block, null);
    }

    public static <T> T safe(ResultBlock<T> block, T defaultValue)
    {
        try
        {
            return block.run();
        } catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static boolean safe(Block block)
    {
        try
        {
            block.run();
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    @FunctionalInterface
    public interface ResultBlock<T>
    {
        T run() throws Exception;
    }

    @FunctionalInterface
    public interface Block
    {
        void run() throws Exception;
    }
}
