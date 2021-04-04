package jgt.demo.bowling;

import jgt.demo.bowling.BowlingGame.GameStatus;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class TestBowlingGame {

    BowlingGame game = new BowlingGame(5);

    @Test
    public void testInvalidRoll_rollGt10() {
        int[] rolls = new int[] {0, 11};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, scoreboard.gameStatus);
        assertEquals(0, scoreboard.rolls[1]);
        assertEquals(0, scoreboard.currentScore);
    }

    @Test
    public void testInvalidRoll_rollLt0() {
        int[] rolls = new int[] {0, -1};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, scoreboard.gameStatus);
        assertEquals(0, scoreboard.rolls[1]);
        assertEquals(0, scoreboard.currentScore);
    }

    @Test
    public void testInvalidRoll_finalRollLt0() {
        game = new BowlingGame(2);
        int[] rolls = new int[] {0, 0, 10, 10, -1};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, scoreboard.gameStatus);
        assertEquals(0, scoreboard.rolls[4]);
        assertEquals(20, scoreboard.currentScore);
    }

    @Test
    public void testInvalidRoll_frameGt10() {
        int[] rolls = new int[] {6, 6};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, scoreboard.gameStatus);
        assertEquals(0, scoreboard.rolls[1]);
        assertEquals(6, scoreboard.currentScore);
    }

    @Test
    public void testInvalidRoll_finalFrameGt30() {
        game = new BowlingGame(2);
        int[] rolls = new int[] {0, 0, 10, 10, 11};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, scoreboard.gameStatus);
        assertEquals(10, scoreboard.rolls[3]);
        assertEquals(0, scoreboard.rolls[4]);
        assertEquals(20, scoreboard.currentScore);
    }

    @Test
    public void testOneOpenFrame() {
        game.onRoll(4);
        BowlingGame.Scoreboard scoreboard = game.onRoll(4);
        assertEquals(GameStatus.GAME_ON, scoreboard.gameStatus);
        assertEquals(4, scoreboard.rolls[1]);
        assertEquals(8, scoreboard.frameScores[1]);
        assertEquals(8, scoreboard.currentScore);
        assertEquals(1, scoreboard.currentFrame);
    }

    @Test
    public void testOneSpare() {
        game.onRoll(5);
        game.onRoll(5);
        BowlingGame.Scoreboard scoreboard = game.onRoll(5);
        assertEquals(GameStatus.GAME_ON, scoreboard.gameStatus);
        assertEquals(5, scoreboard.rolls[2]);
        assertEquals(15, scoreboard.frameScores[1]);
        assertEquals(20, scoreboard.currentScore);
        assertEquals(2, scoreboard.currentFrame);
    }

    @Test
    public void testOneStrike() {
        game.onRoll(10);
        game.onRoll(5);
        BowlingGame.Scoreboard scoreboard = game.onRoll(5);
        assertEquals(GameStatus.GAME_ON, scoreboard.gameStatus);
        assertEquals(5, scoreboard.rolls[2]);
        assertEquals(20, scoreboard.frameScores[1]);
        assertEquals(30, scoreboard.currentScore);
        assertEquals(2, scoreboard.currentFrame);
    }

//    @Test
//    public void test() {
//
//    }

    @Test
    public void testScratchGame() {
        int[] rolls = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, scoreboard.gameStatus);
        assertEquals(5, scoreboard.currentFrame);
        assertEquals(0, scoreboard.currentScore);
    }

    @Test
    public void testWeakGame() {
        int[] rolls = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, scoreboard.gameStatus);
        assertEquals(5, scoreboard.currentFrame);

        assertEquals(2, scoreboard.frameScores[1]);
        assertEquals(4, scoreboard.frameScores[2]);
        assertEquals(6, scoreboard.frameScores[3]);
        assertEquals(8, scoreboard.frameScores[4]);
        assertEquals(10, scoreboard.frameScores[5]);

        assertEquals(10, scoreboard.currentScore);
    }

    @Test
    public void testPerfectGame() {
        game = new BowlingGame(); // standard 10 frames
        int[] rolls = new int[] {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, scoreboard.gameStatus);
        assertEquals(10, scoreboard.currentFrame);
        assertEquals(300, scoreboard.currentScore);
    }

    @Test
    public void testMixedGame_finalStrike() {
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 10, 3, 2};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, scoreboard.gameStatus);
        assertEquals(1, scoreboard.frameScores[1]);
        assertEquals(21, scoreboard.frameScores[2]);
        assertEquals(38, scoreboard.frameScores[3]);
        assertEquals(45, scoreboard.frameScores[4]);
        assertEquals(60, scoreboard.frameScores[5]);
        assertEquals(60, scoreboard.currentScore);
    }

    @Test
    public void testMixedGame_finalSpare() {
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 5, 5, 2};
        BowlingGame.Scoreboard scoreboard = doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, scoreboard.gameStatus);
        assertEquals(5, scoreboard.currentFrame);
        assertEquals(57, scoreboard.currentScore);
    }

    private static BowlingGame.Scoreboard doRolls(BowlingGame game, int[] rolls) {
       BowlingGame.Scoreboard scoreboard = null;
        for(int r : rolls) {
           scoreboard = game.onRoll(r);
            showGame(scoreboard, r);
        }
        return scoreboard;
    }

    private static void showGame(BowlingGame.Scoreboard scoreboard, int lastRoll) {
        System.out.println(scoreboard.gameStatus);
        System.out.println("Frame: "+ scoreboard.currentFrame);
        System.out.println("Last roll: "+ lastRoll);
        System.out.println("Num rolls: "+ scoreboard.currentRollNum);
        System.out.println("Score: " + scoreboard.currentScore);
        System.out.println("Rolls: "+ Arrays.toString(scoreboard.rolls));
        System.out.println("FrameIdx: "+ Arrays.toString(scoreboard.frameToRollIndex));
        System.out.println("Scores: "+ Arrays.toString(scoreboard.frameScores));
        System.out.println();
    }

}

