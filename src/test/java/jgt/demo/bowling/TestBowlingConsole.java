package jgt.demo.bowling;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * It is possible, however tedious, to automate tests of the BowlingConsole.
 * Here we demonstrate at least that the usual input-output main sequence
 * works as expected, including input validation and scoreboard printing.
 */
public class TestBowlingConsole {

    BufferedReader testReader;
    PrintWriter testWriter;

    @Before
    public void setup() {
        try {
            PipedWriter testPipedWriter = new PipedWriter();
            PipedWriter gamePipedWriter = new PipedWriter();

            PipedReader gamePipedReader = new PipedReader(testPipedWriter);
            PipedReader testPipedReader = new PipedReader(gamePipedWriter);

            BufferedReader gameReader = new BufferedReader(gamePipedReader);
            PrintWriter gameWriter = new PrintWriter(gamePipedWriter);

            testReader = new BufferedReader(testPipedReader);
            testWriter = new PrintWriter(testPipedWriter);

            BowlingConsole game = new BowlingConsole(new ConsoleWrapper(gameReader, gameWriter), 3);

            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                game.runGame();
            });
            t.start();

        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }

    @Test
    public void testConsole() {
        try {
            String namePrompt = testReader.readLine();
            assertEquals("Please enter your name: ", namePrompt);

            testWriter.printf("JGT%n");
            String blank = testReader.readLine();
            String firstRollPrompt = testReader.readLine();
            assertEquals("OK, JGT, lets bowl! Roll your first ball: ", firstRollPrompt);

            testWriter.printf("X %n");

            String s = testReader.readLine();
            assertEquals("Invalid input.", s);

            s = testReader.readLine();
            assertEquals("At the prompt, enter each roll as a number between 0 and 10, ", s);

            s = testReader.readLine();
            assertEquals("  Rolls must to 10 or less in each frame except the last.", s);
            blank = testReader.readLine();

            s = testReader.readLine();
            assertEquals("Roll again: ", s);

            testWriter.printf("10 %n");

            s = testReader.readLine();
            assertEquals("Game status: GAME_ON ", s);

            s = testReader.readLine();
            assertEquals("Player: JGT | Frame: 1 | Score: 10 ", s);

            s = testReader.readLine();
            assertEquals("Frame  Roll(s)    Score", s);

            s = testReader.readLine();
            assertEquals("1      10           10 ", s);

            testWriter.close();
            testReader.close();

        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }
}
