package se233.contra_project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class StartScreen extends JPanel {
    private Timer animationTimer;
    private boolean showPressStart = true;
    private Image titleImage;
    private int currentFrame = 0;
    private Image[] animatedFrames;
    private Timer frameTimer;

    public StartScreen() {
        setupUI();
        loadTitleImage();
        setupAnimations();
        setupKeyListener();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    private void loadTitleImage() {
        try {
            // ชื่อไฟล์ตามที่คุณส่งมา
            String filename = "Title Screens.png";

            // ลองหาไฟล์จากหลายๆ ที่
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
                    titleImage = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ ไม่พบไฟล์รูป, ใช้ File Chooser...");
                chooseFileManually();
            }

        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            createFallbackScreen();
        }
    }

    private void chooseFileManually() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("เลือกไฟล์ Contra Title Screen - NES - Contra - Miscellaneous - Title Screens.png");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            titleImage = Toolkit.getDefaultToolkit().getImage(selectedFile.getAbsolutePath());
            System.out.println("✅ เลือกไฟล์แล้ว: " + selectedFile.getAbsolutePath());
        } else {
            createFallbackScreen();
        }
    }

    private void createFallbackScreen() {
        titleImage = null;
        System.out.println("ใช้หน้าจอแบบวาดด้วย code");
    }

    private void setupAnimations() {
        // อนิเมชันข้อความกะพริบ
        animationTimer = new Timer(500, e -> {
            showPressStart = !showPressStart;
            repaint();
        });
        animationTimer.start();

        // อนิเมชันเฟรมรูป (ถ้ามีหลายเฟรม)
        frameTimer = new Timer(200, e -> {
            currentFrame = (currentFrame + 1) % 4; // วน 4 เฟรม
            repaint();
        });
        // frameTimer.start(); // เปิดถ้ารูปมีหลายเฟรม
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        startGame();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                    case KeyEvent.VK_F1:
                        chooseFileManually();
                        repaint();
                        break;
                }
            }
        });
    }

    private void startGame() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        GameScreen gameScreen = new GameScreen();

        frame.setContentPane(gameScreen);
        frame.revalidate();
        frame.repaint();

        SwingUtilities.invokeLater(gameScreen::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // เปิดการทำให้เรียบ
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // โหลดฟอนต์จาก FontManager
        Font bigFont = FontManager.getContraFont(44f);
        Font mediumFont = FontManager.getContraFont(36f);
        Font smallFont = FontManager.getContraFont(24f);

        drawBackground(g2d);

        if (titleImage != null) {
            drawTitleImage(g2d);
        } else {
            drawFallbackTitleScreen(g2d);
        }

        drawPressStart(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        // พื้นหลังสีดำแบบ Contra
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // เพิ่มดาวแบบ Contra
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int size = (int)(Math.random() * 2) + 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawTitleImage(Graphics2D g2d) {
        if (titleImage != null) {
            // วาดรูปเต็มหน้าจอ หรือ scale ให้พอดี
            int imgWidth = titleImage.getWidth(this);
            int imgHeight = titleImage.getHeight(this);

            if (imgWidth > 0 && imgHeight > 0) {
                // Scale รูปให้พอดีกับหน้าจอ
                double scale = Math.min(800.0 / imgWidth, 500.0 / imgHeight);
                int scaledWidth = (int)(imgWidth * scale);
                int scaledHeight = (int)(imgHeight * scale);

                int x = (getWidth() - scaledWidth) / 2;
                int y = 20;

                g2d.drawImage(titleImage, x, y, scaledWidth, scaledHeight, this);
            }
        }
    }

    //ถ้าหาไฟล์รูปไม่เจอ จะแทนด้วยข้อความพวกนี้
    private void drawFallbackTitleScreen(Graphics2D g2d) {
        // CONTRA Logo ใหญ่
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        g2d.drawString("CONTRA", 250, 150);

        // กรอบรอบ CONTRA
        g2d.setColor(Color.WHITE);
        g2d.drawRect(245, 85, 310, 80);

        // PLAY SELECT
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.drawString("PLAY SELECT", 280, 230);
    }


    private void drawPressStart(Graphics2D g2d) {
        if (showPressStart) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("PRESS ENTER TO START", 280, 520);
        }
    }

}