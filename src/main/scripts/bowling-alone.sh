#!/bin/bash

scriptdir=$(dirname $(readlink -f $0))

java -cp $scriptdir/lib/*.jar jgt.demo.bowling.BowlingGame
