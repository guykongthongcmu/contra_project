package se233.contra_project.ui;

import se233.contra_project.game.Game;
import se233.contra_project.game.input.InputHandler;
import se233.contra_project.game.systems.RenderSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GameScreen extends JPanel {
    private HUDOverlay hudOverlay;
    private Game game;
    private InputHandler inputHandler;
    private RenderSystem renderSystem;

    private int playerLives = 3; // Start with 3 lives as per Contra
    private int playerScore = 0;
    private int currentLevel = 1;

    // Background images
    private BufferedImage backgroundImage;
    private BufferedImage stage1Image;

    public GameScreen() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Load background images
        loadBackgroundImages();

        // Initialize game systems
        this.inputHandler = new InputHandler();
        this.renderSystem = new RenderSystem();
        this.game = new Game(this);

        // สร้างและเพิ่ม HUD
        setupHUD();

        // Set up input handling
        setupInputHandling();

        SwingUtilities.invokeLater(this::requestFocusInWindow);

        // Start the game loop in a separate thread
        startGameLoop();
    }

    private void setupInputHandling() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                inputHandler.keyPressed(e.getKeyCode());

                // Handle special keys for testing/debugging
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        endGame(); // ไปหน้า Game Over
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    // กด A เพื่อทดสอบเพิ่มคะแนน
                    addScore(100);
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    // กด L เพื่อทดสอบเสียชีวิต
                    loseLife();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                inputHandler.keyReleased(e.getKeyCode());
            }
        });
    }

    private void startGameLoop() {
        // Start game in a separate thread to avoid blocking EDT
        Thread gameThread = new Thread(() -> {
            game.start();
        });
        gameThread.setDaemon(true); // Allow JVM to exit even if thread is running
        gameThread.start();
    }

    private void loadBackgroundImages() {
        try {
            // Load Stage1.png for level backgrounds
            File stage1File = new File("src/main/resources/se233/contra_project/ui/Stage1.png");
            if (stage1File.exists()) {
                stage1Image = ImageIO.read(stage1File);
            }

            // You can add more background images here as needed
            // backgroundImage = ImageIO.read(new File("path/to/background.png"));

        } catch (Exception e) {
            System.out.println("Error loading background images: " + e.getMessage());
            // Continue without background images - fallback to colored backgrounds
        }
    }

    private void setupHUD() {
        hudOverlay = new HUDOverlay();
        add(hudOverlay, BorderLayout.NORTH); // วาง HUD ด้านบน
        updateHUD();
    }

    private void updateHUD() {
        if (hudOverlay != null) {
            hudOverlay.updateHUD(playerLives, playerScore);
        }
    }

    private void addScore(int points) {
        playerScore += points;
        updateHUD();
    }

    private void gainLife() {
        playerLives++;
        updateHUD();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Render background - try to use actual image first, fallback to colored background
        if (stage1Image != null && currentLevel == 1) {
            // Draw the Stage1.png background properly - scale to fit without distortion
            int imgWidth = stage1Image.getWidth();
            int imgHeight = stage1Image.getHeight();
            int screenWidth = getWidth();
            int screenHeight = getHeight();

            // Calculate scaling to fit the screen while maintaining aspect ratio
            double scaleX = (double) screenWidth / imgWidth;
            double scaleY = (double) screenHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY); // Use the larger scale to cover the screen

            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);

            // Center the image
            int x = (screenWidth - scaledWidth) / 2;
            int y = (screenHeight - scaledHeight) / 2;

            g2d.drawImage(stage1Image, x, y, scaledWidth, scaledHeight, this);
        } else {
            // Fallback to colored background
            renderSystem.renderLevelBackground(g2d, currentLevel, getWidth(), getHeight());
        }

        // Render game entities if game is initialized
        if (game != null) {
            renderSystem.render(g2d, game.getEntities());
        }

        // วาดข้อความ debug (จะลบออกเมื่อ sprite rendering เสร็จ)
        g2d.setColor(Color.WHITE);
        g2d.setFont(FontManager.getContraFont(12f));
        g2d.drawString("GAME AREA - Use WASD/Arrows to move, SPACE to shoot", 50, 350);
        g2d.drawString("Press ENTER to go to Game Over (debug)", 50, 380);

        // Display current game state
        g2d.drawString("Lives: " + playerLives + " Score: " + playerScore + " Level: " + currentLevel, 50, 400);
    }


    private void endGame() {
        // Stop the game loop
        if (game != null) {
            game.stop();
        }

        int finalScore = playerScore;
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        GameOverScreen gameOver = new GameOverScreen(finalScore);
        frame.setContentPane(gameOver);
        frame.revalidate();
        frame.repaint();
        SwingUtilities.invokeLater(gameOver::requestFocusInWindow);
    }

    // Public methods for game integration
    public void updateScore(int points) {
        playerScore += points;
        updateHUD();
    }

    public void loseLife() {
        if (playerLives > 0) {
            playerLives--;
            updateHUD();

            // ถ้าชีวิตหมด เกมโอเวอร์
            if (playerLives <= 0) {
                endGame();
            }
        }
    }

    public void nextLevel() {
        currentLevel++;
        // TODO: Load next boss
    }

    // Getters for game integration
    public Game getGame() { return game; }
    public InputHandler getInputHandler() { return inputHandler; }
    public RenderSystem getRenderSystem() { return renderSystem; }
}