package ru.vksender.vksender;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class VkSender extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL url = new File("/home/asl/IdeaProjects/vk-sender/src/main/resources/ru/vksender/vksender/LoginAndReg.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
//			String css = this.getClass().getResource("designLogin.css").toExternalForm();
//			scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        launch(args);
    }
}
