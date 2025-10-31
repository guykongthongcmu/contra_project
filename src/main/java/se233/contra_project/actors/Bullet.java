package se233.contra_project.actors;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;

/**
 * Bullet class represents player projectiles
 * Moves in a straight line and deals damage to enemies
 */
public class Bullet extends Entity {
    private static final double BULLET_SPEED = 400.0; // pixels per second
    private static final double BULLET_WIDTH = 8.0;
    private static final double BULLET_HEIGHT = 4.0;
    private static final int BULLET_DAMAGE = 1;

    private Sprite sprite;
    private boolean fromPlayer; // true if fired by player, false if enemy projectile

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
    }
}