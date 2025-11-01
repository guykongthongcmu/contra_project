package se233.contra_project.bosses;

import se233.contra_project.actors.Projectile;

/**
 * Boss1 - Defense Wall
 * A stationary boss that shoots projectiles at the player
 */
public class Boss1 extends Boss {
    private static final double BOSS_WIDTH = 64.0;
    private static final double BOSS_HEIGHT = 96.0;
    private static final int BOSS_HEALTH = 10;
    private static final int BOSS_SCORE = 2000;

    private static final double ATTACK_COOLDOWN = 2.0; // seconds between attacks
    private double attackTimer;

    public Boss1(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;

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
        // Fire projectiles towards the left side (towards player)
        double centerX = this.position.getX() + this.width / 2;
        double centerY = this.position.getY() + this.height / 2;

        // Fire 3 projectiles in a spread pattern towards the left
        for (int i = -1; i <= 1; i++) {
            double baseAngle = Math.PI; // 180 degrees (left direction)
            double angle = baseAngle + Math.toRadians(i * 15); // -15, 0, 15 degrees from left
            double vx = Math.cos(angle) * 200; // 200 pixels/second
            double vy = Math.sin(angle) * 200;

            Projectile projectile = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }
    }
}