package se233.contra_project.bosses;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import java.io.InputStream;
import se233.contra_project.actors.Projectile;
import se233.contra_project.core.components.Sprite;

/**
 * Boss1 - Defense Wall
 * A stationary boss that creates defensive barriers and shoots projectiles
 * Based on the Contra boss that builds walls for protection
 */
public class Boss1 extends Boss {
    private static final String SPRITE_SHEET_PATH = "/se233/contra_project/sprites/Bosses1DefenseWall.png";

    // Sprite sheet crop (source data derived from sheet analysis)
    private static final int SPRITE_FRAME_X = 3;
    private static final int SPRITE_FRAME_Y = 63;
    private static final int SPRITE_FRAME_WIDTH = 156;
    private static final int SPRITE_FRAME_HEIGHT = 94;

    private static final double WIDTH_SCALE = 1.0;
    private static final double HEIGHT_SCALE = 3.0; // stretch vertically to emphasize wall height
    private static final int COLOR_KEY_TOLERANCE = 12;

    private static final double BOSS_WIDTH = SPRITE_FRAME_WIDTH * WIDTH_SCALE;
    private static final double BOSS_HEIGHT = SPRITE_FRAME_HEIGHT * HEIGHT_SCALE;
    private static final int BOSS_HEALTH = 10;
    private static final int BOSS_SCORE = 2;

    private static final double ATTACK_COOLDOWN = 2.0; // seconds between attacks
    private static final double WALL_SPAWN_COOLDOWN = 5.0; // seconds between wall spawns
    private double attackTimer;
    private double wallSpawnTimer;
    private boolean hasWall;

    public Boss1(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.wallSpawnTimer = 0;
        this.hasWall = false;

        loadSpriteFrame();
    }

    private void loadSpriteFrame() {
        try (InputStream stream = getClass().getResourceAsStream(SPRITE_SHEET_PATH)) {
            if (stream == null) {
                System.err.println("Boss1 sprite sheet not found at " + SPRITE_SHEET_PATH);
                return;
            }
            Image sheet = new Image(stream);
            PixelReader reader = sheet.getPixelReader();
            if (reader == null) {
                System.err.println("Boss1 sprite sheet pixel reader was null.");
                return;
            }
            WritableImage frame = new WritableImage(reader,
                    SPRITE_FRAME_X,
                    SPRITE_FRAME_Y,
                    SPRITE_FRAME_WIDTH,
                    SPRITE_FRAME_HEIGHT);

            Sprite bossSprite = new Sprite(frame,
                    SPRITE_FRAME_WIDTH * WIDTH_SCALE,
                    SPRITE_FRAME_HEIGHT * HEIGHT_SCALE);
            setSprite(bossSprite);
            setWidth(BOSS_WIDTH);
            setHeight(BOSS_HEIGHT);
        } catch (Exception e) {
            System.err.println("Failed to load Boss1 sprite: " + e.getMessage());
        }
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        wallSpawnTimer += deltaTime;

        // Spawn defensive wall periodically
        if (wallSpawnTimer >= WALL_SPAWN_COOLDOWN && !hasWall) {
            spawnDefensiveWall();
            wallSpawnTimer = 0;
            hasWall = true;
        }

        // Attack periodically
        if (attackTimer >= ATTACK_COOLDOWN) {
            currentState = BossState.ATTACKING;
            stateTimer = 0;
            performAttack();
            attackTimer = 0;
        }
    }

    @Override
    protected void updateAttacking(double deltaTime) {
        // Attack animation/state lasts for 1 second
        if (stateTimer >= 1.0) {
            currentState = BossState.IDLE;
            stateTimer = 0;
        }
    }

    @Override
    protected void updateDying(double deltaTime) {
        // Death animation handled by base class
    }

    @Override
    protected void performAttack() {
        // Fire multiple projectiles in a spread pattern
        double leftEdge = this.position.getX();
        double centerY = this.position.getY() + this.height / 2;

        // Fire 3 projectiles in a fan pattern
        for (int i = -1; i <= 1; i++) {
            double angle = Math.toRadians(i * 25); // wider spread for dodging gaps
            double vx = -Math.cos(angle) * 240;
            double vy = Math.sin(angle) * 190;

            double startX = leftEdge - 12; // emit from just outside the wall
            double startY = centerY - 4;
            Projectile projectile = new Projectile(startX, startY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }
    }

    /**
     * Spawn a defensive wall in front of the boss
     * This wall would block player bullets but allow boss projectiles through
     */
    private void spawnDefensiveWall() {
        // In a full implementation, this would create wall entities
        // For now, just set a flag and handle wall logic in collision system
        System.out.println("Boss1 spawned defensive wall");
    }

    /**
     * Check if boss currently has an active defensive wall
     * @return true if wall is active
     */
    public boolean hasDefensiveWall() {
        return hasWall;
    }

    /**
     * Remove the defensive wall (called when wall is destroyed or times out)
     */
    public void removeDefensiveWall() {
        hasWall = false;
        wallSpawnTimer = 0; // Reset timer to spawn new wall sooner
    }

    @Override
    public void takeDamage(int damage) {
        // If boss has a wall, it absorbs some damage
        if (hasWall) {
            // Wall takes 50% of the damage
            int wallDamage = damage / 2;
            damage -= wallDamage;

            // Chance to destroy wall when hit
            if (Math.random() < 0.3) { // 30% chance
                removeDefensiveWall();
            }
        }

        super.takeDamage(damage);
    }
}
