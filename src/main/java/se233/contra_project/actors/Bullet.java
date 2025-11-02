package se233.contra_project.actors;

import javafx.scene.image.Image;
import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;

/**
 * Bullet class represents player projectiles
 * Moves in a straight line and deals damage to enemies
 */
public class Bullet extends Entity {
    private static final double BULLET_SPEED = 525.0; // pixels per second
    private static final double BULLET_WIDTH = 10.0;
    private static final double BULLET_HEIGHT = 6.0;
    private static final int BULLET_DAMAGE = 5;
    private static final double IMPACT_DURATION_DEFAULT = 0.2;

    private Sprite sprite;
    private boolean fromPlayer; // true if fired by player, false if enemy projectile
    private boolean impactActive;
    private double impactTimer;
    private double impactDuration = IMPACT_DURATION_DEFAULT;

    public Bullet(double x, double y, boolean movingRight, boolean fromPlayer) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
        this.fromPlayer = fromPlayer;

        // Set velocity based on direction
        double vx = movingRight ? BULLET_SPEED : -BULLET_SPEED;
        this.setVelocity(vx, 0);

        // Initialize sprite (placeholder - actual sprite loading would depend on assets)
        // this.sprite = new Sprite("path/to/bullet.png", BULLET_WIDTH, BULLET_HEIGHT);
    }

    @Override
    public void update(double deltaTime) {
        if (impactActive) {
            impactTimer += deltaTime;
            if (sprite != null) {
                sprite.setPosition(this.position.getX(), this.position.getY());
            }
            if (impactTimer >= impactDuration) {
                this.alive = false;
            }
            return;
        }

        // Move bullet based on velocity
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;
        this.setPosition(newX, newY);

        // Update sprite position if sprite exists
        if (sprite != null) {
            sprite.setPosition(newX, newY);
        }

        // Remove bullet if it goes off screen (basic boundary check)
        // This would typically be handled by the game world bounds
        if (newX < -50 || newX > 850 || newY < -50 || newY > 650) {
            this.alive = false;
        }
    }

    /**
     * Get the damage this bullet deals
     * @return damage value
     */
    public int getDamage() {
        return BULLET_DAMAGE;
    }

    /**
     * Check if this bullet was fired by the player
     * @return true if from player
     */
    public boolean isFromPlayer() {
        return fromPlayer;
    }

    /**
     * Get the sprite component
     * @return bullet sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Set the sprite component
     * @param sprite bullet sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        if (sprite != null) {
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
            sprite.setPosition(this.position.getX(), this.position.getY());
        }
    }

    /**
     * Trigger an impact animation for the bullet before it disappears.
     * @param impactImage image representing the impact frame
     * @param scale desired scale for the impact sprite
     * @param durationSeconds how long the impact effect should remain visible
     */
    public void triggerImpact(Image impactImage, double scale, double durationSeconds) {
        if (impactActive) {
            return;
        }

        if (impactImage == null) {
            this.alive = false;
            return;
        }

        impactActive = true;
        impactTimer = 0;
        impactDuration = durationSeconds > 0 ? durationSeconds : IMPACT_DURATION_DEFAULT;

        double centerX = this.position.getX() + this.width / 2.0;
        double centerY = this.position.getY() + this.height / 2.0;

        // Stop further movement so the impact stays in place
        this.setVelocity(0, 0);

        Sprite impactSprite = new Sprite(impactImage, impactImage.getWidth() * scale, impactImage.getHeight() * scale);
        setSprite(impactSprite);

        double newX = centerX - this.width / 2.0;
        double newY = centerY - this.height / 2.0;
        this.setPosition(newX, newY);
        if (this.sprite != null) {
            this.sprite.setPosition(newX, newY);
        }
    }

    /**
     * Indicates whether the bullet is currently displaying its impact effect.
     */
    public boolean isImpactActive() {
        return impactActive;
    }
}
