package jgt.demo.bowling;

import java.util.Arrays;

public class BowlingGame {

    private String playerID;

    /*
     * Internal game state is mutable and private.
     */
    private int[] rolls;
    private int currentRollNum = 0; // last filled index in rolls[]

    private int[] frameToRollIndex; // for each frame, index of its first roll in rolls[]
    private int currentFrame = 1;

    private int[] frameScores; // cumulative score at each frame
    private int currentScore = 0; // total score

    private final int FIRST_FRAME = 1; // NOTE: caunting frames from 1 not 0, helps manage fullyScoredFrame
    private final int FINAL_FRAME;

    public static final int ALL_PINS = 10;
    public static final int FRAME_SIZE = 2;
    public static final int STRIKE_FRAME_SIZE = 1;
    public static final int FINAL_FRAME_MAX_SIZE = 3;
    public static final int FINAL_FRAME_MAX_SCORE = 30;
    public static final int STANDARD_FRAME_COUNT = 10;

    private static final String ANON_USER = "AnonymousCoward";
    private GameStatus gameStatus = GameStatus.GAME_ON;

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
        frameToRollIndex = new int[numFrames + 1];
        frameScores = new int[numFrames + 1];
        FINAL_FRAME = numFrames;
    }

    /**
     * Accept a player's roll and update the score
     * @param roll
     * @return a snapshot of the game's scoreboard after applying the roll
     */
    public Scoreboard onRoll(int roll) {
        if (gameStatus != GameStatus.GAME_OVER) {
            if (onValidRoll(roll)) {
                rolls[currentRollNum] = roll;
                currentScore = score();
                currentRollNum++;
                gameStatus = gameStatus();
            } else {
                gameStatus = GameStatus.GAME_ERROR;
            }
        }
        return new Scoreboard();
    }

    private boolean onValidRoll(int roll) {
        if (advanceFrame(currentFrame)) {
            frameToRollIndex[++currentFrame] = currentRollNum ;
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

    int fullyScoredFrame = 0;

    /**
     * Incrementally recalculate scores starting with the earliest of any incompletely
     * scored frame or the current frame. Enters and updates frame scores as each new roll
     * is delivered. Scorecard rendering preferences are left to presentation code.
     */
    private int score() {
        int score = frameScores[fullyScoredFrame];
        int frameNum = fullyScoredFrame+1;
        int rollNum = frameToRollIndex[frameNum];

        while (rollNum <= currentRollNum) {
            boolean isBonusFrame = false;
            if (isStrike(rollNum)) {
                score += bonusFrameScore(rollNum);
                rollNum += STRIKE_FRAME_SIZE;
                isBonusFrame = true;
            } else if (isSpare(rollNum)) {
                score += bonusFrameScore(rollNum);
                rollNum += FRAME_SIZE;
                isBonusFrame = true;
            } else {
                score += openFrameScore(rollNum);
                rollNum += FRAME_SIZE;
            }
            frameScores[frameNum] = score;
            if (isFullyScored(frameNum, isBonusFrame)) fullyScoredFrame = frameNum;

            if (frameNum == FINAL_FRAME) break;
            else frameNum++;
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

    // all frames are fully scored 2 rolls after of the frame's first roll
    boolean isFullyScored(int frameNum, boolean isBonusFrame) {
        int extraFrame = isBonusFrame ? 1 : 0;
        return currentRollNum > frameToRollIndex[frameNum] + extraFrame;
    }

    /**
     * A snapshot of the current game state. Mostly immutable.
     */
    public class Scoreboard {
        public final String playerID = BowlingGame.this.playerID;
        public final GameStatus gameStatus = BowlingGame.this.gameStatus;

        protected final int FIRST_FRAME = BowlingGame.this.FIRST_FRAME;
        protected final int FINAL_FRAME = BowlingGame.this.FINAL_FRAME;

        protected final int currentRollNum = BowlingGame.this.currentRollNum;
        public final int currentFrame = BowlingGame.this.currentFrame;

        public final int[] rolls = Arrays.copyOf(BowlingGame.this.rolls, BowlingGame.this.rolls.length);
        public final int[] frameToRollIndex = Arrays.copyOf(BowlingGame.this.frameToRollIndex, BowlingGame.this.frameToRollIndex.length);

        public final int[] frameScores = Arrays.copyOf(BowlingGame.this.frameScores, BowlingGame.this.frameScores.length);
        public final int currentScore = BowlingGame.this.currentScore;
    }

    /**
     * @return a copy of the game state
     */
    public Scoreboard scoreboard() {
        return new Scoreboard();
    }
}
