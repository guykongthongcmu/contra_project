package se233.contra_project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.contra_project.logging.LogConfig;
import se233.contra_project.ui.GameOverScreen;
import se233.contra_project.ui.Stage1Screen;
import se233.contra_project.ui.Stage2Screen;
import se233.contra_project.ui.Stage3Screen;
import se233.contra_project.ui.StartScreen;

public class Launcher extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private Stage primaryStage;
    private StartScreen startScreen;
    private GameOverScreen gameOverScreen;

    private Scene startScene;
    private Scene stage1Scene;
    private Scene stage2Scene;
    private Scene stage3Scene;
    private Scene gameOverScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Contra - Boss Demo");
        primaryStage.setResizable(false);

        startScreen = new StartScreen();
        startScreen.setLauncher(this);
        startScene = new Scene(startScreen, WINDOW_WIDTH, WINDOW_HEIGHT);

        createStageScenes();
        createGameOverScene();

        primaryStage.setUserData(this);
        primaryStage.setScene(startScene);
        primaryStage.show();
        startScreen.requestFocus();
    }

    public void switchToGameScreen() {
        switchToStage(1);
    }

    public void switchToStage(int stageIndex) {
        Scene sceneToShow;
        switch (stageIndex) {
            case 2:
                sceneToShow = stage2Scene;
                break;
            case 3:
                sceneToShow = stage3Scene;
                break;
            case 1:
            default:
                sceneToShow = stage1Scene;
                break;
        }

        primaryStage.setScene(sceneToShow);
        if (sceneToShow.getRoot() != null) {
            sceneToShow.getRoot().requestFocus();
        }
        primaryStage.show();
    }

    public void switchToStartScreen() {
        Runnable action = () -> {
            createStageScenes();
            primaryStage.setScene(startScene);
            primaryStage.show();
            if (startScene.getRoot() != null) {
                startScene.getRoot().requestFocus();
            }
        };

        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public void switchToGameOver(int score, boolean victory) {
        Runnable action = () -> {
            if (gameOverScreen == null) {
                createGameOverScene();
            }
            gameOverScreen.showResult(score, victory);
            primaryStage.setScene(gameOverScene);
            primaryStage.show();
            gameOverScreen.requestFocus();
        };

        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    private void createStageScenes() {
        Stage1Screen stage1Screen = new Stage1Screen(() -> switchToStage(2));
        stage1Scene = new Scene(stage1Screen, WINDOW_WIDTH, WINDOW_HEIGHT);

        Stage2Screen stage2Screen = new Stage2Screen(() -> switchToStage(3));
        stage2Scene = new Scene(stage2Screen, WINDOW_WIDTH, WINDOW_HEIGHT);

        Stage3Screen stage3Screen = new Stage3Screen();
        stage3Scene = new Scene(stage3Screen, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void createGameOverScene() {
        gameOverScreen = new GameOverScreen();
        gameOverScreen.setOnRestart(this::switchToStartScreen);
        gameOverScene = new Scene(gameOverScreen, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public static void main(String[] args) {
        LogConfig.configure();
        launch(args);
    }
}
