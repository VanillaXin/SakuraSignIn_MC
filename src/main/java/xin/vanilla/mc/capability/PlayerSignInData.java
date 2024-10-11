package xin.vanilla.mc.capability;

public class PlayerSignInData implements IPlayerSignInData {
    private int score;
    private boolean isActive;

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}
