package se233.contra_project.ui;

import se233.contra_project.bosses.Boss;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Boss Health Bar with advanced visual feedback
 * Shows boss health, attack patterns, and special state indicators
 */
public class BossHealthBar {
    private static final double BAR_WIDTH = 400.0;
    private static final double BAR_HEIGHT = 20.0;
    private static final double PADDING = 10.0;
    private static final double ICON_SIZE = 25.0;
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Boss boss;
    private double x, y;
    private boolean visible;
    private double animationTimer;
    
    // Visual effects
    private double damageFlash;
    private double lowHealthPulse;
    
    public BossHealthBar(Canvas canvas, Boss boss) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.boss = boss;
        this.visible = false;
        this.animationTimer = 0;
        this.damageFlash = 0;
        this.lowHealthPulse = 0;
    }
    
    public void update(double deltaTime) {
        animationTimer += deltaTime;
        
        if (damageFlash > 0) {
            damageFlash -= deltaTime * 3;
        }
        
        if (lowHealthPulse > 0) {
            lowHealthPulse -= deltaTime * 2;
        }
        
        // Show boss health bar when boss is active
        visible = boss != null && !boss.isDefeated() && boss.getCurrentState() != Boss.BossState.DEAD;
        
        // Update state indicators based on boss type
        updateStateIndicators();
    }
    
    private void updateStateIndicators() {
        if (boss == null) return;
        
        // Trigger damage flash when health changes significantly
        double healthPercentage = (double) boss.getHealth() / boss.getMaxHealth();
        if (healthPercentage < 0.3 && lowHealthPulse <= 0) {
            lowHealthPulse = 1.0;
        }
    }
    
    public void draw() {
        if (!visible || boss == null) return;
        
        double bossCenterX = boss.getPosition().getX() + boss.getWidth() / 2;
        x = bossCenterX - BAR_WIDTH / 2;
        y = 20.0;
        
        // Draw background
        drawBackground();
        
        // Draw health bar
        drawHealthBar();
        
        // Draw state indicators
        drawStateIndicators();
        
        // Draw boss name and pattern info
        drawBossInfo();
        
        // Draw special effects
        drawSpecialEffects();
    }
    
    private void drawBackground() {
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.7);
        gc.fillRect(x - PADDING, y - PADDING, 
                   BAR_WIDTH + PADDING * 2, BAR_HEIGHT + PADDING * 2 + 40);
        gc.setGlobalAlpha(1.0);
        
        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(x - PADDING, y - PADDING, 
                     BAR_WIDTH + PADDING * 2, BAR_HEIGHT + PADDING * 2 + 40);
    }
    
    private void drawHealthBar() {
        double healthPercentage = (double) boss.getHealth() / boss.getMaxHealth();
        double currentBarWidth = BAR_WIDTH * healthPercentage;
        
        // Health bar background
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT);
        
        // Health bar fill with gradient effect
        Color healthColor;
        if (healthPercentage > 0.6) {
            healthColor = Color.GREEN;
        } else if (healthPercentage > 0.3) {
            healthColor = Color.ORANGE;
        } else {
            healthColor = Color.RED;
        }
        
        // Add pulsing effect for low health
        if (lowHealthPulse > 0) {
            double pulseIntensity = 0.3 + 0.7 * Math.abs(Math.sin(animationTimer * 8));
            healthColor = new Color(
                Math.min(1.0, healthColor.getRed() + pulseIntensity * 0.2),
                healthColor.getGreen(),
                healthColor.getBlue(),
                1.0
            );
        }
        
        gc.setFill(healthColor);
        gc.fillRect(x, y, currentBarWidth, BAR_HEIGHT);
        
        // Damage flash effect
        if (damageFlash > 0) {
            gc.setFill(Color.YELLOW);
            gc.setGlobalAlpha(damageFlash);
            gc.fillRect(x, y, currentBarWidth, BAR_HEIGHT);
            gc.setGlobalAlpha(1.0);
        }
        
        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, BAR_WIDTH, BAR_HEIGHT);
    }
    
    private void drawStateIndicators() {
        double iconY = y + BAR_HEIGHT + 15;
        double iconX = x;
        
        // Shield indicator
        // Simple indicators for boss stats
        gc.setFont(new Font(12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Score: " + boss.getScoreValue(), iconX, iconY + ICON_SIZE - 8);
    }
    
    private void drawBossInfo() {
        String bossName = getBossName();
        String bossPattern = getBossPattern();
        
        gc.setFont(new Font(14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        
        // Boss name
        gc.fillText(bossName, x + BAR_WIDTH / 2, y + BAR_HEIGHT + 15 + ICON_SIZE + 15);
        
        // Pattern info (if available)
        if (!bossPattern.isEmpty()) {
            gc.setFont(new Font(12));
            gc.setFill(Color.LIGHTGRAY);
            gc.fillText(bossPattern, x + BAR_WIDTH / 2, y + BAR_HEIGHT + 15 + ICON_SIZE + 30);
        }
    }
    
    private void drawSpecialEffects() {
        // Background effects
        if (damageFlash > 0) {
            gc.setFill(Color.YELLOW);
            gc.setGlobalAlpha(damageFlash * 0.3);
            gc.fillRect(x - PADDING, y - PADDING, 
                       BAR_WIDTH + PADDING * 2, BAR_HEIGHT + PADDING * 2 + 40);
            gc.setGlobalAlpha(1.0);
        }
        
        // Corner decorations
        double cornerSize = 8;
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        
        // Top corners
        gc.strokeLine(x - PADDING + cornerSize, y - PADDING, x - PADDING, y - PADDING);
        gc.strokeLine(x + BAR_WIDTH + PADDING, y - PADDING, x + BAR_WIDTH + PADDING - cornerSize, y - PADDING);
        
        // Bottom corners
        gc.strokeLine(x - PADDING + cornerSize, y + BAR_HEIGHT + PADDING + 40, 
                     x - PADDING, y + BAR_HEIGHT + PADDING + 40);
        gc.strokeLine(x + BAR_WIDTH + PADDING, y + BAR_HEIGHT + PADDING + 40, 
                     x + BAR_WIDTH + PADDING - cornerSize, y + BAR_HEIGHT + PADDING + 40);
    }
    
    private String getBossName() {
        if (boss instanceof se233.contra_project.bosses.Boss1) {
            return "BOSS 1: DEFENSE WALL";
        } else if (boss instanceof se233.contra_project.bosses.Boss2) {
            return "BOSS 2: JAVA CORE";
        } else if (boss instanceof se233.contra_project.bosses.Boss3) {
            return "BOSS 3: CODE DRAGON";
        }
        return "UNKNOWN BOSS";
    }
    
    private String getBossPattern() {
        if (boss == null) {
            return "";
        }
        return "Health: " + boss.getHealth() + " / " + boss.getMaxHealth();
    }
    
    public void triggerDamageFlash() {
        this.damageFlash = 1.0;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setBoss(Boss boss) {
        this.boss = boss;
    }
}
