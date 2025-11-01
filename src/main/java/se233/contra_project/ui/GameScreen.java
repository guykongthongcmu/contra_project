package se233.contra_project.ui;

import se233.contra_project.bosses.Boss1;
import se233.contra_project.bosses.Boss2;
import se233.contra_project.bosses.Boss3;
import se233.contra_project.ui.BossHealthBar;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;

/**
 * Game Screen - Stage 1 with Boss1
 * Displays Stage 1 background and Boss1 for gameplay
 */
public class GameScreen extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private Boss1 boss1;
    private BossHealthBar healthBar;
    private javafx.scene.image.Image stage1Background;

    // Stage 1 settings
    private static final int CENTER_X = 400;
    private static final int CENTER_Y = 300;

    // Game controls
    private boolean showHealthBar = true;
    private boolean showInfo = true;

    public GameScreen() {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Load Stage 1 background
        loadStage1Background();

        // Initialize Boss1 for Stage 1
        initializeBoss1();

        // Initialize health bar
        healthBar = new BossHealthBar(canvas, boss1);

        // Set up input handling for game
        setupGameInputHandling();

        // Start animation timer
        startAnimationTimer();
    }

    private void loadStage1Background() {
        try {
            String backgroundPath = "/se233/contra_project/ui/Stage1.png";
            stage1Background = new javafx.scene.image.Image(getClass().getResourceAsStream(backgroundPath));
            System.out.println("Stage 1 background loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load Stage 1 background: " + e.getMessage());
            stage1Background = null;
        }
    }

    private void initializeBoss1() {
        // Create Boss1 at center position for Stage 1
        boss1 = new Boss1(0, 0);
        positionBoss1();
    }

    private void setupGameInputHandling() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case H:
                    showHealthBar = !showHealthBar;
                    break;
                case I:
                    showInfo = !showInfo;
                    break;
                case R:
                    resetBoss1();
                    break;
                case SPACE:
                    damageBoss1(5); // Test damage
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
            }
            draw();
        });
        setFocusTraversable(true);
        requestFocus();
    }

    private void resetBoss1() {
        boss1 = new Boss1(0, 0);
        positionBoss1();
        healthBar.setBoss(boss1);
        System.out.println("Reset Boss1");
        draw();
    }

    private void damageBoss1(int damage) {
        boss1.takeDamage(damage);
        healthBar.triggerDamageFlash();
        System.out.println("Damaged Boss1 for " + damage + " HP");
    }

    private void draw() {
        // Draw Stage 1 background
        drawStage1Background();

        // Draw current boss sprite
        drawCurrentBoss();

        // Draw health bar if enabled
        if (showHealthBar) {
            healthBar.draw();
        }

        // Draw info panel
        if (showInfo) {
            drawInfoPanel();
        }

        // Draw controls help
        drawControlsHelp();
    }

    private void drawStage1Background() {
        if (stage1Background != null) {
            double destWidth = canvas.getWidth();
            double destHeight = canvas.getHeight();
            double imgWidth = stage1Background.getWidth();
            double imgHeight = stage1Background.getHeight();

            double srcWidth = Math.min(destWidth, imgWidth);
            double srcHeight = Math.min(destHeight, imgHeight);

            double srcX = Math.max(0, imgWidth - srcWidth);          // focus on far-right side
            double srcY = Math.max(0, (imgHeight - srcHeight) / 2);  // center vertically if taller

            gc.drawImage(stage1Background, srcX, srcY, srcWidth, srcHeight,
                    0, 0, destWidth, destHeight);
        } else {
            // Fallback: draw dark background
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(0, 0, 800, 600);
        }
    }

    private void positionBoss1() {
        if (boss1 == null) {
            return;
        }
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double bossWidth = boss1.getWidth();
        double bossHeight = boss1.getHeight();

        double marginRight = 20;
        double startX = Math.max(marginRight, canvasWidth - bossWidth - marginRight);

        double groundOffset = 140; // aligns boss with foreground platform
        double startY = canvasHeight - bossHeight - groundOffset;
        if (startY < 0) {
            startY = 0;
        }

        boss1.setPosition(startX, startY);
    }

    private void drawGrid() {
        gc.setStroke(Color.color(0.25, 0.25, 0.25, 0.5));
        gc.setLineWidth(1);

        // Vertical lines
        for (int x = 0; x < 800; x += 50) {
            gc.strokeLine(x, 0, x, 600);
        }

        // Horizontal lines
        for (int y = 0; y < 600; y += 50) {
            gc.strokeLine(0, y, 800, y);
        }

        // Center cross
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(CENTER_X - 20, CENTER_Y, CENTER_X + 20, CENTER_Y);
        gc.strokeLine(CENTER_X, CENTER_Y - 20, CENTER_X, CENTER_Y + 20);
    }

    private void drawCurrentBoss() {
        if (boss1 == null) return;

        try {
            // Draw boss sprite if available
            if (boss1.getSprite() != null) {
                double x = boss1.getPosition().getX();
                double y = boss1.getPosition().getY();
                double width = boss1.getWidth();
                double height = boss1.getHeight();

                // Draw sprite using JavaFX ImageView
                gc.drawImage(boss1.getSprite().getImage(), x, y, width, height);
            } else {
                // Fallback: draw colored rectangle
                gc.setFill(Color.BLUE);
                gc.fillRect(boss1.getPosition().getX(),
                           boss1.getPosition().getY(),
                           boss1.getWidth(),
                           boss1.getHeight());

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeRect(boss1.getPosition().getX(),
                             boss1.getPosition().getY(),
                             boss1.getWidth(),
                             boss1.getHeight());
            }

            // Draw boss bounding box
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(2);
            gc.strokeRect(boss1.getPosition().getX(),
                         boss1.getPosition().getY(),
                         boss1.getWidth(),
                         boss1.getHeight());

        } catch (Exception e) {
            System.err.println("Error drawing boss: " + e.getMessage());
        }
    }

    private Color getBossColorFX(int index) {
        switch (index) {
            case 0: return Color.BLUE;    // Boss1 - Defense Wall
            case 1: return Color.ORANGE;  // Boss2 - Java
            case 2: return Color.GREEN;   // Boss3 - Code Dragon
            default: return Color.GRAY;
        }
    }

    private void drawInfoPanel() {
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(10, 10, 300, 120);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));

        double y = 30;
        gc.fillText("Stage 1 - Boss Fight", 20, y);
        y += 20;

        gc.fillText("Boss: Boss1 - Defense Wall", 20, y);
        y += 20;

        if (boss1 != null) {
            gc.fillText("Health: " + boss1.getHealth() + "/" + boss1.getMaxHealth(), 20, y);
            y += 20;

            gc.fillText("State: " + boss1.getCurrentState(), 20, y);
            y += 20;

            gc.fillText("Sprite: " + (boss1.getSprite() != null ? "Loaded" : "Not Loaded"), 20, y);
            y += 15;
            gc.fillText("Sprite Path: Bosses1DefenseWall.png", 20, y);
        }
    }

    private void drawControlsHelp() {
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(10, 600 - 150, 350, 140);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));

        double y = 600 - 130;
        gc.fillText("Stage 1 Controls:", 20, y);
        y += 15;
        gc.fillText("H - Toggle Health Bar", 20, y);
        y += 15;
        gc.fillText("I - Toggle Info Panel", 20, y);
        y += 15;
        gc.fillText("R - Reset Boss1", 20, y);
        y += 15;
        gc.fillText("SPACE - Damage Boss1 (5 HP)", 20, y);
        y += 15;
        gc.fillText("ESC - Exit Game", 20, y);
    }

    private String getBossName(int index) {
        switch (index) {
            case 0: return "Boss1 - Defense Wall";
            case 1: return "Boss2 - Java";
            case 2: return "Boss3 - Code Dragon";
            default: return "Unknown Boss";
        }
    }

    private String getSpritePath(int index) {
        switch (index) {
            case 0: return "Bosses1DefenseWall.png";
            case 1: return "Bosses2Java.png";
            case 2: return "Boss3.png";
            default: return "Unknown";
        }
    }

    private void startAnimationTimer() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0; // Convert to seconds
                lastTime = now;

                // Update boss1
                if (boss1 != null) {
                    boss1.update(deltaTime);
                }

                // Update health bar
                healthBar.update(deltaTime);

                // Redraw
                draw();
            }
        };
        timer.start();
    }

    // Getters for external access
    public Boss1 getBoss1() { return boss1; }
}
