package control;

import Command.UserCommand;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.TCPConnection;


public class RegController  {

    private TCPConnection tConnection;
    private Gson gson;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordFieldRepeat;

    @FXML
    void btnInReg() throws Exception {
        if(!loginField.getText().equals("") && !passwordField.getText().equals("") && !passwordFieldRepeat.getText().equals("")) {
            if(passwordField.getText().equals(passwordFieldRepeat.getText())) {
                byte[] pass=tConnection.getEncryption().encrypt(UserCommand.passToHash(passwordField.getText()));
                UserCommand user= new UserCommand(loginField.getText(),pass, "UserReg");
                tConnection.sendMsg(gson.toJson(user));
            }
        }
    }

    @FXML
    void initialize(TCPConnection tmpTCP) {
        gson = new Gson();
        tConnection=tmpTCP;
    }
}