rm *.class
javac src/main/java/*.java
mv src/main/java/*.class .
java -jar tools/PlayGame.jar src/test/resources/map23_small.txt 500 60 log.txt "java -Dlog=false -Ddebug=false DoNothingBot" "java DoNothingBot" > game.txt

cat game.txt | java -jar tools/ShowGame.jar
