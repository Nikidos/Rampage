package Command;

import java.util.ArrayList;
import java.util.List;

public class ListMessageCommand extends Command {
    List<MessageCommand> messages= new ArrayList<MessageCommand>();

    public ListMessageCommand(String nameCommand) {
        super(nameCommand);
    }
    public void add(MessageCommand message) {
        messages.add(message);
    }
    public List<MessageCommand> getMessages() { return  messages; }
}
