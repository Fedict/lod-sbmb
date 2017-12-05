#!/bin/sh
SBMB="java -jar lod-sbmb.jar"
ELI=http://www.ejustice.just.fgov.be/eli
OUT=year
GET=-g

$SBMB $GET -s 2017 -b $ELI -t ORD -n ordonnantie -f ordonnance -o $OUT/ord -w 10
$SBMB $GET -s 2017 -b $ELI -t CONST -n grondwet -f constitution -o $OUT/const -w 10
$SBMB $GET -s 2017 -b $ELI -t LAW -n wet -f loi -o  $OUT/law -w 10
$SBMB $GET -s 2017 -b $ELI -t DECREE -n decreet -f decret -o  $OUT/decree -w 10
