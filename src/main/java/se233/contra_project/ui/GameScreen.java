package se233.contra_project.ui;

import se233.contra_project.actors.Projectile;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss1;
import se233.contra_project.bosses.Boss2;
import se233.contra_project.bosses.Boss3;
import se233.contra_project.game.systems.CollisionSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameScreen extends JPanel {
    private Boss currentBoss;
    private CollisionSystem collisionSystem;
    private HUDOverlay hudOverlay;
    private int playerLives = 4;
    private int playerScore = 0;
    private int currentBossIndex = 0;
    private Boss[] bosses;
    private boolean demoMode = true;

    public GameScreen() {
        setLayout(new BorderLayout()); // เปลี่ยนเป็น BorderLayout
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        // สร้างและเพิ่ม HUD
        setupHUD();

        // Initialize boss demo
        initializeBossDemo();

        //ทดสอบเฉยๆ
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        endGame(); // ไปหน้า Game Over
                        break;
                    case KeyEvent.VK_A:
                        // กด A เพื่อทดสอบเพิ่มคะแนน
                        addScore(100);
                        break;
                    case KeyEvent.VK_L:
                        // กด L เพื่อทดสอบเสียชีวิต
                        loseLife();
                        break;
                    case KeyEvent.VK_1:
                        switchToBoss(0);
                        break;
                    case KeyEvent.VK_2:
                        switchToBoss(1);
                        break;
                    case KeyEvent.VK_3:
                        switchToBoss(2);
                        break;
                    case KeyEvent.VK_R:
                        resetBossDemo();
                        break;
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void initializeBossDemo() {
        collisionSystem = new CollisionSystem();

        // Create all three bosses
        bosses = new Boss[3];
        bosses[0] = new Boss1(600, 200);
        bosses[1] = new Boss2(600, 250);
        bosses[2] = new Boss3(600, 150);

        // Start with Boss1
        currentBoss = bosses[0];
        collisionSystem.addBoss(currentBoss);

        Timer gameTimer = new Timer(16, e -> update());
        gameTimer.start();
    }

    private void switchToBoss(int index) {
        if (index >= 0 && index < bosses.length) {
            collisionSystem.removeBoss(currentBoss);
            currentBossIndex = index;
            currentBoss = bosses[index];
            collisionSystem.addBoss(currentBoss);
        }
    }

    private void resetBossDemo() {
        // Reset all bosses
        for (int i = 0; i < bosses.length; i++) {
            switch (i) {
                case 0:
                    bosses[i] = new Boss1(600, 200);
                    break;
                case 1:
                    bosses[i] = new Boss2(600, 250);
                    break;
                case 2:
                    bosses[i] = new Boss3(600, 150);
                    break;
            }
        }
        switchToBoss(0);
    }

    private void update() {
        if (currentBoss != null) {
            currentBoss.update(1.0/60.0);

            if (currentBoss.isDefeated()) {
                System.out.println("Boss " + (currentBossIndex + 1) + " defeated!");
                // Auto-switch to next boss in demo mode
                if (demoMode) {
                    int nextBoss = (currentBossIndex + 1) % bosses.length;
                    switchToBoss(nextBoss);
                }
            }
        }
        collisionSystem.update(1.0/60.0);
        repaint();
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

    private void loseLife() {
        if (playerLives > 0) {
            playerLives--;
            updateHUD();

            // ถ้าชีวิตหมด เกมโอเวอร์
            if (playerLives <= 0) {
                endGame();
            }
        }
    }

    private void gainLife() {
        playerLives++;
        updateHUD();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw demo background
        drawDemoBackground(g2d);

        // Draw boss
        if (currentBoss != null) {
            drawBoss(g2d, currentBoss);

            // Draw projectiles
            for (Projectile proj : currentBoss.getProjectiles()) {
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int)proj.getPosition().getX(),
                        (int)proj.getPosition().getY(),
                        (int)proj.getWidth(),
                        (int)proj.getHeight());
            }
        }

        // Draw demo info
        drawDemoInfo(g2d);
    }

    private void drawDemoBackground(Graphics2D g2d) {
        // Grid background
        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= 800; i += 50) {
            g2d.drawLine(i, 0, i, 600);
        }
        for (int i = 0; i <= 600; i += 50) {
            g2d.drawLine(0, i, 800, i);
        }
    }

    private void drawBoss(Graphics2D g2d, Boss boss) {
        Color bossColor;
        String bossName;

        if (boss instanceof Boss1) {
            bossColor = Color.RED;
            bossName = "BOSS 1: DEFENSE WALL";
        } else if (boss instanceof Boss2) {
            bossColor = Color.ORANGE;
            bossName = "BOSS 2: JAVA CORE";
        } else if (boss instanceof Boss3) {
            bossColor = Color.GREEN;
            bossName = "BOSS 3: CODE DRAGON";
        } else {
            bossColor = Color.GRAY;
            bossName = "UNKNOWN BOSS";
        }

        g2d.setColor(bossColor);
        g2d.fillRect((int)boss.getPosition().getX(),
                    (int)boss.getPosition().getY(),
                    (int)boss.getWidth(),
                    (int)boss.getHeight());

        // Draw boss name
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(bossName, (int)boss.getPosition().getX(),
                      (int)boss.getPosition().getY() - 5);
    }

    private void drawDemoInfo(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        // Demo title
        g2d.drawString("BOSS DEMO - Press 1,2,3 to switch bosses | R to reset", 20, 30);

        // Boss info
        if (currentBoss != null) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Health: " + currentBoss.getHealth() + "/" + currentBoss.getMaxHealth(), 20, 50);
            g2d.drawString("Projectiles: " + currentBoss.getProjectiles().size(), 20, 65);
            g2d.drawString("State: " + currentBoss.getCurrentState(), 20, 80);
        }

        // Controls
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Controls: A=Add Score, L=Lose Life, ENTER=Game Over", 20, 580);
    }



    private void endGame() {
        int finalScore = playerScore;
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        GameOverScreen gameOver = new GameOverScreen(finalScore);
        frame.setContentPane(gameOver);
        frame.revalidate();
        frame.repaint();
        SwingUtilities.invokeLater(gameOver::requestFocusInWindow);
    }
}