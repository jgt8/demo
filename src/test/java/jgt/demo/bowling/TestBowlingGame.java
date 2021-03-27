package jgt.demo.bowling;

import org.junit.Test;
import org.junit.Assert.*;

import java.util.Arrays;
import jgt.demo.bowling.BowlingGame.GameStatus;

import static org.junit.Assert.assertEquals;


public class TestBowlingGame {

    BowlingGame game = new BowlingGame(5);

    @Test
    public void testInvalidRoll_rollGt10() {
        int[] rolls = new int[] {0, 11};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, game.gameStatus);
        assertEquals(0, game.rolls[1]);
        assertEquals(0, game.currentScore);
    }

    @Test
    public void testInvalidRoll_rollLt0() {
        int[] rolls = new int[] {0, -1};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, game.gameStatus);
        assertEquals(0, game.rolls[1]);
        assertEquals(0, game.currentScore);
    }

    @Test
    public void testInvalidRoll_finalRollLt0() {
        game = new BowlingGame(2);
        int[] rolls = new int[] {0, 0, 10, 10, -1};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, game.gameStatus);
        assertEquals(0, game.rolls[4]);
        assertEquals(20, game.currentScore);
    }

    @Test
    public void testInvalidRoll_frameGt10() {
        int[] rolls = new int[] {6, 6};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, game.gameStatus);
        assertEquals(0, game.rolls[1]);
        assertEquals(6, game.currentScore);
    }

    @Test
    public void testInvalidRoll_finalFrameGt30() {
        game = new BowlingGame(2);
        int[] rolls = new int[] {0, 0, 10, 10, 11};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_ERROR, game.gameStatus);
        assertEquals(10, game.rolls[3]);
        assertEquals(0, game.rolls[4]);
        assertEquals(20, game.currentScore);
    }

    @Test
    public void testOneOpenFrame() {
        game.onRoll(4);
        game.onRoll(4);
        assertEquals(GameStatus.GAME_ON, game.gameStatus);
        assertEquals(4, game.rolls[1]);
        assertEquals(8, game.frameScores[0]);
        assertEquals(8, game.currentScore);
        assertEquals(0, game.currentFrame);
    }

    @Test
    public void testOneSpare() {
        game.onRoll(5);
        game.onRoll(5);
        game.onRoll(5);
        assertEquals(GameStatus.GAME_ON, game.gameStatus);
        assertEquals(5, game.rolls[2]);
        assertEquals(15, game.frameScores[0]);
        assertEquals(20, game.currentScore);
        assertEquals(1, game.currentFrame);
    }

    @Test
    public void testOneStrike() {
        game.onRoll(10);
        game.onRoll(5);
        game.onRoll(5);
        assertEquals(GameStatus.GAME_ON, game.gameStatus);
        assertEquals(5, game.rolls[2]);
        assertEquals(20, game.frameScores[0]);
        assertEquals(30, game.currentScore);
        assertEquals(1, game.currentFrame);
    }

//    @Test
//    public void test() {
//
//    }

    @Test
    public void testScratchGame() {
        int[] rolls = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, game.gameStatus);
        assertEquals(4, game.currentFrame);
        assertEquals(0, game.currentScore);
    }

    @Test
    public void testWeakGame() {
        int[] rolls = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, game.gameStatus);
        assertEquals(4, game.currentFrame);
        assertEquals(10, game.currentScore);
    }

    @Test
    public void testPerfectGame() {
        game = new BowlingGame(); // standard 10 frames
        int[] rolls = new int[] {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, game.gameStatus);
        assertEquals(9, game.currentFrame);
        assertEquals(300, game.currentScore);
    }

    @Test
    public void testMixedGame_finalStrike() {
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 10, 3, 2};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, game.gameStatus);
        assertEquals(4, game.currentFrame);
        assertEquals(1, game.frameScores[0]);
        assertEquals(21, game.frameScores[1]);
        assertEquals(38, game.frameScores[2]);
        assertEquals(45, game.frameScores[3]);
        assertEquals(60, game.frameScores[4]);
        assertEquals(60, game.currentScore);
    }

    @Test
    public void testMixedGame_finalSpare() {
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 5, 5, 2};
        doRolls(game, rolls);
        assertEquals(GameStatus.GAME_OVER, game.gameStatus);
        assertEquals(4, game.currentFrame);
        assertEquals(57, game.currentScore);
    }

    private static void doRolls(BowlingGame game, int[] rolls) {
        for(int r : rolls) {
            game.onRoll(r);
            // showGame(game, r);
        }
    }

    private static void showGame(BowlingGame game, int lastRoll) {
        System.out.println(game.gameStatus);
        System.out.println("Frame: "+ game.currentFrame);
        System.out.println("Last roll: "+ lastRoll);
        System.out.println("Num rolls: "+ game.currentRollNum);
        System.out.println("Score: " + game.currentScore);
        System.out.println("Rolls: "+ Arrays.toString(game.rolls));
        System.out.println("FrameIdx: "+ Arrays.toString(game.frameToRollIndex));
        System.out.println("Scores: "+ Arrays.toString(game.frameScores));
        System.out.println();
    }

}

