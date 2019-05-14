package Command;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChatCommand extends Command {


    private String userName;
    private String chatName;
    private String hash;
    private byte type;

    public ChatCommand(String userName, String chatName, byte type, String nameCommand) throws NoSuchAlgorithmException {
        super(nameCommand);
        this.userName = userName;
        this.chatName = chatName;
        this.type = type;
        MessageDigest md = MessageDigest.getInstance("MD5");
        this.hash = new BigInteger(1, md.digest((userName+chatName).getBytes())).toString(16);
    }
    public ChatCommand(ChatCommand copy) throws NoSuchAlgorithmException {
        super(copy.nameCommand);
        this.userName = copy.userName;
        this.chatName = copy.chatName;
        this.type = copy.type;
        MessageDigest md = MessageDigest.getInstance("MD5");
        this.hash = new BigInteger(1, md.digest((userName+chatName).getBytes())).toString(16);
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) { this.userName = userName; }
    public String getChatName() {
        return chatName;
    }
    public String getHash() {
        return hash;
    }
    public byte getType() {
        return type;
    }
    @Override
    public String toString()
    {
        return userName+":"+ chatName;
    }

}
