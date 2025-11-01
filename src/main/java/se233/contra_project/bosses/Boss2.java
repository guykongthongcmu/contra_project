package se233.contra_project.bosses;

import se233.contra_project.actors.Projectile;

/**
 * Boss2 - Java
 * A mobile boss that moves around and shoots projectiles at the player
 */
public class Boss2 extends Boss {
    private static final double BOSS_WIDTH = 80.0;
    private static final double BOSS_HEIGHT = 80.0;
    private static final int BOSS_HEALTH = 15;
    private static final int BOSS_SCORE = 3000;

    private static final double MOVE_SPEED = 100.0; // pixels per second
    private static final double ATTACK_COOLDOWN = 1.5; // seconds between attacks
    private double attackTimer;
    private double moveDirection; // angle in radians for movement
    private double moveTimer;
    private static final double DIRECTION_CHANGE_TIME = 3.0; // change direction every 3 seconds

    public Boss2(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.moveDirection = 0; // start moving right
        this.moveTimer = 0;
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        moveTimer += deltaTime;

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

        // Fire projectiles in all directions
        int numProjectiles = 8;
        for (int i = 0; i < numProjectiles; i++) {
            double angle = (2 * Math.PI * i) / numProjectiles;
            double vx = Math.cos(angle) * 250; // faster projectiles
            double vy = Math.sin(angle) * 250;

            Projectile projectile = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }
    }
}