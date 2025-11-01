package se233.contra_project.actors;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Projectile class for advanced boss attacks
 * Supports different projectile behaviors, visual effects, and collision management
 */
public class Projectile extends Entity {
    private static final double DEFAULT_SPEED = 300.0;
    private static final double DEFAULT_WIDTH = 12.0;
    private static final double DEFAULT_HEIGHT = 8.0;
    private static final int DEFAULT_DAMAGE = 1;

    private Sprite sprite;
    private ProjectileType type;
    private double lifetime; // how long projectile exists
    private double maxLifetime;
    private int damage;
    private boolean alive;
    
    // Advanced visual effects
    private double rotation;
    private double rotationSpeed;
    private double opacity;
    private double scale;
    private String visualEffect;
    private boolean hasTrail;
    private List<Position> trailPositions;
    
    // Enhanced behavior
    private boolean pierceThrough; // Can hit multiple targets
    private boolean bounces; // Can bounce off surfaces
    private int maxBounces;
    private int bounceCount;
    private double gravityEffect;
    private boolean homesToPlayer;
    private double homingStrength;
    
    // Collision properties
    private boolean solidCollision;
    private boolean destroyOnCollision;
    
    // Special projectile types
    private ProjectileEffect effect;
    private double effectTimer;
    
    public enum ProjectileType {
        STRAIGHT,     // moves in straight line
        HOMING,       // homes towards player
        BOUNCING,     // bounces off surfaces
        PIERCING,     // can hit multiple targets
        GRAVITY_AFFECTED, // affected by gravity
        TELEPORTING,  // teleports randomly
        SPLITTING     // splits into smaller projectiles
    }
    
    public enum ProjectileEffect {
        NONE,
        FIRE,         // Fire effect with damage over time
        ICE,          // Slows down targets
        SHOCK,        // Stuns targets briefly
        POISON,       // Damage over time
        EXPLOSIVE,    // Area damage on impact
        CHAIN_LIGHTNING // Chains to nearby targets
    }
    
    private static class Position {
        double x, y;
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public Projectile(double x, double y, double vx, double vy, ProjectileType type) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setVelocity(vx, vy);
        this.type = type;
        this.maxLifetime = 5.0;
        this.lifetime = 0;
        this.damage = DEFAULT_DAMAGE;
        this.alive = true;
        
        // Advanced visual properties
        this.rotation = 0;
        this.rotationSpeed = 0;
        this.opacity = 1.0;
        this.scale = 1.0;
        this.visualEffect = "";
        this.hasTrail = false;
        this.trailPositions = new ArrayList<>();
        
        // Enhanced behavior defaults
        this.pierceThrough = false;
        this.bounces = false;
        this.maxBounces = 3;
        this.bounceCount = 0;
        this.gravityEffect = 0;
        this.homesToPlayer = false;
        this.homingStrength = 0.1;
        
        // Collision properties
        this.solidCollision = true;
        this.destroyOnCollision = true;
        
        // Special effects
        this.effect = ProjectileEffect.NONE;
        this.effectTimer = 0;

        // Initialize sprite based on type
        initializeSprite();
        
        // Configure behavior based on type
        configureBehaviorByType();
    }
    
