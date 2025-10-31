
package se233.contra_project.actors;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;

/**
 * Projectile class represents enemy projectiles (different from player bullets)
 * Can have different behaviors like homing, bouncing, etc.
 */
public class Projectile extends Entity {
    private static final double PROJECTILE_SPEED = 300.0; // pixels per second
    private static final double PROJECTILE_WIDTH = 12.0;
    private static final double PROJECTILE_HEIGHT = 8.0;
    private static final int PROJECTILE_DAMAGE = 1;

    private Sprite sprite;
    private ProjectileType type;
    private double lifetime; // how long projectile exists
    private double maxLifetime;

    public enum ProjectileType {
        STRAIGHT, // moves in straight line
        HOMING,   // homes towards player
        BOUNCING  // bounces off surfaces
    }

    public Projectile(double x, double y, double vx, double vy, ProjectileType type) {
        super(x, y, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        this.setVelocity(vx, vy);
        this.type = type;
        this.maxLifetime = 5.0; // 5 seconds lifetime
        this.lifetime = 0;

        // Initialize sprite (placeholder - actual sprite loading would depend on assets)
        // this.sprite = new Sprite("path/to/projectile.png", PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
    }

    @Override
    public void update(double deltaTime) {
        lifetime += deltaTime;

        // Remove projectile if lifetime exceeded
        if (lifetime >= maxLifetime) {
            this.alive = false;
            return;
        }

        // Update based on projectile type
        switch (type) {
            case STRAIGHT:
                updateStraight(deltaTime);
                break;
            case HOMING:
                updateHoming(deltaTime);
                break;
            case BOUNCING:
                updateBouncing(deltaTime);
                break;
        }

        // Update sprite position if sprite exists
        if (sprite != null) {
            sprite.setPosition(this.position.getX(), this.position.getY());
        }

        // Remove if off screen
        if (this.position.getX() < -50 || this.position.getX() > 850 ||
            this.position.getY() < -50 || this.position.getY() > 650) {
            this.alive = false;
        }
    }

    private void updateStraight(double deltaTime) {
        // Move in straight line (already handled by base velocity)
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;
        this.setPosition(newX, newY);
    }

    private void updateHoming(double deltaTime) {
        // Basic homing logic - would need player reference for full implementation
        // For now, just move straight
        updateStraight(deltaTime);
    }

    private void updateBouncing(double deltaTime) {
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;

        // Simple bouncing off screen edges
        if (newX <= 0 || newX >= 800 - this.width) {
            this.setVelocity(-this.velocity.getX(), this.velocity.getY());
            newX = Math.max(0, Math.min(800 - this.width, newX));
        }
        if (newY <= 0 || newY >= 600 - this.height) {
            this.setVelocity(this.velocity.getX(), -this.velocity.getY());
            newY = Math.max(0, Math.min(600 - this.height, newY));
        }

        this.setPosition(newX, newY);
    }

    /**
     * Get the damage this projectile deals
     * @return damage value
     */
    public int getDamage() {
        return PROJECTILE_DAMAGE;
    }

    /**
     * Get the projectile type
     * @return projectile type
     */
    public ProjectileType getType() {
        return type;
    }

    /**
     * Get the sprite component
     * @return projectile sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Set the sprite component
     * @param sprite projectile sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Set maximum lifetime
     * @param maxLifetime in seconds
     */
    public void setMaxLifetime(double maxLifetime) {
        this.maxLifetime = maxLifetime;
    }
}