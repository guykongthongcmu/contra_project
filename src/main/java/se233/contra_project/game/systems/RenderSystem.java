package se233.contra_project.game.systems;

import javafx.embed.swing.SwingFXUtils;
import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.components.SpriteAnimation;
import se233.contra_project.game.Game;
import se233.contra_project.bosses.Boss;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Handles rendering of game entities and sprites
 */
public class RenderSystem {
    private Game game;
    private Graphics2D graphics;

    public RenderSystem() {
        // Constructor for standalone use
    }

    public RenderSystem(Game game) {
        this.game = game;
    }

    /**
     * Render all game entities
     */
    public void render(Graphics2D g2d, List<Entity> entities) {
        this.graphics = g2d;

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Render all entities
        for (Entity entity : entities) {
            if (entity.isAlive()) {
                renderEntity(entity);
            }
        }
    }

    /**
     * Render a single entity
     */
    private void renderEntity(Entity entity) {
        double x = entity.getPosition().getX();
        double y = entity.getPosition().getY();
        double width = entity.getWidth();
        double height = entity.getHeight();

        // Check if this is a Player entity (basic type checking)
        if (entity.getClass().getSimpleName().equals("Player")) {
            // Render player sprite - extract a specific frame from Characters1.png
            // Characters1.png has 64x64 pixel frames with 65x65 pitch (64px + 1px divider)
            // Make character bigger on screen by scaling up the rendering size
            // Render at 128x128 pixels instead of 64x64 for better visibility
            renderSpriteFromImage("sprites/Characters1.png", x, y, width * 2, height * 2, 0, 0, 64, 64, new Color(0, 162, 232)); // Contra blue background
        } else if (entity.getClass().getSimpleName().equals("Bullet")) {
            // Render bullet as small yellow rectangle
            graphics.setColor(Color.YELLOW);
            graphics.fillRect((int)x, (int)y, (int)width, (int)height);
        } else if (entity.getClass().getSimpleName().equals("Projectile")) {
            // Render enemy projectile as small red rectangle
            graphics.setColor(Color.RED);
            graphics.fillRect((int)x, (int)y, (int)width, (int)height);
        } else if (entity instanceof Boss) {
            Sprite bossSprite = ((Boss) entity).getSprite();
            if (bossSprite != null && bossSprite.getImage() != null) {
                renderSpriteImage(bossSprite, x, y);
            } else {
                String bossImage = getBossImage(entity.getClass().getSimpleName());
                renderSpriteFromImage(bossImage, x, y, width, height);
            }
        } else {
            // Default rendering for unknown entities
            graphics.setColor(Color.GREEN);
            graphics.fillRect((int)x, (int)y, (int)width, (int)height);
        }

        // Draw entity bounds for debugging (only in debug mode)
        graphics.setColor(Color.WHITE);
        graphics.drawRect((int)x, (int)y, (int)width, (int)height);
    }

    /**
     * Get the appropriate boss image path based on boss type
     */
    private String getBossImage(String bossClassName) {
        switch (bossClassName) {
            case "Boss1":
                return "sprites/Bosses1DefenseWall.png";
            case "Boss2":
                return "sprites/Bosses2Java.png";
            case "Boss3":
                return "sprites/Enemies&Obstacles.png"; // Placeholder for Boss3
            default:
                return "sprites/Enemies&Obstacles.png";
        }
    }

    /**
     * Render a sprite component
     */
    public void renderSprite(Sprite sprite, double x, double y) {
        if (sprite == null || graphics == null) return;

        // Update sprite position
        sprite.setPosition(x, y);

        // Get the ImageView from sprite and draw it
        // Note: This requires integrating JavaFX ImageView with Swing Graphics2D
        // For now, we'll use placeholder rendering
        graphics.setColor(Color.BLUE);
        graphics.fillRect((int)x, (int)y, (int)sprite.getWidth(), (int)sprite.getHeight());
    }

    /**
     * Render a sprite using actual image from resources
     */
    public void renderSpriteFromImage(String imagePath, double x, double y, double width, double height) {
        renderSpriteFromImage(imagePath, x, y, width, height, 0, 0, -1, -1);
    }

    /**
     * Render a specific frame from a sprite sheet
     */
    public void renderSpriteFromImage(String imagePath, double x, double y, double width, double height,
                                      int frameX, int frameY, int frameWidth, int frameHeight) {
        renderSpriteFromImage(imagePath, x, y, width, height, frameX, frameY, frameWidth, frameHeight, null);
    }

