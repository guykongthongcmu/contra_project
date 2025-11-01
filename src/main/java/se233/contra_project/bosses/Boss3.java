package se233.contra_project.bosses;

import se233.contra_project.actors.Projectile;

/**
 * Boss3 - Code Dragon
 * A flying boss that shoots projectiles at the player
 */
public class Boss3 extends Boss {
    private static final double BOSS_WIDTH = 96.0;
    private static final double BOSS_HEIGHT = 64.0;
    private static final int BOSS_HEALTH = 20;
    private static final int BOSS_SCORE = 5000;

    private static final double MOVE_SPEED = 120.0;
    private static final double ATTACK_COOLDOWN = 2.0;
    private double attackTimer;
    private double flightPatternTimer;
    private static final double FLIGHT_PATTERN_DURATION = 4.0;

    public Boss3(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.flightPatternTimer = 0;
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        flightPatternTimer += deltaTime;

        // Execute flight pattern
        executeFlightPattern(deltaTime);

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
        // Attack animation/state lasts for 1.2 seconds
        if (stateTimer >= 1.2) {
            currentState = BossState.IDLE;
            stateTimer = 0;
        }
    }

    @Override
    protected void updateDying(double deltaTime) {
        // Fall down when dying
        double newVy = this.velocity.getY() + 300 * deltaTime; // gravity
        this.setVelocity(this.velocity.getX(), newVy);
        this.setPosition(this.position.getX(), this.position.getY() + newVy * deltaTime);
    }

    @Override
    protected void performAttack() {
        double centerX = this.position.getX() + this.width / 2;
        double centerY = this.position.getY() + this.height / 2;

        // Fire projectiles in a cone pattern towards the left (player side)
        int numProjectiles = 5;
        for (int i = 0; i < numProjectiles; i++) {
            double spreadAngle = Math.toRadians(30); // 30 degree cone
            double baseAngle = Math.PI - spreadAngle / 2; // Start from left direction (180°)
            double angle = baseAngle + (spreadAngle * i / (numProjectiles - 1));

            double vx = Math.cos(angle) * 180;
            double vy = Math.sin(angle) * 180;

            Projectile projectile = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }
    }

    private void executeFlightPattern(double deltaTime) {
        double time = flightPatternTimer;
        double centerX = 400; // screen center X
        double centerY = 200; // flight area center Y
        double radius = 150;

        // Fly in circles around the center
        double angle = time * 2; // 2 radians per second
        double targetX = centerX + Math.cos(angle) * radius;
        double targetY = centerY + Math.sin(angle) * radius;
        moveTowards(targetX, targetY, deltaTime);
    }

    private void moveTowards(double targetX, double targetY, double deltaTime) {
        double dx = targetX - this.position.getX();
        double dy = targetY - this.position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 5) { // only move if not very close
            double vx = (dx / distance) * MOVE_SPEED;
            double vy = (dy / distance) * MOVE_SPEED;
            this.setVelocity(vx, vy);
        } else {
            this.setVelocity(0, 0);
        }

        // Apply movement
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;

        // Keep within screen bounds
        newX = Math.max(0, Math.min(800 - this.width, newX));
        newY = Math.max(0, Math.min(600 - this.height, newY));

        this.setPosition(newX, newY);
    }
}