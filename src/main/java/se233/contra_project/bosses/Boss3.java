package se233.contra_project.bosses;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import se233.contra_project.actors.Projectile;
import se233.contra_project.core.components.Sprite;

/**
 * Boss3 - Code Dragon
 * Uses supplied sprite sheet and projectile art.
 */
public class Boss3 extends Boss {
    private static final String SPRITE_SHEET_PATH = "/se233/contra_project/sprites/Boss3_Left.png";
    private static final double SPRITE_SCALE = 0.35;

    // Frame dimensions determined from sheet (first column)
    private static final int RAW_FRAME_WIDTH = 863;
    private static final int RAW_FRAME_HEIGHT = 551;

    private static final double BOSS_WIDTH = RAW_FRAME_WIDTH * SPRITE_SCALE;
    private static final double BOSS_HEIGHT = RAW_FRAME_HEIGHT * SPRITE_SCALE;
    private static final int BOSS_HEALTH = 20;
    private static final int BOSS_SCORE = 2;

    private static final double MOVE_SPEED = 120.0;
    private static final double ATTACK_COOLDOWN = 2.0;
    private double attackTimer;
    private double flightPatternTimer;
    private static final double FLIGHT_PATTERN_DURATION = 4.0;

    private Sprite idleSprite;
    private Sprite attackSprite;

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

        loadSprites();
        applyIdleSprite();
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        flightPatternTimer += deltaTime;

        if (flightPatternTimer >= FLIGHT_PATTERN_DURATION) {
            switchFlightPattern();
            flightPatternTimer = 0;
        }

        executeFlightPattern(deltaTime);
        applyIdleSprite();

        if (attackTimer >= ATTACK_COOLDOWN) {
            currentState = BossState.ATTACKING;
            stateTimer = 0;
            applyAttackSprite();
            performAttack();
            attackTimer = 0;
        }
    }

    @Override
    protected void updateAttacking(double deltaTime) {
        applyAttackSprite();
        if (stateTimer >= 1.2) {
            currentState = BossState.IDLE;
            stateTimer = 0;
            applyIdleSprite();
        }
    }

    @Override
    protected void updateDying(double deltaTime) {
        double newVy = this.velocity.getY() + 300 * deltaTime;
        this.setVelocity(this.velocity.getX(), newVy);
        this.setPosition(this.position.getX(), this.position.getY() + newVy * deltaTime);
    }

    @Override
    protected void performAttack() {
        double centerX = this.position.getX() + this.width / 2;
        double centerY = this.position.getY() + this.height / 2;

        int numCodeLines = 5;
        for (int i = 0; i < numCodeLines; i++) {
            double spreadAngle = Math.toRadians(30);
            double baseAngle = -spreadAngle / 2;
            double angle = baseAngle + (spreadAngle * i / (numCodeLines - 1));

            double vx = Math.cos(angle) * 180;
            double vy = Math.sin(angle) * 180;

            Projectile codeLine = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.BOUNCING);
            codeLine.setMaxLifetime(3.0);
            addProjectile(codeLine);
        }

        if (Math.random() < 0.4) {
            performCodeCompilation();
        }
    }

    private void executeFlightPattern(double deltaTime) {
        double time = flightPatternTimer;
        double centerX = 400;
        double centerY = 200;
        double radius = 150;

        switch (currentFlightPattern) {
            case CIRCULAR:
                double angle = time * 2;
                moveTowards(centerX + Math.cos(angle) * radius,
                        centerY + Math.sin(angle) * radius, deltaTime);
                break;
            case ZIGZAG:
                double zigzagX = centerX + Math.sin(time * 3) * radius;
                double zigzagY = centerY + time * 50;
                moveTowards(zigzagX, zigzagY, deltaTime);
                break;
            case DIVE_BOMB:
                moveTowards(centerX, 450, deltaTime * 2);
                break;
        }
    }

    private void moveTowards(double targetX, double targetY, double deltaTime) {
        double dx = targetX - this.position.getX();
        double dy = targetY - this.position.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 5) {
            double vx = (dx / distance) * MOVE_SPEED;
            double vy = (dy / distance) * MOVE_SPEED;
            this.setVelocity(vx, vy);
        } else {
            this.setVelocity(0, 0);
        }

        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;

        newX = Math.max(0, Math.min(800 - this.width, newX));
        newY = Math.max(0, Math.min(600 - this.height, newY));

        this.setPosition(newX, newY);
    }

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

    private void performCodeCompilation() {
        double compileX = this.position.getX() + this.width / 2;
        double compileY = this.position.getY() + this.height;

        for (int i = 0; i < 12; i++) {
            double angle = (2 * Math.PI * i) / 12;
            double vx = Math.cos(angle) * 300;
            double vy = Math.sin(angle) * 300;

            Projectile codeProjectile = new Projectile(compileX, compileY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            codeProjectile.setMaxLifetime(2.0);
            addProjectile(codeProjectile);
        }
    }

    public FlightPattern getCurrentFlightPattern() {
        return currentFlightPattern;
    }

    private void loadSprites() {
        try (java.io.InputStream stream = getClass().getResourceAsStream(SPRITE_SHEET_PATH)) {
            if (stream == null) {
                System.err.println("Boss3 sprite sheet not found: " + SPRITE_SHEET_PATH);
                return;
            }

            Image sheet = new Image(stream);
            idleSprite = createFrameSprite(sheet, 0, 0, true);
            attackSprite = createFrameSprite(sheet, 2, 1, true);

            if (idleSprite == null && attackSprite == null) {
                System.err.println("Failed to load Boss3 sprites from sheet.");
            } else {
                if (idleSprite == null) {
                    idleSprite = attackSprite;
                }
                if (attackSprite == null) {
                    attackSprite = idleSprite;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load Boss3 sprite from " + SPRITE_SHEET_PATH + ": " + e.getMessage());
        }
    }

    private Sprite createFrameSprite(Image sheet, int column, int row, boolean flipHorizontal) {
        PixelReader reader = sheet.getPixelReader();
        if (reader == null) {
            return null;
        }

        int frameX = column * RAW_FRAME_WIDTH;
        int frameY = row * RAW_FRAME_HEIGHT;
        int sheetWidth = (int) Math.round(sheet.getWidth());
        int sheetHeight = (int) Math.round(sheet.getHeight());

        if (frameX + RAW_FRAME_WIDTH > sheetWidth || frameY + RAW_FRAME_HEIGHT > sheetHeight) {
            System.err.println("Requested Boss3 frame (" + column + ", " + row + ") is outside of sprite sheet bounds.");
            return null;
        }

        WritableImage frame = new WritableImage(reader, frameX, frameY, RAW_FRAME_WIDTH, RAW_FRAME_HEIGHT);

        if (flipHorizontal) {
            frame = flipImageHorizontally(frame);
        }

        return new Sprite(frame, frame.getWidth() * SPRITE_SCALE, frame.getHeight() * SPRITE_SCALE);
    }

    private WritableImage flipImageHorizontally(WritableImage image) {
        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return image;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage flipped = new WritableImage(width, height);
        PixelWriter writer = flipped.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(width - 1 - x, y, reader.getArgb(x, y));
            }
        }

        return flipped;
    }

    private void applyIdleSprite() {
        Sprite target = idleSprite != null ? idleSprite : attackSprite;
        if (target != null && this.sprite != target) {
            applySprite(target);
        }
    }

    private void applyAttackSprite() {
        Sprite target = attackSprite != null ? attackSprite : idleSprite;
        if (target != null && this.sprite != target) {
            applySprite(target);
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
