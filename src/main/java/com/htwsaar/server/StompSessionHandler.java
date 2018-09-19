package com.htwsaar.server;

import com.htwsaar.container.Message;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handels StompSession events like afterConnected and
 * all messages received from subscribed server URLs.
 *
 * @author Matthias Gessner
 * @version 1.2
 */
public class StompSessionHandler extends StompSessionHandlerAdapter
{





    /**
     * Event that is raised after an connection is established.
     *
     * @param session          session
     * @param connectedHeaders Details of the connection
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders)
    {
    }

    /**
     * Event that is raised if an error is occurred.
     */
    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception)
    {
        if (session.isConnected())
        {
            session.disconnect();
        }
        System.out.println("Exception in handleException des SessionHandlers");
        exception.printStackTrace(); //Exception leer?
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception)
    {
        System.out.println("Exception in handleTransportError");
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload)
    {
        System.out.println("handleFrame ausgefuhrt");
    }
}