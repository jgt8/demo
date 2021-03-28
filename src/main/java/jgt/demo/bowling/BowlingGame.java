package jgt.demo.bowling;

public class BowlingGame {

    protected String playerID;

    /*
     * Internal game state is here exposed to package-visibility to simplify the writing of
     * the console client and tests. In a multi-user setting, these would need to be held private,
     * and a separate state object containing independent copies of this data published externally.
     * A version of this approach returning a BowlingGameState object from onRoll() can be
     * inspected at commit f3f9cc87d. This was removed for simplicity in the current context.
     */
    protected int[] rolls;
    protected int currentRollNum = 0; // last filled index in rolls[]

    protected int[] frameToRollIndex; // for each frame, index of its first roll in rolls[]
    protected int currentFrame = 0;

    protected int[] frameScores; // cumulative score at each frame
    protected int currentScore = 0; // total score

    protected final int ALL_PINS = 10;
    protected final int FIRST_FRAME = 0;
    protected final int FINAL_FRAME;
    protected final int FRAME_SIZE = 2;
    protected final int STRIKE_FRAME_SIZE = 1;
    protected final int FINAL_FRAME_MAX_SIZE = 3;
    protected final int FINAL_FRAME_MAX_SCORE = 30;

    protected static final int STANDARD_FRAME_COUNT = 10;
    protected static final String ANON_USER = "AnonymousCoward";

    protected GameStatus gameStatus = GameStatus.GAME_ON;

    public enum GameStatus {
        GAME_ON, GAME_OVER, GAME_ERROR;
    }

    public BowlingGame() {
        this(ANON_USER, STANDARD_FRAME_COUNT);
    }

    public BowlingGame(String userName) {
        this(userName, STANDARD_FRAME_COUNT);
    }

    public BowlingGame(int numFrames) {
        this(ANON_USER, numFrames);
    }

    public BowlingGame(String userName, int numFrames) {
        playerID = userName;
        rolls = new int[numFrames * 2 + 1];
        frameToRollIndex = new int[numFrames];
        frameScores = new int[numFrames];
        FINAL_FRAME = numFrames - 1;
    }

    public void onRoll(int roll) {
        if (gameStatus != GameStatus.GAME_OVER) {
            if (onValidRoll(roll)) {
                rolls[currentRollNum++] = roll;
                currentScore = score();
                gameStatus = gameStatus();
            } else {
                gameStatus = GameStatus.GAME_ERROR;
            }
        }
    }

    private boolean onValidRoll(int roll) {
        if (advanceFrame(currentFrame)) {
            frameToRollIndex[++currentFrame] = currentRollNum + 1; // todo: setting this twice, hmm
        }
        return roll >=0
                && (frameSum(currentFrame, roll) <= ALL_PINS
                    || (currentFrame == FINAL_FRAME
                        && frameSum(currentFrame, roll) <= FINAL_FRAME_MAX_SCORE)) ;
    }

    private boolean advanceFrame(int frame) {
        if (frame == FINAL_FRAME) return false;
        return isStrike(frameToRollIndex[frame])
                || rollsInFrame(frame) == FRAME_SIZE;
    }

    /**
     *  Number of rolls already delivered for this frame
     */
    private int rollsInFrame(int frame) {
        return currentRollNum - frameToRollIndex[frame];
    }

    /**
     *  Simple sum of rolls in frame, excluding bonus points from subsequent frames
     */
    private int frameSum(int frame, int roll) {
        int sum = roll;
        for (int i = frameToRollIndex[frame]; i <= currentRollNum; i++) {
            sum += rolls[i];
        }
        return sum;
    }

    /**
     * Recalculate the whole score array rather than manage marks of non-final frame scoring.
     * Fine for small data sets. Enters and updates frame scores as new roll is delivered.
     * Scorecard rendering preferences are left to presentation code.
     */
    private int score() {
        int score = 0;
        int currentFrame = 0;
        int rollNum = 0;

        while (rollNum < currentRollNum) {
            frameToRollIndex[currentFrame] = rollNum;

            if (isStrike(rollNum)) {
                score += bonusFrameScore(rollNum);
                rollNum += STRIKE_FRAME_SIZE;
            } else if (isSpare(rollNum)) {
                score += bonusFrameScore(rollNum);
                rollNum += FRAME_SIZE;
            } else {
                score += openFrameScore(rollNum);
                rollNum += FRAME_SIZE;
            }
            frameScores[currentFrame] = score;
            if (currentFrame == FINAL_FRAME) break;
            else currentFrame++;
        }
        return score;
    }

    private GameStatus gameStatus() {
        if (currentFrame == FINAL_FRAME) {
            int rollsInFrame = rollsInFrame(currentFrame);
            if ((isStrike(frameToRollIndex[currentFrame]) || isSpare(frameToRollIndex[currentFrame]))) {
                if (rollsInFrame == FINAL_FRAME_MAX_SIZE) return GameStatus.GAME_OVER;
            } else if (rollsInFrame == FRAME_SIZE) {
                return GameStatus.GAME_OVER;
            }
        }
        return GameStatus.GAME_ON;
    }

    private boolean isStrike(int rollNum) {
        return rolls[rollNum] == ALL_PINS;
    }

    private boolean isSpare(int rollNum) {
        return rolls[rollNum] + rolls[rollNum + 1] == ALL_PINS;
    }

    private int bonusFrameScore(int rollNum) {
        return rolls[rollNum] + rolls[rollNum + 1] + rolls[rollNum + 2];
    }

    private int openFrameScore(int rollNum) {
        return rolls[rollNum] + rolls[rollNum+1];
    }

}
