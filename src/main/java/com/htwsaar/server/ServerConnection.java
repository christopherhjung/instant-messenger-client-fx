package com.htwsaar.server;

import com.htwsaar.container.Group;
import com.htwsaar.container.Message;
import com.htwsaar.container.User;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Handles websocket server connection over STOMP Protokoll
 * and provides methods to interact with the server
 *
 * @author Matthias Gessner
 * @version 1.4
 */
public class ServerConnection
{
    public static Logger logger = Logger.getLogger(ServerConnection.class.getName());
    public static String HOST = "ws://localhost:8080/chat";

    private StompSession session;
    private WebSocketStompClient stompClient;
    private StompSessionHandler sessionHandler;

    public static ServerConnection INSTANCE = new ServerConnection();

    public boolean connect(String user, String pass)
    {
        try
        {
            close();

            connect(HOST, user, pass);

            return session.isConnected();
        } catch (Exception e)
        {
            return false;
        }
    }

    public void close()
    {
        if (session != null)
        {
            session.disconnect();
            session = null;
        }
    }

    /**
     * Create an new connection to the server
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private ServerConnection()
    {

    }

    /**
     * Closes the current session and tries to reconnect to the server
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void reconnect() throws ExecutionException, InterruptedException
    {
        if (!session.isConnected())
        {
            session.disconnect();
            stompClient.stop();
        }
    }

    /**
     * Configurate the connection settings and tries to connect to the server.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void connect(String host, String user, String pass) throws ExecutionException, InterruptedException
    {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(
                new WebSocketTransport(
                        new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        sessionHandler = new StompSessionHandler();
        session = stompClient.connect(host, (WebSocketHttpHeaders) null,
                connectionHeader(user, pass), sessionHandler).get();
    }

    /**
     * Creates an CONNECT Stomp-Header with user login credentials
     *
     * @return CONNECT StompHeader
     */
    private StompHeaders connectionHeader(String user, String pass)
    {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("login", user);
        connectHeaders.add("passcode", pass);
        return connectHeaders;
    }

    /**
     * Subscribes the message URL from that all messages will be received.
     */
    public void subscribeMessage(Consumer<Message[]> callback)
    {
        session.subscribe("/user/queue/messages", new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return Message[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                Message[] messages = (Message[]) payload;
                callback.accept(messages);
            }
        });

        //Informs the server that we subscribed the message URL
        //and that we ready to receive messages.
        session.send("/app/receive", "");
    }

    /**
     * Subscribes the message URL from that all messages will be received.
     */
    public void subscribeGroups(Consumer<Group> callback)
    {
        session.subscribe("/user/queue/groups", new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return Group.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                callback.accept((Group) payload);
            }
        });

        //Informs the server that we subscribed the message URL
        //and that we ready to receive messages.
        session.send("/app/receive", "");
    }

    /**
     * Subscribes the confirmation URL where the server confirms the receipt of
     * the client messages send to him.
     */
    public void subscribeMessageConfirmation(Consumer<Message> callback)
    {
        session.subscribe("/user/queue/received", new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                callback.accept((Message) payload);
            }
        });
    }


    /**
     * Send the passed object to the server.
     *
     * @param clientMessage ClientMessage object
     * @return true if it was send otherwise false
     */
    public boolean sendMessage(Message clientMessage)
    {
        if (session.isConnected())
        {
            session.send("/app/send", clientMessage);
            return true;
        }
        return false;
    }

    /**
     * Sends the messageID of an received message from the server
     * back to the server to confirm the receipt
     *
     * @param messageID server ID of the message
     * @return true if it was send otherwise false
     */
    public boolean sendConfirmation(int messageID)
    {
        if (session.isConnected())
        {
            System.out.println("Verbindung aktiv!" + messageID + " Message bestätigt");
            session.send("/app/received", new int[]{messageID});
            return true;
        }
        return false;
    }

    public User createUser(String name, String password)
    {
        return new RestTemplate().getForObject(
                "http://localhost:8080/users?name={name}&password={password}",
                User.class, name, password
        );
    }

    /**
     * Requests the server to send an user object about an name
     *
     * @param name name of the requested user object
     */
    public void getUserFromName(String name, Consumer<User> callback)
    {
        request("/app/get.user.by.name/" + name, callback, User.class);
    }

    public void getUserFromID(int ID, Consumer<User> callback)
    {
        request("/app/get.user.by.id/" + ID, callback, User.class);
    }

    public void getGroups(Consumer<Group[]> callback)
    {
        request("/app/get.groups/", callback, Group[].class);
    }

    public void getMemberOfGroup(int groupID, Consumer<User[]> callback)
    {
        request("/app/get.members/" + groupID, callback, User[].class);
    }

    public void getGroupByID(int groupID, Consumer<Group> callback)
    {
        request("/app/get.group.by.id/" + groupID, callback, Group.class);
    }

    public void getGroupByName(String name, Consumer<Group> callback)
    {
        request("/app/get.group.by.name/" + name, callback, Group.class);
    }

    public void addUserToGroup(int userID, int groupID)
    {
        session.send("/app/add.user.to.group/" + groupID + "/" + userID, null);
    }

    public void removeUserFromGroup(int userID, int groupID, Consumer<Group> callback)
    {
        request("/app/remove.user.from.group/" + groupID + "/" + userID, callback, Group.class);
    }

    public void createGroup(String name, Consumer<Group> callback)
    {
        request("/app/create.group/" + name, callback, Group.class);
    }

    public void removeGroup(int groupID, Consumer<Group> callback)
    {
        request("/app/delete.group/" + groupID, callback, Group.class);
    }


    private <T> void request(String name, Consumer<T> callback, Class<? extends T> type)
    {
        synchronized (session)
        {
            session.subscribe(name, new StompFrameHandler()
            {
                @Override
                public Type getPayloadType(StompHeaders headers)
                {
                    return type;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload)
                {
                    callback.accept((T) payload);
                }
            });
        }
    }

}
