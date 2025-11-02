package se233.contra_project.bosses;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import se233.contra_project.actors.Projectile;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.exceptions.GameExceptions;

/**
 * Boss2 - Java
 * A mobile boss that moves around and performs complex attack patterns
 * Based on the Java-themed boss from Contra
 */
public class Boss2 extends Boss {
    private static final double BOSS_WIDTH = 112.0;
    private static final double BOSS_HEIGHT = 113.0;
    private static final int BOSS_HEALTH = 15;
    private static final int BOSS_SCORE = 2;

    private static final double MOVE_SPEED = 100.0; // pixels per second
    private static final double ATTACK_COOLDOWN = 1.5; // seconds between attacks
    private double attackTimer;
    private double moveDirection; // angle in radians for movement
    private double moveTimer;
    private static final double DIRECTION_CHANGE_TIME = 3.0; // change direction every 3 seconds

    private Sprite idleSprite;
    private Sprite attackSprite;
    private Sprite deadSprite;

    public Boss2(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.moveDirection = 0; // start moving right
        this.moveTimer = 0;

        loadSprites();
        applySprite(idleSprite);
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        moveTimer += deltaTime;

        if (this.getSprite() != idleSprite) {
            applySprite(idleSprite);
        }

        // Change movement direction periodically
        if (moveTimer >= DIRECTION_CHANGE_TIME) {
            moveDirection = Math.random() * 2 * Math.PI; // random direction
            moveTimer = 0;
        }

        // Move in current direction
        double vx = Math.cos(moveDirection) * MOVE_SPEED;
        double vy = Math.sin(moveDirection) * MOVE_SPEED;
        this.setVelocity(vx, vy);

        // Keep boss within screen bounds (assuming 800x600 screen)
        double newX = this.position.getX() + vx * deltaTime;
        double newY = this.position.getY() + vy * deltaTime;

        // Bounce off screen edges
        if (newX <= 0 || newX >= 800 - this.width) {
            moveDirection = Math.PI - moveDirection; // reflect horizontally
            vx = Math.cos(moveDirection) * MOVE_SPEED;
        }
        if (newY <= 0 || newY >= 600 - this.height) {
            moveDirection = -moveDirection; // reflect vertically
            vy = Math.sin(moveDirection) * MOVE_SPEED;
        }

        this.setVelocity(vx, vy);
        this.setPosition(this.position.getX() + vx * deltaTime,
                        this.position.getY() + vy * deltaTime);

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
        // Attack animation/state lasts for 0.8 seconds
        if (stateTimer >= 0.8) {
            currentState = BossState.IDLE;
            stateTimer = 0;
            applySprite(idleSprite);
        }
    }

    @Override
    protected void updateDying(double deltaTime) {
        // Stop movement when dying
        this.setVelocity(0, 0);
    }

    @Override
    protected void performAttack() {
        double centerX = this.position.getX() + this.width / 2;
        double centerY = this.position.getY() + this.height / 2;

        applySprite(attackSprite != null ? attackSprite : idleSprite);

        // Perform a circular spread attack - fire projectiles in all directions
        int numProjectiles = 8;
        for (int i = 0; i < numProjectiles; i++) {
            double angle = (2 * Math.PI * i) / numProjectiles;
            double vx = Math.cos(angle) * 250; // faster projectiles
            double vy = Math.sin(angle) * 250;

            Projectile projectile = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }

        // Occasionally fire homing projectiles
        if (Math.random() < 0.3) { // 30% chance
            // For homing projectiles, we'd need player reference
            // For now, just fire straight down as placeholder
            Projectile homingProjectile = new Projectile(centerX, centerY + this.height/2,
                                                      0, 150, Projectile.ProjectileType.HOMING);
            addProjectile(homingProjectile);
        }
    }

    /**
     * Get the current movement direction (for animation purposes)
     * @return direction angle in radians
     */
    public double getMoveDirection() {
        return moveDirection;
    }

    /**
     * Set movement direction (can be used for AI control)
     * @param direction angle in radians
     */
    public void setMoveDirection(double direction) {
        this.moveDirection = direction;
        this.moveTimer = 0; // reset direction change timer
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // When damaged, change direction to be more aggressive
        if (this.health > 0) {
            // Move toward player (simplified - would need player position)
            // For now, just random direction change
            moveDirection += Math.PI / 4; // turn 45 degrees
            moveTimer = DIRECTION_CHANGE_TIME / 2; // change direction sooner
        } else if (deadSprite != null) {
            applySprite(deadSprite);
        }
    }

    private void loadSprites() {
        idleSprite = loadSpriteResource("/se233/contra_project/sprites/Bosses2Java.png", 112);
        attackSprite = loadSpriteResource("/se233/contra_project/sprites/Bosses2JavaAttack.png", 0);
        deadSprite = loadSpriteResource("/se233/contra_project/sprites/Bosses2JavaDead.png", 0);
    }

    private Sprite loadSpriteResource(String path, int cropWidth) {
        try (java.io.InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw GameExceptions.failure("Sprite resource not found at " + path);
            }
            Image image = new Image(stream);
            if (cropWidth > 0 && image.getWidth() > cropWidth) {
                PixelReader reader = image.getPixelReader();
                if (reader != null) {
                    WritableImage frame = new WritableImage(reader, 0, 0, cropWidth, (int) image.getHeight());
                    return new Sprite(frame, frame.getWidth(), frame.getHeight());
                }
            }
            return new Sprite(image, image.getWidth(), image.getHeight());
        } catch (Exception e) {
            throw GameExceptions.failure("Failed to load Boss2 sprite from " + path, e);
        }
    }

    private void applySprite(Sprite sprite) {
        if (sprite == null) {
            return;
        }

        setSprite(sprite);
        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
        sprite.setPosition(this.position.getX(), this.position.getY());
    }
}
