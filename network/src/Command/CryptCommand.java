package Command;

public class CryptCommand extends Command {

    public byte[] publicKey;
    public CryptCommand( byte[] publicKey,String nameCommand ) {
        super(nameCommand);
        this.publicKey = publicKey;
    }
}
