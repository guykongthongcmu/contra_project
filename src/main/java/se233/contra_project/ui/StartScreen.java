package se233.contra_project.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;

import java.io.File;

public class StartScreen extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    private boolean showPressStart = true;
    private Image titleImage;
    private int currentFrame = 0;
    private Image[] animatedFrames;
    private AnimationTimer frameTimer;

    private se233.contra_project.Launcher launcher;

    public StartScreen() {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        setupUI();
        loadTitleImage();
        setupAnimations();
        setupKeyListener();
        setupFocusHandling();
    }

    private void setupUI() {
        setPrefSize(800, 600);
        setStyle("-fx-background-color: black;");
    }

    private void loadTitleImage() {
        try {
            String filename = "Title Screens.png";
            String[] possiblePaths = {
                    filename,
                    "src/main/resources/" + filename,
                    "resources/" + filename,
                    "images/" + filename,
                    "src/main/resources/se233/contra_project/ui/" + filename
            };

            boolean found = false;
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    titleImage = new Image("file:" + file.getAbsolutePath());
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ ไม่พบไฟล์รูป, ใช้หน้าจอแบบวาดด้วย code...");
                createFallbackScreen();
            }

        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            createFallbackScreen();
        }
    }

    private void createFallbackScreen() {
        titleImage = null;
        System.out.println("ใช้หน้าจอแบบวาดด้วย code");
    }

    private void setupAnimations() {
        animationTimer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                if ((now - lastTime) / 1_000_000_000.0 >= 0.5) {
                    showPressStart = !showPressStart;
                    lastTime = now;
                    repaint();
                }
            }
        };
        animationTimer.start();

        // Frame animation if needed
        // frameTimer = new AnimationTimer() { ... };
    }

    private void setupKeyListener() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ENTER:
                case SPACE:
                    startGame();
                    break;
                case DIGIT1:
                case NUMPAD1:
                    startGameAtStage(1);
                    break;
                case DIGIT2:
                case NUMPAD2:
                    startGameAtStage(2);
                    break;
                case DIGIT3:
                case NUMPAD3:
                    startGameAtStage(3);
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
                case F1:
                    // Could implement file chooser in JavaFX
                    break;
            }
        });
        setFocusTraversable(true);
        requestFocus();
    }

    private void setupFocusHandling() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::requestFocus);
            }
        });
    }

    private void startGame() {
        startGameAtStage(1);
    }

    private void startGameAtStage(int stageIndex) {
        if (launcher != null) {
            if (stageIndex == 1) {
                launcher.switchToGameScreen();
            } else {
                launcher.switchToStage(stageIndex);
            }
            System.out.println("Switching to Stage " + stageIndex + "...");
        } else {
            System.out.println("Launcher is null!");
        }
    }

    public void setLauncher(se233.contra_project.Launcher launcher) {
        this.launcher = launcher;
    }

    private void repaint() {
        draw();
    }

    private void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // Draw stars
        gc.setFill(Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 600);
            int size = (int)(Math.random() * 2) + 1;
            gc.fillOval(x, y, size, size);
        }

        if (titleImage != null) {
            drawTitleImage();
        } else {
            drawFallbackTitleScreen();
        }

        drawPressStart();
    }

    private void drawTitleImage() {
        if (titleImage != null) {
            double scale = Math.min(800.0 / titleImage.getWidth(), 500.0 / titleImage.getHeight());
            double scaledWidth = titleImage.getWidth() * scale;
            double scaledHeight = titleImage.getHeight() * scale;
            double x = (800 - scaledWidth) / 2;
            double y = 20;
            gc.drawImage(titleImage, x, y, scaledWidth, scaledHeight);
        }
    }

    private void drawFallbackTitleScreen() {
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", 72));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("CONTRA", 400, 150);

        gc.setFill(Color.WHITE);
        gc.strokeRect(245, 85, 310, 80);

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 32));
        gc.fillText("PLAY SELECT", 400, 230);
    }

    private void drawPressStart() {
        if (showPressStart) {
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", 20));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("PRESS ENTER TO START", 400, 520);
        }
    }
}
