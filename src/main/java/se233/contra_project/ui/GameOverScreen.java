package se233.contra_project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GameOverScreen extends JPanel {

    private final int score;
    private final Star[] stars;
    private final Timer starTimer;

    private static class Star {
        int x, y, speed, size;
    }

    public GameOverScreen(int score) {
        this.score = score;

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        stars = createStars(60);
        starTimer = new Timer(40, e -> {
            moveStars();
            repaint();
        });
        starTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    backToStart();
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private Star[] createStars(int count) {
        Random rand = new Random();
        Star[] stars = new Star[count];
        for (int i = 0; i < count; i++) {
            Star s = new Star();
            s.x = rand.nextInt(800);
            s.y = rand.nextInt(600);
            s.size = rand.nextInt(2) + 1;
            s.speed = rand.nextInt(3) + 1;
            stars[i] = s;
        }
        return stars;
    }

    private void moveStars() {
        for (Star s : stars) {
            s.y += s.speed;
            if (s.y > 600) {
                s.y = 0;
                s.x = new Random().nextInt(800);
            }
        }
    }

    private void backToStart() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        StartScreen start = new StartScreen();
        frame.setContentPane(start);
        frame.revalidate();
        frame.repaint();
        SwingUtilities.invokeLater(start::requestFocusInWindow);
        starTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawStars(g);

        Graphics2D g2d = (Graphics2D) g;

        int panelWidth = getWidth();

        // GAME OVER
        g2d.setColor(Color.GRAY);
        g2d.setFont(FontManager.getFont(24f));
        drawCenteredString(g2d, "GAME OVER", panelWidth, 160);

        // 1P SCORE
        g2d.setFont(FontManager.getFont(18f));
        drawCenteredString(g2d, "1P SCORE", panelWidth, 250);

        // SCORE
        g2d.setFont(FontManager.getFont(18f));
        drawCenteredString(g2d, String.format("%08d", score), panelWidth, 290);

        // END
        g2d.setFont(FontManager.getFont(18f));
        drawCenteredString(g2d, "END", panelWidth, 390);
    }

    /** 🔹 เมธอดช่วยวาดข้อความให้อยู่ตรงกลางแนวนอน */
    private void drawCenteredString(Graphics2D g2d, String text, int panelWidth, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (panelWidth - textWidth) / 2;
        g2d.drawString(text, x, y);
    }

    private void drawStars(Graphics g) {
        g.setColor(Color.WHITE);
        for (Star s : stars) {
            g.fillOval(s.x, s.y, s.size, s.size);
        }
    }
}
