package se233.contra_project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameScreen extends JPanel {
    private HUDOverlay hudOverlay;
    private int playerLives = 4;
    private int playerScore = 0;

    public GameScreen() {
        setLayout(new BorderLayout()); // เปลี่ยนเป็น BorderLayout
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        // สร้างและเพิ่ม HUD
        setupHUD();

        //ทดสอบเฉยๆ
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    endGame(); // ไปหน้า Game Over
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    // กด A เพื่อทดสอบเพิ่มคะแนน
                    addScore(100);
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    // กด L เพื่อทดสอบเสียชีวิต
                    loseLife();
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
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

        // วาดข้อความทดสอบ
        g.setColor(Color.WHITE);
        g.setFont(FontManager.getContraFont(12f));
        g.drawString("GAME AREA - Press A to add score, L to lose life", 100, 350);
        g.drawString("Press ENTER to go to Game Over", 100, 380);
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