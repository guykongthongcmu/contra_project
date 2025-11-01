package se233.contra_project.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import se233.contra_project.bosses.*;
import se233.contra_project.actors.Player;
import se233.contra_project.game.systems.CollisionSystem;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Boss Demo Screen for testing and demonstrating all enhanced boss mechanics
 * Shows off all three bosses with their unique attack patterns and AI behaviors
 */
public class BossDemoScreen extends Parent {
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    
    private CollisionSystem collisionSystem;
    private Boss currentBoss;
    private Player testPlayer;
    private double timeElapsed;
    private boolean demoActive;
    
    // Demo controls
    private boolean showDebugInfo = true;
    private boolean slowMotion = false;
    private boolean autoBattle = false;
    
    // Boss demonstration states
    private int currentBossIndex = 0;
    private Boss[] bosses = new Boss[3];
    private double bossSwitchTimer = 0;
    private static final double BOSS_DEMO_DURATION = 15.0; // 15 seconds per boss

    public BossDemoScreen(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.collisionSystem = new CollisionSystem();
        this.timeElapsed = 0;
        this.demoActive = false;
        
        initializeDemo();
        setupControls();
    }
    
    private void initializeDemo() {
        // Create test player
        testPlayer = new Player(100, 300);
        testPlayer.setVelocity(0, 0);
        collisionSystem.setPlayer(testPlayer);
        
        // Initialize bosses
        bosses[0] = new Boss1(600, 200);
        bosses[1] = new Boss2(600, 250);
        bosses[2] = new Boss3(600, 150);
        
        // Set initial boss
        currentBoss = bosses[currentBossIndex];
        collisionSystem.addBoss(currentBoss);
    }
    
