package Command;

import java.util.ArrayList;

public class ListChatCommand extends Command {
     private ArrayList<ChatCommand> chats = new ArrayList<ChatCommand>();

    public ListChatCommand(String nameCommand) {
        super(nameCommand);
    }
    public void add(ChatCommand chat) {
        chats.add(chat);
    }
    public ArrayList<ChatCommand> getCollection()
    {
        return chats;
    }

}
