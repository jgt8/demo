# Demo

## Requirement Statement
Write a command line application that prompts its user for each individual roll in a one-player bowling game. The application should inform its user of the game status after every input.

## Background
Details of how to score a game of bowling:
* https://en.wikipedia.org/wiki/Ten-pin_bowling#Traditional_scoring 
* https://www.topendsports.com/sport/tenpin/scoring.htm
* https://www.kidslearntobowl.com/how-to-keep-score/

## Discussion
BowlingGame accepts a username and a desired number of frames to play. If the username is 
omitted, a default "anonymous" value is set. If the frame count is omitted, the standard 10 
frames is set. The score is updated on each roll, with bonus frames re-scored as the 
bonus rolls are delivered. While there appear to be player conventions around postponing score 
display until all the bonus rolls are available, this is a indeed a display concern, and need 
not complicate the score calculation itself.
 
BowlingConsole prints user prompts and messages, passes input to BowlingGame, and formats the 
output to provide an ongoing game status snapshot to the user. The output format is as follows:

```
Game status: GAME_ON
Player: 123 | Frame: 2 | Score: 32
Frame  Roll(s)    Score
1       9  1        21
2      11           32

Roll again: 
```

Since no specifics are present in the requirements, Frames are presented vertically rather than in 
the traditional horizontal format. Further, the 
non-numeric marks often used to flag strikes, spares, etc. are here avoided. These seem to be 
particularly designed to facilitate manual scoring on paper, and would present a kind of visual
skeuomorphism for a tool such as this. Likewise, the "superstitious" withholding of score display 
while a strike run is in progress is ignored. It is not anticipated that superstitious or finicky, 
tradition-minded bowlers are likely users at this time. All of this can be revisited after user 
input, of course.

## To Build and Run
A wrapper script is included to run the tool in a unix-like environment. The script expects to 
find the required jarfile in ../lib, relative to its own "physical" location. (A Windows script 
is not included, alas, as the author has no access to a Windows workstation at this time.)

To run from the project root:
```
mvn clean install
./target/demo-1.0.0-SNAPSHOT-package/bin/bowling-alone.sh 
```
A symlink could be installed in a user's own ~/bin folder, and should work as well.

## Technical Notes
The anticipated usage of a single-user scoring tool is that it will be invoked, and scores will be
entered manually, by a user at a console. However, a bowling scorekeeper is a more general
application that may be useful as a plugin in other contexts. The console
handling and game management concerns are separated here.

At present geared to a single-user, single-threaded context, BowlingGame is not thread-safe. It 
maintains, and indeed _exposes_ mutable internal state. Thread-safety would require some trivial 
modifications, some of which are noted in a comment at the top of BowlingGame.java.

Multiple output formats and preferences could be supported via command-line switches, should users 
exhibit any such interest. Adding color would help guide the eye as the game progresses.

Scoring variants such as used for international or Olympic play might also be implemented based 
on user feedback.

