#!/bin/sh
# 컴파일 후 실행
set -e
cd "$(dirname "$0")"
javac -encoding UTF-8 -d out src/horserace/*.java
java -cp out horserace.Main
