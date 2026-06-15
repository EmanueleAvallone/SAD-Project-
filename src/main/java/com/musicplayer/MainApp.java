package com.musicplayer;

import com.musicplayer.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/musicplayer/view/MusicPlaylistManagerView.fxml")
        );

        Scene scene = new Scene(loader.load());
        MainController mainController = loader.getController();
        stage.setTitle("Music Playlist Manager");
        stage.setOnCloseRequest(event -> {
            try {
                mainController.saveSessionOnExit();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
