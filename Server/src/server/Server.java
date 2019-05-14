package server;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.*;
import Command.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;


public class Server implements TCPConnectionListener {

    private final ArrayList<TCPConnection> connections= new ArrayList<>();
    private Gson gson;

    private Server() throws FileNotFoundException, SQLException {
        gson = new Gson();
        System.out.println("Server started...");
        RunDBCommand.connectionBD();
        System.out.println("Connection to DataBase Completed!");
        try (ServerSocket serverSocket= new ServerSocket(8190))
        {
            System.out.println("Network connection set to port 8190");
            while(true)
            {
                try
                {
                    new TCPConnection(serverSocket.accept(), this);
                }
                catch (Exception e) {System.out.println("TCPConnection exception"+ e);}
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection)
    {
        connections.add(tcpConnection);
        tcpConnection.sendMsg(gson.toJson(
                new CryptCommand(tcpConnection.getEncryption().getPublicKeyEncode(),"CryptCommand")));
        System.out.println("Client "+ tcpConnection+ " connection.");
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection)
    {
        connections.remove(tcpConnection);
        System.out.println("Client "+ tcpConnection+ " disconnect.");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception:" + e);
    }

    @Override
    public synchronized void onInputMsg(TCPConnection tcpConnection, String objectMsg) throws Exception {
        String command= gson.fromJson(objectMsg,Command.class).getNameCommand();
        if(command.equals("UserReg")) {
            UserCommand answer = RunDBCommand.RunReg(gson.fromJson((objectMsg), UserCommand.class),tcpConnection.getEncryption());
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("UserAuth")) {
            UserCommand answer = RunDBCommand.RunAuth(gson.fromJson((objectMsg), UserCommand.class),tcpConnection.getEncryption());
            if(answer.isDone()) tcpConnection.setUserName(answer.getUserName());
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("ChatCr")) {
            ChatCommand answer = RunDBCommand.ChatCreate(gson.fromJson(objectMsg, ChatCommand.class));
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("SearchCh")) {
            ListChatCommand answer = RunDBCommand.ChatSearch(gson.fromJson(objectMsg, ChatCommand.class));
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("ChatAdd")) {
            BooleanCommand answer= RunDBCommand.ChatAddToList(gson.fromJson(objectMsg, ChatCommand.class),tcpConnection.getUserName());
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("UpdateChatList")) {
            ListChatCommand answer = RunDBCommand.UpdateChatList(tcpConnection.getUserName());
            tcpConnection.sendMsg(gson.toJson(answer));
        }
        if(command.equals("ActivateChat")) {
            ChatCommand activeChat= gson.fromJson(objectMsg, ChatCommand.class);
            for (TCPConnection connection : connections) {
                if(connection.getUserName().equals(activeChat.getUserName())) {
                    connection.setActiveChat(activeChat);
                    connection.sendMsg(gson.toJson(RunDBCommand.UpdateMessageList(activeChat.getHash())));
                }
            }
        }
        if(command.equals("EnterMessage")) {
            MessageCommand message= gson.fromJson(objectMsg,MessageCommand.class);
            RunDBCommand.EnterMessage(message);
            for (TCPConnection connection : connections) {
               if(connection.getActiveChat().getHash().equals(message.getHash())) {
                   connection.sendMsg(gson.toJson(message));
                }
            }
        }
        if(command.equals("ExitChat")) {
            ChatCommand exitChat= gson.fromJson(objectMsg,ChatCommand.class);
            RunDBCommand.ExitChat(exitChat);
            tcpConnection.sendMsg(gson.toJson(RunDBCommand.UpdateChatList(exitChat.getUserName())));
        }

    }
    static public void main (String[] args) {
        try {
            new Server();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

