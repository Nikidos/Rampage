package control;

import Command.ChatCommand;
import Command.Command;
import Command.MessageCommand;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.TCPConnection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MainController {

    private TCPConnection tConnection;
    private Gson gson;
    private ChatCommand selectedChat;

    @FXML
    private TextField searchField;

    @FXML
    private Label labelActiveChat;

    @FXML
    public ListView<ChatCommand> listView;

    @FXML
    private MenuItem addChatBtn;

    @FXML
    private MenuItem DeleteChatBtn;

    @FXML
    private TextArea inputMessField;

    @FXML
    public TextArea MessageArea;

    @FXML
    void ChanelBtnClick() throws NoSuchAlgorithmException {
        TextInputDialog textInputDialog= new TextInputDialog();
        textInputDialog.setTitle("Введите название");
        textInputDialog.showAndWait();
        String result=textInputDialog.getResult();
        if(result!=null && !result.equals("")) {
            tConnection.sendMsg(gson.toJson(new ChatCommand(tConnection.getUserName(), result, (byte) 0, "ChatCr")));
        }
    }

    @FXML
    void ChatBtnClick() throws NoSuchAlgorithmException {
        TextInputDialog textInputDialog= new TextInputDialog();
        textInputDialog.setTitle("Введите название");
        textInputDialog.showAndWait();
        String result=textInputDialog.getResult();
        if(result!=null && !result.equals("")) {
            tConnection.sendMsg(gson.toJson(new ChatCommand(tConnection.getUserName(), result, (byte) 1, "ChatCr")));
        }
}

    @FXML
    void addChatBtnClick() {
        ChatCommand selectedChat= listView.getSelectionModel().getSelectedItem();
        selectedChat.setNameCommand("ChatAdd");
        tConnection.sendMsg(gson.toJson(selectedChat));
    }

    @FXML
    void searchEntered(KeyEvent event) throws NoSuchAlgorithmException {
        if(event.getCode().equals(KeyCode.ENTER) && !searchField.getText().equals("")) {
            tConnection.sendMsg(gson.toJson(new ChatCommand(null,searchField.getText(), (byte) 0,"SearchCh")));
            searchField.setText("");
            addChatBtn.setVisible(true);
            DeleteChatBtn.setVisible(false);
        }
    }

    @FXML
    void searchCanceled() {
        addChatBtn.setVisible(false);
        DeleteChatBtn.setVisible(true);
        tConnection.sendMsg(gson.toJson(new Command("UpdateChatList")));
    }

    @FXML
    void ListViewClick() throws NoSuchAlgorithmException {
        if(!addChatBtn.isVisible())
        {
            if(listView.getSelectionModel().isEmpty()) return;
            selectedChat= new ChatCommand (listView.getSelectionModel().getSelectedItem());
            labelActiveChat.setText(selectedChat.toString());
            MessageArea.setText("");
            if(selectedChat.getType()==0 && !selectedChat.getUserName().equals(tConnection.getUserName())) {
                inputMessField.setText("Вы не являетесь администратором канала\nпоэтому не можете оставлять сообщения!");
                inputMessField.setEditable(false);
            } else {
                inputMessField.setText("");
                inputMessField.setEditable(true);
            }
            selectedChat.setNameCommand("ActivateChat");
            selectedChat.setUserName(tConnection.getUserName());
            tConnection.sendMsg(gson.toJson(selectedChat));
        }
    }

    @FXML
    void inputEntered(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER) && !inputMessField.getText().equals(""))
        {
            if(selectedChat==null) {
                inputMessField.setText("");
                return;
            }
            tConnection.sendMsg(gson.toJson(new MessageCommand(tConnection.getUserName(),
                    selectedChat.getHash(),inputMessField.getText(),"EnterMessage")));
            inputMessField.setText("");
        }
    }

    @FXML
    void DeleteChatBtnClick() {
        if(selectedChat!=null) {
            ChatCommand exitChat= selectedChat;
            exitChat.setNameCommand("ExitChat");
            tConnection.sendMsg(gson.toJson(exitChat));
            selectedChat=null;
            MessageArea.setText("");
        }
    }

    @FXML
    void ExitBtnClick() {
        tConnection.setUserName(null);
        Parent root = null;
        try {
            root = FXMLLoader.load((getClass().getResource("../scene/windowAuth.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Window> open = Stage.getWindows().filtered(Window::isShowing);
        Stage stageMain = (Stage) open.get(0);
        stageMain.setTitle("Rampage");
        stageMain.setResizable(true);
        stageMain.setMinWidth(600);
        stageMain.setMinHeight(450);
        stageMain.setScene(new Scene(root));
        stageMain.show();
    }

    @FXML
    void initialize(TCPConnection tmpTCP) {
        gson = new Gson();
        tConnection=tmpTCP;
        tConnection.sendMsg(gson.toJson(new Command("UpdateChatList")));
    }
}
