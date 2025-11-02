package se233.contra_project.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * JavaFX heads-up display overlay for player lives and score.
 */
public class HUDOverlay extends StackPane {
    private static final double PREF_WIDTH = 800;
    private static final double PREF_HEIGHT = 60;
    private static final double LIFE_ICON_WIDTH = 16;
    private static final double LIFE_ICON_HEIGHT = 32;

    private int playerLives = 3;
    private int playerScore = 0;
    private Image lifeIcon;

    private final HBox livesBox = new HBox(10);
    private final Label scoreTitleLabel = new Label("SCORE");
    private final Label scoreValueLabel = new Label();
    private final Label fallbackLivesLabel = new Label();

    public HUDOverlay() {
        initializeLayout();
        loadLifeIcon();
        refreshLivesDisplay();
        refreshScoreDisplay();
    }

    private void initializeLayout() {
        setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setBackground(Background.EMPTY);
        setMouseTransparent(true);
        setPickOnBounds(false);

        livesBox.setAlignment(Pos.CENTER_LEFT);
        livesBox.setPickOnBounds(false);

        fallbackLivesLabel.setTextFill(Color.GRAY);
        fallbackLivesLabel.setVisible(false);
        fallbackLivesLabel.setManaged(false);
        fallbackLivesLabel.setFont(FontManager.getFxFont(18));

        scoreTitleLabel.setTextFill(Color.GRAY);
        scoreTitleLabel.setFont(FontManager.getFxFont(16));

        scoreValueLabel.setTextFill(Color.GRAY);
        scoreValueLabel.setFont(FontManager.getFxFont(18));

        HBox scoreBox = new HBox(10, scoreTitleLabel, scoreValueLabel);
        scoreBox.setAlignment(Pos.CENTER_RIGHT);
        scoreBox.setPickOnBounds(false);

        BorderPane content = new BorderPane();
        content.setBackground(Background.EMPTY);
        content.setPadding(new Insets(12, 24, 12, 24));
        content.setPickOnBounds(false);
        content.setLeft(livesBox);
        content.setRight(scoreBox);

        getChildren().add(content);
        StackPane.setAlignment(content, Pos.TOP_LEFT);
    }

    private void loadLifeIcon() {
        String[] resourceCandidates = {
                "/se233/contra_project/ui/Life.png",
                "/Life.png"
        };

        for (String path : resourceCandidates) {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    lifeIcon = new Image(stream, LIFE_ICON_WIDTH, LIFE_ICON_HEIGHT, false, false);
                    return;
                }
            } catch (Exception ignored) {
                // Try the next candidate
            }
        }

        String[] fileCandidates = {
                "Life.png",
                "src/main/resources/se233/contra_project/ui/Life.png",
                "src/main/resources/se233/contra_project/sprites/Life.png",
                "resources/Life.png",
                "images/Life.png"
        };

        for (String filePath : fileCandidates) {
            File file = new File(filePath);
            if (file.exists()) {
                try (InputStream stream = new FileInputStream(file)) {
                    lifeIcon = new Image(stream, LIFE_ICON_WIDTH, LIFE_ICON_HEIGHT, false, false);
                    return;
                } catch (Exception ignored) {
                    // Continue searching
                }
            }
        }

        System.err.println("⚠ Life icon not found; displaying text fallback.");
        lifeIcon = null;
    }

    public void updateHUD(int lives, int score) {
        if (Platform.isFxApplicationThread()) {
            applyUpdate(lives, score);
        } else {
            Platform.runLater(() -> applyUpdate(lives, score));
        }
    }

    private void applyUpdate(int lives, int score) {
        boolean livesChanged = lives != this.playerLives;
        boolean scoreChanged = score != this.playerScore;

        this.playerLives = Math.max(0, lives);
        this.playerScore = Math.max(0, score);

        if (livesChanged) {
            refreshLivesDisplay();
        }
        if (scoreChanged) {
            refreshScoreDisplay();
        }
    }

    private void refreshLivesDisplay() {
        livesBox.getChildren().clear();

        if (lifeIcon == null) {
            fallbackLivesLabel.setText("LIVES: " + playerLives);
            fallbackLivesLabel.setManaged(true);
            fallbackLivesLabel.setVisible(true);
            if (!livesBox.getChildren().contains(fallbackLivesLabel)) {
                livesBox.getChildren().add(fallbackLivesLabel);
            }
            return;
        }

        fallbackLivesLabel.setManaged(false);
        fallbackLivesLabel.setVisible(false);

        for (int i = 0; i < playerLives; i++) {
            ImageView view = new ImageView(lifeIcon);
            view.setFitWidth(LIFE_ICON_WIDTH);
            view.setFitHeight(LIFE_ICON_HEIGHT);
            view.setPreserveRatio(false);
            view.setSmooth(false);
            livesBox.getChildren().add(view);
        }
    }

    private void refreshScoreDisplay() {
        scoreValueLabel.setText(String.format("%08d", Math.max(0, playerScore)));
    }

    public int getPlayerLives() {
        return playerLives;
    }

    public void setPlayerLives(int playerLives) {
        updateHUD(playerLives, playerScore);
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        updateHUD(playerLives, playerScore);
    }

    public void addScore(int points) {
        if (points <= 0) {
            return;
        }
        updateHUD(playerLives, playerScore + points);
    }

    public void loseLife() {
        if (playerLives <= 0) {
            return;
        }
        updateHUD(playerLives - 1, playerScore);
    }

    public void gainLife() {
        updateHUD(playerLives + 1, playerScore);
    }
}
