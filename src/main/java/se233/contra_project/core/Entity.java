package se233.contra_project.core;

import javafx.geometry.Point2D;

/**
 * Base class for all game entities (Player, Bullet, Boss, etc.)
 * Provides common properties like position, velocity, health, and basic update logic.
 */
public abstract class Entity {
    protected Point2D position;
    protected Point2D velocity;
    protected int health;
    protected boolean alive;
    protected double width;
    protected double height;

    public Entity(double x, double y, double width, double height) {
        this.position = new Point2D(x, y);
        this.velocity = new Point2D(0, 0);
        this.health = 1;
        this.alive = true;
        this.width = width;
        this.height = height;
    }

    /**
     * Update entity state each frame
     * @param deltaTime time elapsed since last update
     */
    public abstract void update(double deltaTime);

    /**
     * Check if entity collides with another entity
     * @param other the other entity to check collision with
     * @return true if colliding
     */
    public boolean collidesWith(Entity other) {
        return this.position.getX() < other.position.getX() + other.width &&
               this.position.getX() + this.width > other.position.getX() &&
               this.position.getY() < other.position.getY() + other.height &&
               this.position.getY() + this.height > other.position.getY();
    }

    /**
     * Take damage and check if entity should die
     * @param damage amount of damage to take
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.alive = false;
        }
    }

    // Getters and setters
    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public void setPosition(double x, double y) { this.position = new Point2D(x, y); }

    public Point2D getVelocity() { return velocity; }
    public void setVelocity(Point2D velocity) { this.velocity = velocity; }
    public void setVelocity(double vx, double vy) { this.velocity = new Point2D(vx, vy); }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
}