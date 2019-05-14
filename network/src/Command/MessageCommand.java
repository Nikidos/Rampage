package Command;

public class MessageCommand extends Command {


    private String userName;
    private String hash;
    private String message;

    public MessageCommand(String userName, String hash, String message, String nameCommand) {
        super(nameCommand);
        this.userName = userName;
        this.hash = hash;
        this.message = message;
    }

    public String getUserName() { return userName; }
    public String getHash() { return hash; }
    public String getMessage() { return message; }

    @Override
    public String toString()
    {
        return userName+":"+message;
    }
}