    private void setupControls() {
        canvas.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case SPACE:
                    toggleDemo();
                    break;
                case D:
                    showDebugInfo = !showDebugInfo;
                    break;
                case S:
                    slowMotion = !slowMotion;
                    break;
                case A:
                    autoBattle = !autoBattle;
                    break;
                case DIGIT1:
                    switchToBoss(0);
                    break;
                case DIGIT2:
                    switchToBoss(1);
                    break;
                case DIGIT3:
                    switchToBoss(2);
                    break;
                case R:
                    resetDemo();
                    break;
                default:
                    break;
            }
        });
    }
    
    private void toggleDemo() {
        demoActive = !demoActive;
        if (demoActive) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }
    
    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        animationTimer.start();
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    private void update() {
        double deltaTime = 1.0 / 60.0;
        if (slowMotion) {
            deltaTime *= 0.3;
        }
        
        timeElapsed += deltaTime;
        bossSwitchTimer += deltaTime;
        
        // Auto-switch bosses
        if (bossSwitchTimer >= BOSS_DEMO_DURATION && !autoBattle) {
            nextBoss();
        }
        
        // Update entities
        if (autoBattle) {
            updateAutoPlayer();
        }
        
        updatePlayerMovement();
        updateCurrentBoss();
        collisionSystem.update(deltaTime);
        
        // Check if boss is defeated and switch to next
        if (currentBoss != null && currentBoss.isDefeated()) {
            bossSwitchTimer = 0;
            if (!autoBattle) {
                nextBoss();
            }
        }
    }
    
    private void updateAutoPlayer() {
        // Simple AI for demonstration
        if (currentBoss != null && !currentBoss.isDefeated()) {
            double playerX = testPlayer.getPosition().getX();
            double playerY = testPlayer.getPosition().getY();
            double bossX = currentBoss.getPosition().getX();
            double bossY = currentBoss.getPosition().getY();
            
            // Move towards boss
            double dx = bossX - playerX;
            double dy = bossY - playerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance > 100) {
                double speed = 100;
                testPlayer.setVelocity((dx / distance) * speed, (dy / distance) * speed);
            } else {
                testPlayer.setVelocity(0, 0);
            }
        }
    }
    
    private void updatePlayerMovement() {
        // Basic player movement for manual control
        double speed = 200;
        double dx = 0, dy = 0;
        
        if (canvas.isFocused()) {
            // Check arrow keys (implementation depends on input system)
            // For demo, we'll simulate some movement
            dx = Math.sin(timeElapsed * 2) * 50 * 1/60.0;
            dy = Math.cos(timeElapsed * 3) * 30 * 1/60.0;
        }
        
        testPlayer.setPosition(
            Math.max(0, Math.min(800 - testPlayer.getWidth(), testPlayer.getPosition().getX() + dx)),
            Math.max(0, Math.min(600 - testPlayer.getHeight(), testPlayer.getPosition().getY() + dy))
        );
        testPlayer.setVelocity(dx / (1/60.0), dy / (1/60.0));
    }
    
    private void updateCurrentBoss() {
        if (currentBoss != null) {
            currentBoss.update(1.0 / 60.0);
            
            // Add boss projectiles to collision system
            for (se233.contra_project.actors.Projectile projectile : currentBoss.getProjectiles()) {
                collisionSystem.addBossProjectile(projectile);
            }
        }
    }
    
    private void switchToBoss(int index) {
        if (index >= 0 && index < bosses.length) {
            if (currentBoss != null) {
                collisionSystem.removeBoss(currentBoss);
            }
            currentBossIndex = index;
            currentBoss = bosses[currentBossIndex];
            collisionSystem.addBoss(currentBoss);
            bossSwitchTimer = 0;
        }
    }
    
    private void nextBoss() {
        switchToBoss((currentBossIndex + 1) % bosses.length);
    }
    
    private void resetDemo() {
        // Reset all bosses
        for (int i = 0; i < bosses.length; i++) {
            bosses[i] = createNewBoss(i);
        }
        switchToBoss(0);
        timeElapsed = 0;
        bossSwitchTimer = 0;
    }
    
    private Boss createNewBoss(int index) {
        switch (index) {
            case 0:
                return new Boss1(600, 200);
            case 1:
                return new Boss2(600, 250);
            case 2:
                return new Boss3(600, 150);
            default:
                return null;
        }
    }
    
    private void render() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw demo background
        drawDemoBackground();
        
        // Draw entities
        if (currentBoss != null) {
            drawBoss(currentBoss);
        }
        
        drawPlayer(testPlayer);
        
        // Draw projectiles
        drawProjectiles();
        
        // Draw UI
        drawUI();
        
        // Draw debug information
        if (showDebugInfo) {
            drawDebugInfo();
        }
        
        // Draw visual effects
        drawVisualEffects();
    }
    
    private void drawDemoBackground() {
        // Grid background
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        for (int i = 0; i <= 800; i += 50) {
            gc.strokeLine(i, 0, i, 600);
        }
        for (int i = 0; i <= 600; i += 50) {
            gc.strokeLine(0, i, 800, i);
        }
        
        // Demo title
        gc.setFont(new Font(24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.CYAN);
        gc.fillText("BOSS AI & ATTACK PATTERNS DEMO", 400, 30);
        
        if (!demoActive) {
            gc.setFont(new Font(16));
            gc.setFill(Color.WHITE);
            gc.fillText("Press SPACE to start demo", 400, 60);
        }
    }
    
    private void drawBoss(Boss boss) {
        if (boss instanceof Boss1) {
            drawBoss1((Boss1) boss);
        } else if (boss instanceof Boss2) {
            drawBoss2((Boss2) boss);
        } else if (boss instanceof Boss3) {
            drawBoss3((Boss3) boss);
        }
    }
    
    private void drawBoss1(Boss1 boss) {
        gc.setFill(Color.RED);
        gc.fillRect(boss.getPosition().getX(), boss.getPosition().getY(), 
                   boss.getWidth(), boss.getHeight());
    }
    
    private void drawBoss2(Boss2 boss) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(boss.getPosition().getX(), boss.getPosition().getY(), 
                   boss.getWidth(), boss.getHeight());
    }
    
    private void drawBoss3(Boss3 boss) {
        gc.setFill(Color.GREEN);
        gc.fillRect(boss.getPosition().getX(), boss.getPosition().getY(), 
                   boss.getWidth(), boss.getHeight());
    }
    
    private void drawPlayer(Player player) {
        gc.setFill(Color.CYAN);
        gc.fillRect(player.getPosition().getX(), player.getPosition().getY(), 
                   player.getWidth(), player.getHeight());
        
        gc.setFont(new Font(10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Lives: " + player.getLives(), player.getPosition().getX(), player.getPosition().getY() - 4);
    }
    
    private void drawProjectiles() {
        if (currentBoss == null) {
            return;
        }

        for (se233.contra_project.actors.Projectile projectile : currentBoss.getProjectiles()) {
            if (!projectile.isAlive()) {
                continue;
            }
            Color projColor = getProjectileColor(projectile.getType());
            gc.setFill(projColor);
            gc.fillRect(projectile.getPosition().getX(), projectile.getPosition().getY(), 
                       projectile.getWidth(), projectile.getHeight());
        }
    }
    
    private Color getProjectileColor(se233.contra_project.actors.Projectile.ProjectileType type) {
        switch (type) {
            case STRAIGHT: return Color.YELLOW;
            case HOMING: return Color.PINK;
            case BOUNCING: return Color.CYAN;
            default: return Color.WHITE;
        }
    }
    
    private void drawUI() {
        // Boss health bar
        BossHealthBar healthBar = new BossHealthBar(canvas, currentBoss);
        healthBar.update(1.0 / 60.0);
        healthBar.draw();
        
        // Demo info
        gc.setFont(new Font(14));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(Color.WHITE);
        gc.fillText("Current Boss: " + (currentBossIndex + 1) + "/3", 780, 50);
        gc.fillText("Time: " + String.format("%.1f", timeElapsed) + "s", 780, 70);
        gc.fillText("Demo Duration: " + String.format("%.1f", bossSwitchTimer) + "s", 780, 90);
    }
    
    private void drawDebugInfo() {
        gc.setFont(new Font(12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.LIGHTGRAY);
        
        int y = 120;
        gc.fillText("=== CONTROLS ===", 20, y);
        y += 20;
        gc.fillText("SPACE: Start/Stop Demo", 20, y);
        y += 15;
        gc.fillText("D: Toggle Debug Info", 20, y);
        y += 15;
        gc.fillText("S: Toggle Slow Motion", 20, y);
        y += 15;
        gc.fillText("A: Toggle Auto Battle", 20, y);
        y += 15;
        gc.fillText("1,2,3: Switch Bosses", 20, y);
        y += 15;
        gc.fillText("R: Reset Demo", 20, y);
        y += 30;
        
        if (currentBoss != null) {
            gc.fillText("=== BOSS DATA ===", 20, y);
            y += 20;
            gc.fillText("Health: " + currentBoss.getHealth() + "/" + currentBoss.getMaxHealth(), 20, y);
            y += 15;
            gc.fillText("State: " + currentBoss.getCurrentState(), 20, y);
            y += 15;
            gc.fillText("Score Value: " + currentBoss.getScoreValue(), 20, y);
            y += 15;
            gc.fillText("Active Projectiles: " + currentBoss.getProjectiles().size(), 20, y);
        }
    }
    
    private void drawVisualEffects() {
        // Visual effects are handled by the collision system
        // This method is kept for future enhancements
    }
    
    private Color getEffectColor(String effectType) {
        switch (effectType) {
            case "bullet_hit": return Color.YELLOW;
            case "explosion": return Color.RED;
            case "wall_break": return Color.CYAN;
            case "shield_block": return Color.BLUE;
            case "fire_damage": return Color.ORANGE;
            case "ice_slow": return Color.SKYBLUE;
            case "stun": return Color.YELLOW;
            case "poison": return Color.LIME;
            case "lightning": return Color.CYAN;
            default: return Color.WHITE;
        }
    }
    
    public void start() {
        demoActive = true;
        startAnimation();
    }
    
    public void stop() {
        demoActive = false;
        stopAnimation();
    }
    
    public boolean isActive() {
        return demoActive;
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
