rm *.class
javac src/main/java/*.java
mv src/main/java/*.class .
java -jar tools/PlayGame.jar maps/map23.txt 500 200 log.txt "java -Dlog=false -Ddebug=false MyBot" "java -jar example_bots/bot20101011.jar" > game.txt

cat game.txt | java -jar tools/ShowGame.jar
