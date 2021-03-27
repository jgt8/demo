package jgt.demo.bowling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BowlingGame {

    String playerID;
    String gameID;

    int[] rolls;
    int currentRollNum = 0; // last filled index in rolls[]

    int[] frameToRollIndex; // for each frame, index of its first roll in rolls[]
    int currentFrame = 0;

    int[] frameScores; // cumulative score at each frame
    int currentScore = 0; // total score

    final int ALL_PINS = 10;
    final int FIRST_FRAME = 0;
    final int FINAL_FRAME;
    final int FRAME_SIZE = 2;
    final int STRIKE_FRAME_SIZE = 1;
    final int FINAL_FRAME_MAX_SIZE = 3;
    final int FINAL_FRAME_MAX_SCORE = 30;

    static final int STANDARD_FRAME_COUNT = 10;
    static final String NO_USER_NAME = "AnonymousCoward";

    GameStatus gameStatus = GameStatus.GAME_ON;

    public enum GameStatus {
        GAME_ON, GAME_OVER, GAME_ERROR;
    }

    /**
     * A snapshot of the current game state, preserving the working state against external modification
     */
    public class BowlingGameState {
        String playerID = BowlingGame.this.playerID;
        String gameID = BowlingGame.this.gameID;
        GameStatus gameStatus = BowlingGame.this.gameStatus;

        int[] rolls = Arrays.copyOf(BowlingGame.this.rolls, BowlingGame.this.rolls.length);
        int currentRoll = BowlingGame.this.currentRollNum;

        int[] frameIndex = Arrays.copyOf(BowlingGame.this.rolls, BowlingGame.this.frameToRollIndex.length);
        int currentFrame = BowlingGame.this.currentFrame;

        int[] frameScores = Arrays.copyOf(BowlingGame.this.rolls, BowlingGame.this.frameScores.length);
        int currentScore = BowlingGame.this.currentScore;
    }

    public BowlingGame() {
        this(NO_USER_NAME, STANDARD_FRAME_COUNT);
    }

    public BowlingGame(String userName) {
        this(userName, STANDARD_FRAME_COUNT);
    }

    public BowlingGame(int numFrames) {
        this(NO_USER_NAME, numFrames);
    }

    public BowlingGame(String userName, int numFrames) {
        playerID = userName;
        gameID = "TODO"; // tODO generate unique (ish) short ID
        rolls = new int[numFrames * 2 + 1];
        frameToRollIndex = new int[numFrames];
        frameScores = new int[numFrames];
        FINAL_FRAME = numFrames - 1;
    }

    public BowlingGameState onRoll(int roll) {
        if (gameStatus != GameStatus.GAME_OVER) {
            if (onValidRoll(roll)) {
                rolls[currentRollNum++] = roll;
                currentScore = score();
                gameStatus = gameStatus();
            } else {
                gameStatus = GameStatus.GAME_ERROR;
            }
        }
        return new BowlingGameState();
    }

    private boolean onValidRoll(int roll) {
        if (advanceFrame(currentFrame)) {
            frameToRollIndex[++currentFrame] = currentRollNum + 1; // todo setting thsis twice?
        }
        return (roll >=0 && frameSum(currentFrame, roll) <= ALL_PINS)
                    || (currentFrame == FINAL_FRAME
                        && roll >=0 && frameSum(currentFrame, roll) <= FINAL_FRAME_MAX_SCORE);
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
     * Fine for small data sets.
     * Enters and updates frame scores as new roll is delivered.
     * Scorecard rendering habits are left to presentation code.
     */
    private int score() {
        int score = 0;
        int currentFrame = 0;

        for (int rollNum = 0; rollNum < currentRollNum;) {
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

    public static void main(String[] args) {
        BowlingGame game = new BowlingGame(5);

//        game.rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 10, 3, 2}; // 60
//        game.currentRollNum = game.rolls.length;
//        showGame(game, game.rolls[game.currentRollNum -1]);
//
//        game = new BowlingGame();
//        game.rolls = new int[]       {0, 1, 5, 5, 10, 3, 4,   5, 5, 2}; // 57
//        game.currentRollNum = game.rolls.length;
//        showGame(game, game.rolls[game.currentRollNum -1]);

//        game = new BowlingGame("Greg");
//        game.rolls = new int[]       {0, 1, 5, 5, 10, 3, 4,   5}; // incomplete last frame
//        showGame(game);
//
//
//        game = new BowlingGame("Greg");
//        game.rolls = new int[]       {0, 1, 5, 5}; // incomplete
//        showGame(game);

        game = new BowlingGame(5);
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 10, 3, 2}; // 60
        // int[] rolls = new int[]  {0, 1, 5, 5, 10, 3, 4,   5, 5, 2}; // 57
        //int[] rolls = new int[] {1, 1, 10, 1, 1}; // 16

        doRolls(game, rolls);

//
//
//        game = new BowlingGame();
//        rolls = new int[] {0, 1, 11, 11, 11}; // invalid rolls
//
//        doRolls(game, rolls);

    }

    private static void doRolls(BowlingGame game, int[] rolls) {
        for(int r : rolls) {
            game.onRoll(r);
            showGame(game, r);
        }
    }

    private static void showGame(BowlingGame game, int lastRoll) {
        System.out.println(game.gameStatus);
        System.out.println("Frame: "+ game.currentFrame);
        System.out.println("Last roll: "+ lastRoll);
        System.out.println("Num rolls: "+ game.currentRollNum);
        System.out.println("Score: " + game.currentScore);
        System.out.println("Rolls: "+ Arrays.toString(game.rolls));
        System.out.println("FrameX: "+ Arrays.toString(game.frameToRollIndex));
        System.out.println("Scores: "+ Arrays.toString(game.frameScores));
        System.out.println();
    }
}