    /**
     * Render a sprite with optional transparent color
     */
    public void renderSpriteFromImage(String imagePath, double x, double y, double width, double height,
                                      int frameX, int frameY, int frameWidth, int frameHeight, Color transparentColor) {
        if (graphics == null) return;

        try {
            // Try to load image from resources
            File imageFile = new File("src/main/resources/se233/contra_project/" + imagePath);
            if (imageFile.exists()) {
                BufferedImage spriteSheet = ImageIO.read(imageFile);

                if (frameWidth > 0 && frameHeight > 0) {
                    // Extract specific frame from sprite sheet
                    BufferedImage frame = spriteSheet.getSubimage(frameX, frameY, frameWidth, frameHeight);

                    // Make background transparent if color specified
                    if (transparentColor != null) {
                        frame = makeColorTransparent(frame, transparentColor);
                    }

                    graphics.drawImage(frame, (int)x, (int)y, (int)width, (int)height, null);
                } else {
                    // Draw entire image
                    graphics.drawImage(spriteSheet, (int)x, (int)y, (int)width, (int)height, null);
                }
            } else {
                // Fallback to colored rectangle
                graphics.setColor(Color.MAGENTA);
                graphics.fillRect((int)x, (int)y, (int)width, (int)height);
            }
        } catch (Exception e) {
            // Fallback to colored rectangle
            graphics.setColor(Color.RED);
            graphics.fillRect((int)x, (int)y, (int)width, (int)height);
        }
    }

    private void renderSpriteImage(Sprite sprite, double x, double y) {
        if (graphics == null || sprite.getImage() == null) {
            return;
        }

        BufferedImage frame = SwingFXUtils.fromFXImage(sprite.getImage(), null);
        graphics.drawImage(frame, (int) x, (int) y, (int) sprite.getWidth(), (int) sprite.getHeight(), null);
    }

    /**
     * Make a specific color transparent in a BufferedImage
     */
    private BufferedImage makeColorTransparent(BufferedImage image, Color transparentColor) {
        BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color pixelColor = new Color(rgb, true);

                // Check if pixel matches the transparent color (with some tolerance)
                if (colorsMatch(pixelColor, transparentColor, 10)) {
                    // Make transparent
                    transparentImage.setRGB(x, y, 0x00000000);
                } else {
                    // Keep original color
                    transparentImage.setRGB(x, y, rgb);
                }
            }
        }

        return transparentImage;
    }

    /**
     * Check if two colors match within a tolerance
     */
    private boolean colorsMatch(Color c1, Color c2, int tolerance) {
        return Math.abs(c1.getRed() - c2.getRed()) <= tolerance &&
                Math.abs(c1.getGreen() - c2.getGreen()) <= tolerance &&
                Math.abs(c1.getBlue() - c2.getBlue()) <= tolerance;
    }

    /**
     * Render a sprite animation
     */
    public void renderSpriteAnimation(SpriteAnimation animation, double x, double y) {
        if (animation == null || graphics == null) return;

        // Get current frame and render it
        // This also requires JavaFX integration
        graphics.setColor(Color.RED);
        graphics.fillRect((int)x, (int)y, 32, 32); // Placeholder size
    }

    /**
     * Render background
     */
    public void renderBackground(Graphics2D g2d, int width, int height) {
        // Default Contra-style background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Add stars
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int)(Math.random() * width);
            int y = (int)(Math.random() * height);
            int size = (int)(Math.random() * 2) + 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    /**
     * Render level-specific background
     */
    public void renderLevelBackground(Graphics2D g2d, int level, int width, int height) {
        switch (level) {
            case 1:
                renderBoss1Background(g2d, width, height);
                break;
            case 2:
                renderBoss2Background(g2d, width, height);
                break;
            case 3:
                renderBoss3Background(g2d, width, height);
                break;
            default:
                renderBackground(g2d, width, height);
        }
    }

    private void renderBoss1Background(Graphics2D g2d, int width, int height) {
        // Boss 1 - Defense Wall background
        g2d.setColor(new Color(20, 20, 40)); // Dark blue-gray
        g2d.fillRect(0, 0, width, height);

        // Add some geometric patterns
        g2d.setColor(new Color(60, 60, 80));
        for (int i = 0; i < width; i += 50) {
            g2d.drawLine(i, 0, i, height);
        }
    }

    private void renderBoss2Background(Graphics2D g2d, int width, int height) {
        // Boss 2 - Java background
        g2d.setColor(new Color(40, 20, 20)); // Dark red-brown
        g2d.fillRect(0, 0, width, height);

        // Add circuit-like patterns
        g2d.setColor(new Color(80, 60, 60));
        for (int i = 0; i < 20; i++) {
            int x1 = (int)(Math.random() * width);
            int y1 = (int)(Math.random() * height);
            int x2 = (int)(Math.random() * width);
            int y2 = (int)(Math.random() * height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void renderBoss3Background(Graphics2D g2d, int width, int height) {
        // Boss 3 - Custom background
        g2d.setColor(new Color(20, 40, 20)); // Dark green
        g2d.fillRect(0, 0, width, height);

        // Add custom patterns
        g2d.setColor(new Color(60, 80, 60));
        for (int i = 0; i < 50; i++) {
            int x = (int)(Math.random() * width);
            int y = (int)(Math.random() * height);
            int size = (int)(Math.random() * 20) + 5;
            g2d.fillOval(x, y, size, size);
        }
    }

    /**
     * Convert JavaFX Image to BufferedImage for Swing rendering
     * This is a utility method for future JavaFX integration
     */
    public static BufferedImage javafxImageToBufferedImage(javafx.scene.image.Image fxImage) {
        if (fxImage == null) return null;

        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // TODO: Implement actual conversion when JavaFX integration is complete

        return bufferedImage;
    }
}
