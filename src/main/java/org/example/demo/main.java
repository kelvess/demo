package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(true);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
        stage.setScene(scene);
        stage.show();
    }

}