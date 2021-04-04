# Demo

## Requirement Statement
Write a command line application that prompts its user for each individual roll in a one-player bowling game. The application should inform its user of the game status after every input.

## Background
Details of how to score a game of bowling:
* https://en.wikipedia.org/wiki/Ten-pin_bowling#Traditional_scoring 
* https://www.topendsports.com/sport/tenpin/scoring.htm
* https://www.kidslearntobowl.com/how-to-keep-score/

## Discussion
The anticipated usage of a single-user scoring tool is that it will be invoked, and scores will be
entered manually, by a user at a console. However, a bowling scorekeeper is a more general
application that may be useful in other contexts. The console
handling and game management concerns are separated here.

_BowlingGame_ accepts a username and a desired number of frames to play. If the username is 
omitted, a default "anonymous" value is set. If the frame count is omitted, the standard 10 
frames is set. The score is updated on each roll, with bonus frames re-scored as the 
bonus rolls are delivered. While there appear to be player conventions around postponing score 
display until all the bonus rolls are available, this is a indeed a display concern, and need 
not complicate the score calculation itself.
 
_BowlingConsole_ prints user prompts and messages, passes input to BowlingGame, and formats the 
output to provide an ongoing game status snapshot to the user. The output format is as follows:

```
Game status: GAME_ON
Player: 123 | Frame: 2 | Score: 26
Frame  Roll(s)    Score
1       9  1        18
2       8           26

Roll again: 
```

Since no specifics are present in the requirements, frames are presented vertically, as is 
natural to the scrolling console, rather than in the traditional horizontal format. Further, 
the non-numeric marks often used to flag strikes, spares, etc. are here avoided. These seem to be 
particularly designed to facilitate manual scoring on paper, and would present a kind of
skeuomorphism for a tool such as this. Likewise, the "superstitious" withholding of score display 
while a strike run is in progress is ignored. It is not anticipated that superstitious or finicky, 
tradition-minded bowlers are likely users at this time. All of this can be revisited after user 
feedback, of course.

Although at present geared to a single-user, single-threaded context, BowlingGame hides 
its internal, mutable state, publishing only a series of separate, independent Scoreboard objects. 
While this imposes a small cost in memory usage, 
and might appear a trifle over-engineered for such a simple application, it 
seems a reasonable price to avoid needing to alter the client-facing interface at a later 
date, should multi-user, multi-threaded usage come into scope.

## Potential extensions

Monitoring and logging facilities have been omitted, as these seem overkill for a simple, 
single-user console app which prints all critical state to standard output.

The game maintains no state between invocations. This is would be a significant scope expansion.

Multiple output formats and preferences could be supported via command-line switches, should users 
exhibit any such interest. Adding color would help guide the eye as the game progresses.

Scoring variants such as used for international or Olympic play might also be implemented based 
on user feedback.

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