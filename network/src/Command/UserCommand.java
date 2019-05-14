package Command;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserCommand extends Command {

    private String userName;
    private byte[] pass;
    private boolean isDone;

    public UserCommand(String userName, byte[] pass, String nameCommand)  {
        super(nameCommand);
        this.nameCommand= nameCommand;
        this.userName = userName;
        this.pass=pass;
    }

    public UserCommand(boolean isDone, String userName, String nameCommand) {
        super(nameCommand);
        this.isDone=isDone;
        this.userName=userName;
    }

    public static String passToHash(String pass) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new  BigInteger(1, md.digest(pass.getBytes())).toString(16);
    }
    public boolean isDone() {
        return isDone;
    }
    public String getUserName() {
        return userName;
    }
    public byte[] getPass() {
        return pass;
    }



}
