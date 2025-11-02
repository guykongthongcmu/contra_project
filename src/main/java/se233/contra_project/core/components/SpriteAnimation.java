package se233.contra_project.core.components;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

/**
 * Component for handling sprite sheet animations
 */
public class SpriteAnimation {
    private Image[] frames;
    private int currentFrame;
    private long lastFrameTime;
    private long frameDuration; // in nanoseconds
    private boolean playing;
    private boolean loop;
    private Runnable onAnimationEnd;

    public SpriteAnimation(Image[] frames, long frameDurationMillis, boolean loop) {
        this.frames = frames;
        this.currentFrame = 0;
        this.frameDuration = frameDurationMillis * 1_000_000; // convert to nanoseconds
        this.playing = false;
        this.loop = loop;
        this.lastFrameTime = 0;
    }

    /**
     * Start the animation
     */
    public void play() {
        this.playing = true;
        this.currentFrame = 0;
        this.lastFrameTime = System.nanoTime();
    }

    /**
     * Stop the animation
     */
    public void stop() {
        this.playing = false;
    }

    /**
     * Update animation frame based on elapsed time
     * @param currentTime current time in nanoseconds
     */
    public void update(long currentTime) {
        if (!playing) return;

        if (currentTime - lastFrameTime >= frameDuration) {
            currentFrame++;
            lastFrameTime = currentTime;

            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    playing = false;
                    if (onAnimationEnd != null) {
                        onAnimationEnd.run();
                    }
                }
            }
        }
    }

    /**
     * Get the current frame image
     * @return current animation frame
     */
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    /**
     * Set callback for when animation ends (non-looping animations)
     * @param callback function to run when animation ends
     */
    public void setOnAnimationEnd(Runnable callback) {
        this.onAnimationEnd = callback;
    }

    // Getters
    public boolean isPlaying() { return playing; }
    public boolean isLoop() { return loop; }
    public int getCurrentFrameIndex() { return currentFrame; }
    public int getFrameCount() { return frames.length; }
}