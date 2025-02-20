package com.patatedouce.openmotor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWin extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWin.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 921, 550);
        stage.setTitle("OpenMotor");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.JPG")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}