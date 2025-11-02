package se233.contra_project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss1;

/**
 * Stage 1 gameplay screen – Defense Wall boss encounter.
 */
public class Stage1Screen extends BaseStageScreen {
    private static final String BACKGROUND_PATH = "/se233/contra_project/ui/Stage1_full.png";
    private static final String STAGE_TITLE = "Stage 1 - Boss Fight";
    private static final String BOSS_NAME = "Boss1 - Defense Wall";
    private static final String SPRITE_INFO = "Bosses1DefenseWall.png";
    private final Runnable advanceToStage2;

    public Stage1Screen() {
        this(null);
    }

    public Stage1Screen(Runnable advanceToStage2) {
        this.advanceToStage2 = advanceToStage2;
    }
    private static final double GROUND_OFFSET = 140.0;

    @Override
    protected Boss createBoss() {
        return new Boss1(0, 0);
    }

    @Override
    protected String getBackgroundResourcePath() {
        return BACKGROUND_PATH;
    }

    @Override
    protected String getStageTitle() {
        return STAGE_TITLE;
    }

    @Override
    protected String getBossDisplayName() {
        return "Boss: " + BOSS_NAME;
    }

    @Override
    protected String getSpriteResourceInfo() {
        return SPRITE_INFO;
    }

    @Override
    protected String getResetHint() {
        return "R - Reset Boss1";
    }

    @Override
    protected String[] getAdditionalControlHints() {
        return new String[] { "SPACE - Shoot" };
    }

    @Override
    protected void onBossCreated(Boss boss) {
        positionBossRightEdge(boss);
    }

    @Override
    protected void onBossDefeated() {
        System.out.println("Boss1 defeated! Head to the right edge to advance.");
    }

    @Override
    protected void onPlayerReachedExitAfterBossDefeat() {
        if (advanceToStage2 != null) {
            advanceToStage2.run();
        } else {
            System.out.println("Advance callback not set; cannot switch to Stage 2.");
        }
    }

    @Override
    protected void handleCustomKey(KeyCode code) {
        onCustomKeyPressed(code, true);
    }

    @Override
    protected boolean drawCustomBackground(GraphicsContext context) {
        javafx.scene.image.Image background = getBackgroundImage();
        if (background == null) {
            return false;
        }

        double destWidth = getCanvasNode().getWidth();
        double destHeight = getCanvasNode().getHeight();
        context.drawImage(background, 0, 0, destWidth, destHeight);
        return true;
    }

    private void positionBossRightEdge(Boss boss) {
        double canvasWidth = getCanvasNode().getWidth();
        double canvasHeight = getCanvasNode().getHeight();

        double marginRight = 20;
        double startX = Math.max(marginRight, canvasWidth - boss.getWidth() - marginRight);

        double startY = canvasHeight - boss.getHeight() - GROUND_OFFSET;
        if (startY < 0) {
            startY = 0;
        }

        boss.setPosition(startX, startY);
    }

    @Override
    protected double getFloorY() {
        return getCanvasNode().getHeight() - GROUND_OFFSET;
    }
}
