package control;

import javafx.application.Application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public  class Main extends Application   {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../scene/windowAuth.fxml"));
        primaryStage.setTitle("Авторизация");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image( Main.class.getResourceAsStream("../scene/icon.png")));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
