package jgt.demo.bowling;

import jgt.demo.bowling.BowlingGame.GameStatus;

import java.io.Console;
import java.util.Formatter;

public class BowlingConsole {

    private static final String USAGE = "At the prompt, enter each roll as a number between 0 and 10.%n";
    private static final String WELCOME = "%nHI, WELCOME TO BOWLING ALONE. Here's how it works:%n  "
            + USAGE + "  The system will keep track of your score.%n";

    private final static String FIRST_ROLL_PROMPT = "%nOK, %s, lets bowl! Roll your first ball: ";
    private final static String BAD_INPUT_MSG = "Invalid input.%n";
    private final static int MAX_INPUT_ERRORS = 3;

    private final Console console;

    public static void main(String[] args) {

        Console console = System.console();
        if (console == null) {
            exitOnError(1, "Console unavailable");
        } else {
            console.printf(WELCOME);
            new BowlingConsole(console).runGame();
        }
    }

    private BowlingConsole(Console console) {
        this.console =  console;
    }

    private void runGame() {
        String prompt = "Please enter your name: ";
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

        boolean keepPlaying = true;

        while (keepPlaying) {
            BowlingGame game = new BowlingGame(userName);
            int consecutiveInputErrors = 0;

            prompt = FIRST_ROLL_PROMPT;
            while ((input = console.readLine(prompt, userName)) != null
                    && consecutiveInputErrors < MAX_INPUT_ERRORS) {
                try {
                    int rollScore = Integer.parseInt(input.trim());
                    consecutiveInputErrors = 0;
                    game.onRoll(rollScore);
                    if (game.gameStatus == GameStatus.GAME_ERROR) {
                        console.printf("Game status: %s %n", game.gameStatus);
                        console.printf(BAD_INPUT_MSG + USAGE);

                    } else {
                        showScoreBoard(game, console);
                    }
                } catch (NumberFormatException e1) {
                    consecutiveInputErrors++;
                    console.printf(BAD_INPUT_MSG + USAGE);
                }

                if (game.gameStatus == GameStatus.GAME_OVER) {
                    console.printf("%n********%nGood bowling! You scored %d on that game.", game.currentScore);
                    input = console.readLine("%nAnother game? (Y or N): ");
                    if (input != null && input.trim().equalsIgnoreCase("Y")) {
                        console.printf("One more game then.");
                    } else {
                        console.printf("Thanks for playing!%n");
                        keepPlaying = false;
                    }
                    break;
                } else {
                    prompt = "%nRoll again: ";
                }
            }
        }
        console.printf("Bye.\n\n");
    }

    private void showScoreBoard(BowlingGame game, Console console) {
        console.printf("Game status: %s %n", game.gameStatus);
        console.printf("Player: %s | Frame: %d | Score: %d %n",
                game.playerID, game.currentFrame+1, game.currentScore);

        String headerFmt = "%-6s %-10s %-5s%n";
        String frameFmt = "%-6d %-10s %4d %n";
        String rollFmt = "%2d ";

        console.printf(headerFmt, "Frame", "Roll(s)", "Score");

        for (int f = 0; f <= game.currentFrame; f++) {
            StringBuilder rollsInFrame = new StringBuilder();
            Formatter rollsFormatter = new Formatter(rollsInFrame);
            int frameEnd = (f == game.FINAL_FRAME || f == game.currentFrame)
                    ? game.currentRollNum
                    : game.frameToRollIndex[f+1];
            for ( int r = game.frameToRollIndex[f]; r < frameEnd; r++) {
                rollsFormatter.format(rollFmt, game.rolls[r]);
            }
            console.printf(frameFmt, f+1, rollsInFrame.toString(), game.frameScores[f]);
        }
    }

    private static void exitOnError(int code, String message) {
        System.err.println(message);
        System.exit(code);
    }

    private boolean validateUserName(String s) {
        return (s != null && s.length() > 2);
    }
}
