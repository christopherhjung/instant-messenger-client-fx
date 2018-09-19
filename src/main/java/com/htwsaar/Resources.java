package com.htwsaar;

import java.net.URL;

public class Resources
{
    public static URL get(String name)
    {
        return Resources.class.getClassLoader().getResource(name);
    }
}
