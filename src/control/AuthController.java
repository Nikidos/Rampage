package control;
import Command.*;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.TCPConnection;
import net.TCPConnectionListener;

import java.util.List;

public class AuthController implements TCPConnectionListener{

    private TCPConnection tConnection;
    private Gson gson;
    private Stage stageReg;
    private Stage stageMain;
    private MainController mainControl;
    private Parent root;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    void btnRegClick(ActionEvent event) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../scene/windowReg.fxml"));
            Parent root=loader.load();
            RegController regControl= loader.getController();
            regControl.initialize(tConnection);         // initialize TCPConnection in reg Window
            stageReg= new Stage();
            stageReg.setTitle("Регистрация");
            stageReg.setResizable(false);
            stageReg.getIcons().add(new Image( Main.class.getResourceAsStream("../scene/icon.png")));
            stageReg.setScene(new Scene(root, 600, 400));
            stageReg.initModality(Modality.WINDOW_MODAL);
            stageReg.initOwner(((Node)event.getSource()).getScene().getWindow());
            stageReg.show();
    }

    @FXML
    void btnInClick(ActionEvent event) throws Exception {
        if(!loginField.getText().equals("") && !passwordField.getText().equals(""))
        {
            if(stageMain == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../scene/windowMain.fxml"));
                root = loader.load();
                mainControl= loader.getController();
                stageMain = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stageMain.setTitle("Rampage");
                stageMain.setResizable(true);
                stageMain.setMinWidth(600);
                stageMain.setMinHeight(450);
            }
            byte[] pass=tConnection.getEncryption().encrypt(UserCommand.passToHash(passwordField.getText()));
            UserCommand user= new UserCommand(loginField.getText(),pass, "UserAuth");
            tConnection.sendMsg(gson.toJson(user));
        }
    }

    @FXML
    void initialize() throws Exception {
        tConnection= new TCPConnection("localhost",8190,AuthController.this);
        gson = new Gson();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.show();
    }

    @Override
    public void onInputMsg  (TCPConnection tcpConnection, String objectMsg)  {
            Platform.runLater(() -> {
                try {
                    String command = gson.fromJson(objectMsg, Command.class).getNameCommand();
                    if (command.equals("CryptCommand")) {
                        tcpConnection.getEncryption().setPublicKey(gson.fromJson(objectMsg, CryptCommand.class).publicKey);
                    }
                    if (command.equals("UserReg"))
                    {
                        if (gson.fromJson((objectMsg), UserCommand.class).isDone()) {
                            stageReg.close();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Пользователь с таким именем уже существует");
                            alert.show();
                        }
                    }
                    if (command.equals("UserAuth"))
                    {
                        UserCommand user=gson.fromJson((objectMsg), UserCommand.class);
                        if (user.isDone()) {
                            tcpConnection.setUserName(user.getUserName());
                            stageMain.setScene(new Scene(root));
                            stageMain.show();
                            mainControl.initialize(tConnection);

                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Логин или пароль введены неверно");
                            alert.show();
                        }
                    }
                    if (command.equals("ChatCr"))
                    {
                        ChatCommand chat=gson.fromJson((objectMsg), ChatCommand.class);
                        mainControl.listView.getItems().add(chat);
                    }
                    if(command.equals("SearchCh") || command.equals("UpdateChatList"))
                    {
                        mainControl.listView.getItems().clear();
                        ListChatCommand listChat=gson.fromJson((objectMsg),ListChatCommand.class);
                        mainControl.listView.getItems().addAll(listChat.getCollection());
                    }
                    if(command.equals("ChatAdd"))
                    {
                        BooleanCommand bool=gson.fromJson((objectMsg),BooleanCommand.class);
                        if(bool.isBool()) {
                            new Alert(Alert.AlertType.INFORMATION, "Чат добавлен в список");
                        } else new Alert(Alert.AlertType.INFORMATION, "Чат не был добавлен");
                    }
                    if(command.equals("EnterMessage"))
                    {
                        MessageCommand message= gson.fromJson(objectMsg,MessageCommand.class);
                        mainControl.MessageArea.appendText(message.toString());
                    }
                    if(command.equals("UpdateMessageList"))
                    {
                        List<MessageCommand> messages=gson.fromJson(objectMsg,ListMessageCommand.class).getMessages();
                        for (MessageCommand message: messages) {
                            mainControl.MessageArea.appendText(message.toString());
                        }
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.show();
                }
            });
    }

}
