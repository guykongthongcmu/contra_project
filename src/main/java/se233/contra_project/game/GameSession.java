package se233.contra_project.game;

public class GameSession {
    private int score;

    public int getScore() {
        return Math.max(0, score);
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    /**
     * Add points to the current score.
     *
     * @param delta points to add; ignored if non-positive
     * @return updated score total
     */
    public int addScore(int delta) {
        if (delta <= 0) {
            return getScore();
        }
        score = Math.max(0, score + delta);
        return score;
    }

    public void reset() {
        score = 0;
    }
}
