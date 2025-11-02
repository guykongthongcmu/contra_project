package se233.contra_project.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HUDOverlay extends JPanel {
    private int playerLives = 3;
    private int playerScore = 0;
    private Image lifeIcon; // รูปไอคอนชีวิต

    public HUDOverlay() {
        setupHUD();
        loadLifeIcon();
    }

    private void setupHUD() {
        setOpaque(false); // ทำให้พื้นหลังโปร่งใส
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 60));
    }

    private void loadLifeIcon() {
        try {
            // เปลี่ยนเป็นชื่อไฟล์รูปของคุณ
            String filename = "Life.png"; // หรือชื่อไฟล์ของคุณ
            String[] possiblePaths = {
                    filename,
                    "src/main/resources/" + filename,
                    "resources/" + filename,
                    "images/" + filename,
                    "src/main/resources/se233/contra_project/sprites/" + filename,
                    "src/main/resources/se233/contra_project/ui/" + filename
            };

            boolean found = false;
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    lifeIcon = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("❌ ไม่พบไฟล์รูปไอคอนชีวิต");
            }
        } catch (Exception e) {
            System.out.println("❌ โหลดไอคอนชีวิตไม่สำเร็จ: " + e.getMessage());
        }
    }

    public void updateHUD(int lives, int score) {
        this.playerLives = lives;
        this.playerScore = score;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // เปิดการทำให้เรียบ
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        // วาดชีวิตผู้เล่น (ซ้าย)
        drawPlayerLives(g2d);

        // วาดคะแนน (ขวา)
        drawPlayerScore(g2d);
    }

    private void drawPlayerLives(Graphics2D g2d) {
        // วาดไอคอนชีวิต
        drawLifeIcons(g2d, 50, 25);
    }

    private void drawLifeIcons(Graphics2D g2d, int startX, int y) {
        for (int i = 0; i < playerLives; i++) {
            if (lifeIcon != null) {
                // ใช้รูปภาพ (ขยายจาก 8x16 เป็น 16x32 เพื่อให้เห็นชัด)
                g2d.drawImage(lifeIcon, startX + i * 25, y, 16, 32, this);
            }
        }
    }


    private void drawPlayerScore(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(FontManager.getContraFont(16f));
        g2d.drawString("SCORE", 530, 50);

        // วาดคะแนน (8 หลัก)
        g2d.setColor(Color.GRAY);
        g2d.setFont(FontManager.getContraFont(18f));
        String scoreText = String.format("%08d", playerScore);
        g2d.drawString(scoreText, 630, 50);
    }

    // Getter และ Setter
    public int getPlayerLives() { return playerLives; }
    public void setPlayerLives(int playerLives) { this.playerLives = playerLives; repaint(); }

    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; repaint(); }

    public void addScore(int points) {
        this.playerScore += points;
        repaint();
    }

    public void loseLife() {
        if (playerLives > 0) {
            this.playerLives--;
            repaint();
        }
    }

    public void gainLife() {
        this.playerLives++;
        repaint();
    }
}