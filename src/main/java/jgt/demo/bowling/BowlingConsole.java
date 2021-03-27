package jgt.demo.bowling;

import java.io.Console;
import java.util.Arrays;

import jgt.demo.bowling.BowlingGame.BowlingGameState;
import jgt.demo.bowling.BowlingGame.GameStatus;

public class BowlingConsole {

    private static final String WELCOME = "something something \n"; // usage: enter integer rolls or commands A, B C
    private static final String USAGE = "something something \n"; // usage: enter integer rolls or commands A, B C
    private final static int MAX_INPUT_ERRORS = 3;

    private final Console console;

    public static void main(String[] args) {


        // parse cmd line switches
        // -q quiet output
        // -i international scoring

        Console console = System.console();
        if (console == null) {
            exitOnError(1, "Console unavailable");
        } else {
            console.printf(WELCOME);

            BowlingConsole bowlingConsole = new BowlingConsole(console); // TODO args
            bowlingConsole.runGame();

            // cleanup?
        }
    }

    private BowlingConsole(Console console) {
        this.console =  console;
    }

    private void runGame() {
        String prompt = "Hi, please enter your name: ";
        String input = null;
        String userName = null;

        for (int i = 0; i < 3; i++) {
            input = console.readLine(prompt);
            if (validateUserName(input)) {
                userName = input;
                break;
            } else {
                prompt = "Please enter a username of at least three characters: ";

            }
        }
        if (userName == null) {
            console.printf("Bye.\n\n");
            exitOnError(2, "Bad user name input");
        }

        BowlingGame game = new BowlingGame(userName);
        BowlingGameState gameState;
        int consecutiveInputErrors = 0;

        prompt = "OK, " + userName +", lets bowl! Roll your first ball: ";
        while ((input = console.readLine(prompt)) != null
                && consecutiveInputErrors < MAX_INPUT_ERRORS) {
            try {
                int rollScore = Integer.parseInt(input); // todo maybe trim whitespace
                consecutiveInputErrors = 0;
                gameState = game.onRoll(rollScore);
                if (gameState.gameStatus == GameStatus.GAME_ERROR) {
                    console.printf("Invalid input.\n");
                    console.printf(USAGE);
                }
                console.printf(formatGameState(gameState) +"\n"); // todo
            } catch (NumberFormatException e1) {
                consecutiveInputErrors++;
                console.printf("Invalid input.\n");
                console.printf(USAGE);
            }
            prompt = "Roll again: ";
            // if game over, offer a new game
        }
        console.printf("Bye.\n\n");
    }

    private String formatGameState(BowlingGameState state) {
        StringBuffer s = new StringBuffer("Game status: ").append(state.gameStatus).append("\n")
                .append("Player: ").append(state.playerID)
                .append(" | Frame: ").append(state.currentFrame)
                .append(" | Last Roll: ").append(state.rolls[state.currentRoll])
                .append(" | Score: ").append(state.currentScore).append("\n")
                .append("Rolls: ").append(Arrays.toString(state.rolls)).append("\n")
                .append("FrameIndex: ").append(Arrays.toString(state.frameIndex)).append("\n")
                .append("FrameScores: ").append(Arrays.toString(state.frameScores)).append("\n");

        return s.toString();
    }

    private static void exitOnError(int code, String message) {
        System.err.println(message);
        System.exit(code);
    }

    private boolean validateUserName(String s) {
        return (s != null && s.length() > 2);
    }
}
