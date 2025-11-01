package se233.contra_project.bosses;

import se233.contra_project.actors.Projectile;

/**
 * Boss3 - Custom Boss (Code Dragon)
 * A flying boss that shoots lines of code and performs aerial attacks
 * Custom design with unique mechanics not found in original Contra
 */
public class Boss3 extends Boss {
    private static final double BOSS_WIDTH = 96.0;
    private static final double BOSS_HEIGHT = 64.0;
    private static final int BOSS_HEALTH = 20;
    private static final int BOSS_SCORE = 2;

    private static final double MOVE_SPEED = 120.0;
    private static final double ATTACK_COOLDOWN = 2.0;
    private double attackTimer;
    private double flightPatternTimer;
    private static final double FLIGHT_PATTERN_DURATION = 4.0;

    private FlightPattern currentFlightPattern;
    private enum FlightPattern {
        CIRCULAR,
        ZIGZAG,
        DIVE_BOMB
    }

    public Boss3(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.flightPatternTimer = 0;
        this.currentFlightPattern = FlightPattern.CIRCULAR;

        // Initialize sprite (placeholder - would load custom dragon sprite)
        // this.sprite = new Sprite("path/to/fire_dragon_boss.png", BOSS_WIDTH, BOSS_HEIGHT);
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        flightPatternTimer += deltaTime;

        // Change flight pattern periodically
        if (flightPatternTimer >= FLIGHT_PATTERN_DURATION) {
            switchFlightPattern();
            flightPatternTimer = 0;
        }

        // Execute current flight pattern
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

        // Code breath attack - shoot System.out.println("Hello World"); as projectiles
        int numCodeLines = 5;
        for (int i = 0; i < numCodeLines; i++) {
            double spreadAngle = Math.toRadians(30); // 30 degree cone
            double baseAngle = -spreadAngle / 2;
            double angle = baseAngle + (spreadAngle * i / (numCodeLines - 1));

            double vx = Math.cos(angle) * 180;
            double vy = Math.sin(angle) * 180;

            Projectile codeLine = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.BOUNCING);
            codeLine.setMaxLifetime(3.0); // code lines last longer
            addProjectile(codeLine);
        }

        // Occasionally perform special code compilation attack
        if (Math.random() < 0.4) { // 40% chance
            performCodeCompilation();
        }
    }

    /**
     * Execute the current flight pattern movement
     */
    private void executeFlightPattern(double deltaTime) {
        double time = flightPatternTimer;
        double centerX = 400; // screen center X
        double centerY = 200; // flight area center Y
        double radius = 150;

        switch (currentFlightPattern) {
            case CIRCULAR:
                // Fly in circles around the center
                double angle = time * 2; // 2 radians per second
                double targetX = centerX + Math.cos(angle) * radius;
                double targetY = centerY + Math.sin(angle) * radius;
                moveTowards(targetX, targetY, deltaTime);
                break;

            case ZIGZAG:
                // Zigzag pattern
                double zigzagX = centerX + Math.sin(time * 3) * radius;
                double zigzagY = centerY + time * 50; // move down over time
                moveTowards(zigzagX, zigzagY, deltaTime);
                break;

            case DIVE_BOMB:
                // Quick dive toward player area
                double diveX = centerX;
                double diveY = 450; // near ground
                moveTowards(diveX, diveY, deltaTime * 2); // faster for dive
                break;
        }
    }

    /**
     * Move smoothly towards a target position
     */
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

    /**
     * Switch to the next flight pattern
     */
    private void switchFlightPattern() {
        switch (currentFlightPattern) {
            case CIRCULAR:
                currentFlightPattern = FlightPattern.ZIGZAG;
                break;
            case ZIGZAG:
                currentFlightPattern = FlightPattern.DIVE_BOMB;
                break;
            case DIVE_BOMB:
                currentFlightPattern = FlightPattern.CIRCULAR;
                break;
        }
    }

    /**
     * Perform a special code compilation attack
     */
    private void performCodeCompilation() {
        // Create a burst of code projectiles at the compilation location
        double compileX = this.position.getX() + this.width / 2;
        double compileY = this.position.getY() + this.height;

        // Fire System.out.println("Hello World"); projectiles outward in all directions
        for (int i = 0; i < 12; i++) {
            double angle = (2 * Math.PI * i) / 12;
            double vx = Math.cos(angle) * 300;
            double vy = Math.sin(angle) * 300;

            Projectile codeProjectile = new Projectile(compileX, compileY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            codeProjectile.setMaxLifetime(2.0);
            addProjectile(codeProjectile);
        }
    }

    /**
     * Get the current flight pattern (for animation purposes)
     * @return current flight pattern
     */
    public FlightPattern getCurrentFlightPattern() {
        return currentFlightPattern;
    }
}