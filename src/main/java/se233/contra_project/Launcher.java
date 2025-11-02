package se233.contra_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.contra_project.game.GameSession;
import se233.contra_project.ui.BaseStageScreen;
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
    private Scene startScene;
    private Scene stage1Scene;
    private Scene stage2Scene;
    private Scene stage3Scene;
    private GameSession gameSession;
    private Scene gameOverScene;
    private GameOverScreen gameOverScreen;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Contra - Boss Demo");
        primaryStage.setResizable(false);

        gameSession = new GameSession();

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
        gameSession.reset();
        createStageScenes();
        switchToStage(1);
    }

    public void switchToStage(int stageIndex) {
        Scene sceneToShow;
        switch (stageIndex) {
            case 2:
                if (stage2Scene == null) {
                    stage2Scene = createStage2Scene();
                }
                sceneToShow = stage2Scene;
                break;
            case 3:
                if (stage3Scene == null) {
                    stage3Scene = createStage3Scene();
                }
                sceneToShow = stage3Scene;
                break;
            case 1:
            default:
                sceneToShow = stage1Scene;
                break;
        }

        if (sceneToShow != null && sceneToShow.getRoot() instanceof BaseStageScreen stageScreen) {
            stageScreen.syncSessionScore();
        }

        primaryStage.setScene(sceneToShow);
        if (sceneToShow.getRoot() != null) {
            sceneToShow.getRoot().requestFocus();
        }
        primaryStage.show();
    }

    public void switchToStartScreen() {
        primaryStage.setScene(startScene);
        primaryStage.show();
        if (startScene.getRoot() != null) {
            startScene.getRoot().requestFocus();
        }
    }

    public void switchToGameOver(int score, boolean victory) {
        if (gameOverScreen == null) {
            createGameOverScene();
        }
        int sanitizedScore = Math.max(0, score);
        gameOverScreen.showResult(sanitizedScore, victory);
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
        gameOverScreen.requestFocus();
    }

    private void createStageScenes() {
        Stage1Screen stage1Screen = new Stage1Screen(() -> switchToStage(2));
        stage1Screen.bindSession(gameSession);
        stage1Screen.setLauncher(this);
        stage1Scene = new Scene(stage1Screen, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage2Scene = null;
        stage3Scene = null;
    }

    private Scene createStage2Scene() {
        Stage2Screen stage2Screen = new Stage2Screen(() -> switchToStage(3));
        stage2Screen.bindSession(gameSession);
        stage2Screen.setLauncher(this);
        return new Scene(stage2Screen, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private Scene createStage3Scene() {
        Stage3Screen stage3Screen = new Stage3Screen();
        stage3Screen.bindSession(gameSession);
        stage3Screen.setLauncher(this);
        return new Scene(stage3Screen, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void createGameOverScene() {
        gameOverScreen = new GameOverScreen();
        gameOverScreen.setOnRestart(this::switchToStartScreen);
        gameOverScene = new Scene(gameOverScreen, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