    private void initializeSprite() {
        try {
            String spritePath = getSpritePathByType();
            this.sprite = new Sprite(spritePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } catch (Exception e) {
            // Fallback to default or no sprite
            System.err.println("Failed to load projectile sprite: " + e.getMessage());
        }
    }
    
    private String getSpritePathByType() {
        switch (type) {
            case STRAIGHT:
                return "se233/contra_project/sprites/projectiles/basic_projectile.png";
            case HOMING:
                return "se233/contra_project/sprites/projectiles/homing_projectile.png";
            case BOUNCING:
                return "se233/contra_project/sprites/projectiles/bouncing_projectile.png";
            case PIERCING:
                return "se233/contra_project/sprites/projectiles/piercing_projectile.png";
            case GRAVITY_AFFECTED:
                return "se233/contra_project/sprites/projectiles/gravity_projectile.png";
            case TELEPORTING:
                return "se233/contra_project/sprites/projectiles/teleport_projectile.png";
            case SPLITTING:
                return "se233/contra_project/sprites/projectiles/splitting_projectile.png";
            default:
                return "se233/contra_project/sprites/projectiles/default_projectile.png";
        }
    }
    
    private void configureBehaviorByType() {
        switch (type) {
            case STRAIGHT:
                this.destroyOnCollision = true;
                this.opacity = 1.0;
                break;
            case HOMING:
                this.homesToPlayer = true;
                this.homingStrength = 0.05;
                this.rotationSpeed = 180;
                this.hasTrail = true;
                break;
            case BOUNCING:
                this.bounces = true;
                this.maxBounces = 3;
                this.destroyOnCollision = false;
                this.rotationSpeed = 360;
                break;
            case PIERCING:
                this.pierceThrough = true;
                this.destroyOnCollision = false;
                this.maxLifetime = 8.0;
                break;
            case GRAVITY_AFFECTED:
                this.gravityEffect = 300; // pixels per second squared
                this.hasTrail = true;
                break;
            case TELEPORTING:
                this.destroyOnCollision = false;
                this.maxLifetime = 4.0;
                this.hasTrail = true;
                break;
            case SPLITTING:
                this.pierceThrough = true;
                this.destroyOnCollision = false;
                this.hasTrail = true;
                break;
        }
    }

    @Override
    public void update(double deltaTime) {
        lifetime += deltaTime;
        effectTimer += deltaTime;

        // Remove projectile if lifetime exceeded
        if (lifetime >= maxLifetime) {
            this.alive = false;
            return;
        }

        // Update visual effects
        updateVisualEffects(deltaTime);
        
        // Update trail
        if (hasTrail) {
            updateTrail();
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
            case PIERCING:
                updatePiercing(deltaTime);
                break;
            case GRAVITY_AFFECTED:
                updateGravityAffected(deltaTime);
                break;
            case TELEPORTING:
                updateTeleporting(deltaTime);
                break;
            case SPLITTING:
                updateSplitting(deltaTime);
                break;
        }

        // Update sprite position and visual properties
        updateSprite();

        // Remove if off screen (with margin for projectiles that move fast)
        double margin = 100;
        if (this.position.getX() < -margin || this.position.getX() > 800 + margin ||
            this.position.getY() < -margin || this.position.getY() > 600 + margin) {
            this.alive = false;
        }
    }
    
    private void updateVisualEffects(double deltaTime) {
        // Update rotation
        rotation += rotationSpeed * deltaTime;
        
        // Apply special effect
        switch (effect) {
            case FIRE:
                // Flicker effect
                opacity = 0.8 + 0.2 * Math.sin(effectTimer * 20);
                break;
            case ICE:
                // Slight blue tint (handled in rendering)
                opacity = 0.9;
                break;
            case SHOCK:
                // Rapid flicker
                opacity = 0.5 + 0.5 * Math.sin(effectTimer * 50);
                break;
            case POISON:
                // Green pulse
                opacity = 0.7 + 0.3 * Math.sin(effectTimer * 3);
                break;
            case EXPLOSIVE:
                // Grow then fade
                double progress = effectTimer / maxLifetime;
                scale = 1.0 + progress * 0.5;
                opacity = 1.0 - progress * 0.3;
                break;
            case CHAIN_LIGHTNING:
                // Random flicker
                opacity = Math.random() * 0.8 + 0.2;
                break;
            default:
                break;
        }
    }
    
    private void updateTrail() {
        // Add current position to trail
        trailPositions.add(new Position(this.position.getX(), this.position.getY()));
        
        // Limit trail length
        int maxTrailLength = 8;
        if (trailPositions.size() > maxTrailLength) {
            trailPositions.remove(0);
        }
    }

    private void updateStraight(double deltaTime) {
        advance(deltaTime);
    }

    private void updateHoming(double deltaTime) {
        // Basic homing logic - in full implementation would track player
        // For now, apply slight random steering
        double currentVx = this.velocity.getX();
        double currentVy = this.velocity.getY();
        
        // Add slight random deviation
        double steeringX = (Math.random() - 0.5) * 20 * deltaTime;
        double steeringY = (Math.random() - 0.5) * 20 * deltaTime;
        
        currentVx += steeringX;
        currentVy += steeringY;
        
        // Normalize to maintain speed
        double speed = Math.sqrt(currentVx * currentVx + currentVy * currentVy);
        if (speed > 0) {
            currentVx = (currentVx / speed) * DEFAULT_SPEED;
            currentVy = (currentVy / speed) * DEFAULT_SPEED;
        }
        
        this.setVelocity(currentVx, currentVy);
        advance(deltaTime);
    }

    private void updateBouncing(double deltaTime) {
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;

        // Check collisions with screen edges
        boolean bounced = false;
        
        if (newX <= 0 || newX >= 800 - this.width) {
            this.setVelocity(-this.velocity.getX(), this.velocity.getY());
            newX = Math.max(0, Math.min(800 - this.width, newX));
            bounced = true;
        }
        if (newY <= 0 || newY >= 600 - this.height) {
            this.setVelocity(this.velocity.getX(), -this.velocity.getY());
            newY = Math.max(0, Math.min(600 - this.height, newY));
            bounced = true;
        }
        
        if (bounced) {
            bounceCount++;
            if (bounceCount >= maxBounces) {
                this.alive = false;
            }
        }
        
        this.setPosition(newX, newY);
    }

    private void updatePiercing(double deltaTime) {
        // Move straight but don't destroy on collision
        advance(deltaTime);
        // For piercing projectiles, collision detection would be handled externally
    }

    private void updateGravityAffected(double deltaTime) {
        // Apply gravity
        double newVy = this.velocity.getY() + gravityEffect * deltaTime;
        this.setVelocity(this.velocity.getX(), newVy);
        advance(deltaTime);
    }

    private void updateTeleporting(double deltaTime) {
        // Random teleportation
        if (Math.random() < 0.02) { // 2% chance per frame
            double newX = Math.random() * (800 - this.width);
            double newY = Math.random() * (600 - this.height);
            this.setPosition(newX, newY);
            
            // Create teleport effect
            visualEffect = "teleport";
        }
        advance(deltaTime);
    }

    private void updateSplitting(double deltaTime) {
        // Will split into smaller projectiles (implementation would depend on game state)
        if (lifetime > maxLifetime * 0.7 && Math.random() < 0.01) {
            // Mark for splitting (actual splitting handled externally)
            visualEffect = "split_ready";
        }
        advance(deltaTime);
    }
    
    private void updateSprite() {
        if (sprite != null) {
            sprite.setPosition(this.position.getX(), this.position.getY());
            sprite.setRotation((float)rotation);
        }
    }

    private void advance(double deltaTime) {
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;
        this.setPosition(newX, newY);
    }

    // Public methods for collision and effects
    
    public void handleCollision(Entity other) {
        if (!solidCollision || !alive) return;
        
        switch (effect) {
            case EXPLOSIVE:
                // Create explosion effect (handled externally)
                visualEffect = "explosion";
                this.alive = false;
                break;
            case CHAIN_LIGHTNING:
                // Chain to nearby targets (handled externally)
                visualEffect = "lightning";
                break;
            default:
                if (destroyOnCollision) {
                    this.alive = false;
                }
                break;
        }
    }
    
    public boolean shouldPierce() {
        return pierceThrough;
    }
    
    public boolean canBounce() {
        return bounces && bounceCount < maxBounces;
    }
    
    public void setBounceCount(int count) {
        this.bounceCount = count;
    }
    
    public void setEffect(ProjectileEffect effect) {
        this.effect = effect;
        this.effectTimer = 0;
    }
    
    public void setVisualEffect(String effect) {
        this.visualEffect = effect;
    }
    
    public void setPierceThrough(boolean pierce) {
        this.pierceThrough = pierce;
    }
    
    public void setMaxBounces(int maxBounces) {
        this.maxBounces = maxBounces;
    }
    
    public void setHomingStrength(double strength) {
        this.homingStrength = strength;
    }
    
    public void setGravityEffect(double gravity) {
        this.gravityEffect = gravity;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public void setMaxLifetime(double maxLifetime) {
        this.maxLifetime = maxLifetime;
    }
    
    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
    
    public void setHasTrail(boolean hasTrail) {
        this.hasTrail = hasTrail;
    }
    
    public void setSolidCollision(boolean solid) {
        this.solidCollision = solid;
    }
    
    public void setDestroyOnCollision(boolean destroy) {
        this.destroyOnCollision = destroy;
    }

    // Getters
    public ProjectileType getType() { return type; }
    public ProjectileEffect getEffect() { return effect; }
    public int getDamage() { return damage; }
    public double getLifetime() { return lifetime; }
    public double getMaxLifetime() { return maxLifetime; }
    public double getRotation() { return rotation; }
    public double getOpacity() { return opacity; }
    public double getScale() { return scale; }
    public String getVisualEffect() { return visualEffect; }
    public boolean isAlive() { return alive; }
    public boolean hasTrail() { return hasTrail; }
    public List<Position> getTrail() { return new ArrayList<>(trailPositions); }
    public int getBounceCount() { return bounceCount; }
    public int getMaxBounces() { return maxBounces; }
    public boolean isPierceThrough() { return pierceThrough; }
    public boolean isBounces() { return bounces; }
    public boolean isHomesToPlayer() { return homesToPlayer; }
    public double getHomingStrength() { return homingStrength; }
    public double getGravityEffect() { return gravityEffect; }
    public boolean isSolidCollision() { return solidCollision; }
    public boolean shouldDestroyOnCollision() { return destroyOnCollision; }
    
    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }
    
    // Factory methods for creating special projectiles
    public static Projectile createFireProjectile(double x, double y, double vx, double vy) {
        Projectile proj = new Projectile(x, y, vx, vy, ProjectileType.STRAIGHT);
        proj.setEffect(ProjectileEffect.FIRE);
        proj.setDamage(2);
        return proj;
    }
    
    public static Projectile createIceProjectile(double x, double y, double vx, double vy) {
        Projectile proj = new Projectile(x, y, vx, vy, ProjectileType.HOMING);
        proj.setEffect(ProjectileEffect.ICE);
        proj.setDamage(1);
        return proj;
    }
    
    public static Projectile createExplosiveProjectile(double x, double y, double vx, double vy) {
        Projectile proj = new Projectile(x, y, vx, vy, ProjectileType.GRAVITY_AFFECTED);
        proj.setEffect(ProjectileEffect.EXPLOSIVE);
        proj.setDamage(3);
        return proj;
    }
}
