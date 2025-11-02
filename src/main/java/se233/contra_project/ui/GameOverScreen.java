package se233.contra_project.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameOverScreen extends StackPane {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;
    private static final int STAR_COUNT = 70;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random();
    private AnimationTimer animationTimer;
    private int score;
    private boolean victory;
    private Runnable onRestart;

    private static class Star {
        double x;
        double y;
        double speed;
        double size;
    }

    public GameOverScreen() {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);

        initStars(STAR_COUNT);
        setupInputHandling();
        setupFocusHandling();
        startAnimation();
        draw();
    }

    public void showResult(int score, boolean victory) {
        this.score = Math.max(0, score);
        this.victory = victory;
        draw();
        Platform.runLater(this::requestFocus);
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    private void initStars(int count) {
        stars.clear();
        for (int i = 0; i < count; i++) {
            Star star = new Star();
            star.x = random.nextDouble() * WIDTH;
            star.y = random.nextDouble() * HEIGHT;
            star.size = 1 + random.nextDouble() * 2;
            star.speed = 30 + random.nextDouble() * 40;
            stars.add(star);
        }
    }

    private void updateStars(double deltaSeconds) {
        for (Star star : stars) {
            star.y += star.speed * deltaSeconds;
            if (star.y > HEIGHT) {
                star.y = 0;
                star.x = random.nextDouble() * WIDTH;
                star.speed = 30 + random.nextDouble() * 40;
                star.size = 1 + random.nextDouble() * 2;
            }
        }
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastTime = 0L;

            @Override
            public void handle(long now) {
                if (lastTime == 0L) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                updateStars(delta);
                draw();
            }
        };
        animationTimer.start();
    }

    private void setupInputHandling() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                if (onRestart != null) {
                    onRestart.run();
                }
            } else if (code == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });
    }

    private void setupFocusHandling() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::requestFocus);
            }
        });
    }

    private void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawStars();

        String headline = victory ? "MISSION ACCOMPLISHED" : "GAME OVER";
        String subline = victory ? "You cleared the final boss!" : "All lives lost...";
        String prompt = "Press ENTER to return to the title screen";
        Color headlineColor = victory ? Color.CYAN : Color.ORANGERED;

        drawCenteredText(headline, 180, FontManager.getFxFont(46), headlineColor);
        drawCenteredText(subline, 230, FontManager.getFxFont(24), Color.WHITE);

        drawCenteredText("1P SCORE", 310, FontManager.getFxFont(22), Color.GRAY);
        drawCenteredText(String.format("%08d", score), 350, FontManager.getFxFont(28), Color.WHITE);

        drawCenteredText(prompt, 440, FontManager.getFxFont(18), Color.LIGHTGRAY);
    }

    private void drawStars() {
        gc.setFill(Color.WHITE);
        for (Star star : stars) {
            double alpha = 0.6 + random.nextDouble() * 0.4;
            gc.setGlobalAlpha(alpha);
            gc.fillOval(star.x, star.y, star.size, star.size);
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawCenteredText(String text, double y, Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        double textWidth = computeTextWidth(font, text);
        gc.fillText(text, (WIDTH - textWidth) / 2, y);
    }

    private double computeTextWidth(Font font, String text) {
        Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }
}
