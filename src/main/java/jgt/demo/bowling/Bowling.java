package jgt.demo.bowling;

/*
    10 Frames [1 2]
    10 pins
    2 rolls [0 10]
    strike = 10 + 2 rolls
    spare =  10 + one roll
    10th frame extra rolls

    0     1      2    3
    [0 1] [5 5] [10] [3 4] ... [10 10 10]
     0 1   2 3   4    5 6
 */

public class Bowling {

    int score(int[] rolls) {
        int score = 0;
        int currentFrame = 0;
        int currentRoll = 0;

        for (int i = 0; i< rolls.length;) {
            if (isStrike(rolls, i)) {
                score += strikeScore(rolls, i);
                i++;
            }
            else if (isSpare(rolls, i)) {
                score += spareScore(rolls, i);
                // dont double count final rolls, final frame consumes all rolls
                i += 2;
            }
            else {
                score += frameScore(rolls, i);
                i += 2;
            }
            if (currentFrame == 4) break;
            currentFrame++;
        }
        return score;
    }

    boolean isStrike(int[] rolls, int i) {
        return rolls[i] == 10;
    }
    boolean isSpare(int[] rolls, int i) {
        return rolls[i] + rolls[i+1] == 10;
    }

    int strikeScore(int[] rolls, int i) {
        return 10 + rolls[i+1] + rolls[i+2];
    }

    int spareScore(int[] rolls, int i) {
        return 10 + rolls[i+2];
    }

    int frameScore(int[] rolls, int i) {
        return rolls[i] + rolls[i+1];
    }

    public static void main(String[] args) {
        Bowling d = new Bowling();
        int[] rolls = new int[] {0, 1, 5, 5, 10, 3, 4, 10, 3, 2}; // 60
        System.out.println(d.score(rolls));

        rolls = new int[]       {0, 1, 5, 5, 10, 3, 4,   5, 5, 2}; // 57
        System.out.println(d.score(rolls));

    }

}
