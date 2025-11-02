package se233.contra_project.ui;

import javafx.scene.input.KeyCode;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss3;

/**
 * Stage 3 gameplay screen – Code Dragon boss encounter.
 */
public class Stage3Screen extends BaseStageScreen {
    private static final String BACKGROUND_PATH = "/se233/contra_project/ui/Stage3.png";
    private static final String STAGE_TITLE = "Stage 3 - Boss Fight";
    private static final String BOSS_NAME = "Boss3 - Code Dragon";
    private static final String SPRITE_INFO = "Boss3.png";

    @Override
    protected Boss createBoss() {
        return new Boss3(0, 0);
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
        return "R - Reset Boss3";
    }

    @Override
    protected String[] getAdditionalControlHints() {
        return new String[] { "SPACE - Shoot" };
    }

    @Override
    protected void onBossCreated(Boss boss) {
        positionBossCenter(boss);
    }

    @Override
    protected void onBossDefeated() {
        triggerGameOver(true);
    }

    @Override
    protected void handleCustomKey(KeyCode code) {
        onCustomKeyPressed(code, true);
    }

    @Override
    protected String getBossProjectileSpritePath() {
        return "/se233/contra_project/sprites/Boss3bullet.png";
    }

    @Override
    protected double getBossProjectileSpriteScale() {
        return 0.18;
    }

    private void positionBossCenter(Boss boss) {
        double canvasWidth = getCanvasNode().getWidth();
        double canvasHeight = getCanvasNode().getHeight();

        double startX = (canvasWidth - boss.getWidth()) / 2;
        double startY = (canvasHeight - boss.getHeight()) / 2;

        boss.setPosition(Math.max(0, startX), Math.max(0, startY));
    }
}
