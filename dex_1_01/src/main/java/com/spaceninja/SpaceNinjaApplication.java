package com.spaceninja;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class SpaceNinjaApplication extends Application {

    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Ninja");
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/icons/icon.png")));
        gameController = new GameController();
        Scene scene = new Scene(gameController.getRoot(), 1200, 800);
        
        // Add key event handling for color switching
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                gameController.switchColor();
            } else if (event.getCode() == KeyCode.ENTER && !gameController.isGameRunning()) {
                gameController.startGame();
            }
        });
        
        scene.setOnMouseClicked(event -> {
            if (gameController.isGameRunning()) {
                gameController.switchColor();
            } else {
                gameController.startGame();
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Request focus for key events
        scene.getRoot().requestFocus();
        
        // Initialize the game
        gameController.initializeGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
