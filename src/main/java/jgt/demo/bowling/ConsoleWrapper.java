package jgt.demo.bowling;

import java.io.*;

/*
 * The java Console is unavailable if the system is not started from an actual console, so
 * to enable automation, especially testing, wrap it in a simple facade and swap so that
 * it can be swapped for a Reader/Writer pair in such cases.
 */
public class ConsoleWrapper {

    private BufferedReader reader;
    private PrintWriter writer;
    private Console console;

    public ConsoleWrapper(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public ConsoleWrapper(Console console) {
        this.console = console;
    }

    public String readLine(String format, Object... args) {
        if (console != null) {
            return console.readLine(format, args);
        } else {
            try {
                writer.printf(format+"%n", args);
                return reader.readLine();
            } catch (IOException x) {
                throw new IOError(x);
            }
        }
    }

    public void printf(String format, Object... args) {
        if (console != null) {
            console.printf(format, args);
        } else {
            writer.printf(format, args);
        }

    }
}

