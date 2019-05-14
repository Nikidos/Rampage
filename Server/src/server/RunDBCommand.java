/*
This class is a wrapper for working with the database
you must initialize the database connection strings in the ConnectionDBFile.json
 */
package server;
import Command.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.RsaCrypt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public final class RunDBCommand {
     private static Statement stmt;
     private static ResultSet res;
     private static Connection connectionDataBase;

    public static void connectionBD() throws FileNotFoundException, SQLException {
        JsonParser parser= new JsonParser();
        JsonObject jsonObject= (JsonObject) parser.parse(new FileReader("connectDBFile.json"));
        String instanceName= jsonObject.get("instanceName").getAsString();
        String databaseName= jsonObject.get("databaseName").getAsString();
        String userName= jsonObject.get("userName").getAsString();
        String pass= jsonObject.get("pass").getAsString();
        String connectionUrl = "jdbc:sqlserver://%1$s;databaseName=%2$s;user=%3$s;password=%4$s;";
        String connectionString = String.format(connectionUrl, instanceName, databaseName, userName, pass);
        connectionDataBase= DriverManager.getConnection(connectionString);
    }

    public static UserCommand RunReg(UserCommand userCommand, RsaCrypt crypt) throws Exception {
        String pass= crypt.decrypt(userCommand.getPass());
        String query= String.format("SELECT Count(UserName) FROM Rampage.dbo.Users Where UserName='%s'", userCommand.getUserName());
        stmt=connectionDataBase.createStatement();
        res=stmt.executeQuery(query);
        if(res.next() && res.getInt(1)==0) {
            stmt.close();
            query= String.format("INSERT INTO Users  VALUES ('%s', '%s')", userCommand.getUserName(), pass);
            useStatment(query);
            return  new UserCommand(true,userCommand.getUserName(),"UserReg");
        } else {
            stmt.close();
            return new UserCommand(false,null,"UserReg");
        }
    }

    public static UserCommand RunAuth(UserCommand userCommand, RsaCrypt crypt) throws Exception {
        String pass= crypt.decrypt(userCommand.getPass());
        String query= String.format("select case when exists(select * from Users WHERE UserName='%s' and password='%s') then '1' else '0' end", userCommand.getUserName(), pass);
        stmt=connectionDataBase.createStatement();
        res=stmt.executeQuery(query);
        if(res.next() && res.getInt(1)==1) {
            stmt.close();
            return  new UserCommand(true,userCommand.getUserName(),"UserAuth");
        }
        else {
            return  new UserCommand(false,null,"UserAuth");
        }
    }

    public static ChatCommand ChatCreate(ChatCommand chCr) throws Exception {
        String query= String.format("INSERT INTO Chats  VALUES ('%s', '%s','%s','%s')", chCr.getUserName(), chCr.getChatName(), chCr.getHash(), chCr.getType());
        useStatment(query);
        ChatAddToList(chCr,chCr.getUserName());
        return chCr;
    }

    public static ListChatCommand ChatSearch(ChatCommand search) throws SQLException, NoSuchAlgorithmException {
        ListChatCommand listChat= new ListChatCommand("SearchCh");
        String query= String.format("SELECT * FROM Chats WHERE ChatName LIKE '%%%s%%'", search.getChatName());
        stmt=connectionDataBase.createStatement();
        res=stmt.executeQuery(query);
        while(res.next()) {
            listChat.add(new ChatCommand(res.getString(1),res.getString(2),res.getByte(4),null));
        }
        stmt.close();
        return listChat;
    }

    public static BooleanCommand ChatAddToList(ChatCommand search, String userName) throws Exception {
        String query= String.format("INSERT INTO ChatsList  VALUES ('%s', '%s')", userName, search.getHash());
        return new BooleanCommand(useStatment(query),"ChatAdd");
    }

    public static ListChatCommand UpdateChatList(String userName) throws SQLException, NoSuchAlgorithmException {
        ListChatCommand listChat= new ListChatCommand("UpdateChatList");
        String query= String.format("SELECT Chats.UserName, ChatName, Type FROM ChatsList join Chats on Chats.Hash=ChatsList.Hash where ChatsList.UserName='%s'", userName);
        stmt=connectionDataBase.createStatement();
        res=stmt.executeQuery(query);
        while(res.next()) {
            listChat.add(new ChatCommand(res.getString(1),res.getString(2),res.getByte(3),null));
        }
        stmt.close();
        return listChat;
    }

    public synchronized static MessageCommand EnterMessage(MessageCommand message) throws Exception {
        String query= String.format("INSERT INTO Messages VALUES (GETDATE(),'%s','%s','%s')", message.getUserName(), message.getHash(), message.getMessage());
        if(useStatment(query)) return message;
        return message;
    }

    public static ListMessageCommand UpdateMessageList(String hash) throws Exception {
        ListMessageCommand listMessages= new ListMessageCommand("UpdateMessageList");
        String query= String.format("SELECT * FROM Messages WHERE Hash='%s'", hash);
        stmt=connectionDataBase.createStatement();
        res=stmt.executeQuery(query);
        while(res.next()) {
           listMessages.add(new MessageCommand(res.getString(2),res.getString(3),res.getString(4),null));
        }
        return listMessages;
    }

    public static void ExitChat(ChatCommand exitChat) throws Exception {
        String query= String.format("DELETE ChatsList WHERE  UserName='%s' and Hash='%s' ", exitChat.getUserName(), exitChat.getHash());
        useStatment(query);
    }

    private static boolean useStatment(String query) throws Exception {
        stmt=connectionDataBase.createStatement();
        boolean successfully= stmt.execute(query);
        stmt.close();
        return successfully;
    }
}
