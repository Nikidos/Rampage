package Command;

public class BooleanCommand extends Command {
    boolean bool;

    public BooleanCommand(boolean bool, String nameCommand) {
        super(nameCommand);
        this.bool=bool;
    }
    public boolean isBool() { return bool; }
}
