#!/bin/bash
java JLex.Main Scanner.lex
java java_cup.Main Parser.cup
rm Yylex.java
mv Scanner.lex.java Yylex.java
javac *.java
