#!/bin/bash

basedir="$(cd "$(dirname $0)//.." && pwd)"

java -cp basedir/lib/*.jar jgt.demo.bowling.BowlingGame
