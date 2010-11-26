rm *.class
javac src/main/java/*.java
mv src/main/java/*.class .
java -jar tools/PlayGame.jar maps/map23.txt 5000000 200 log.txt "java -Dlog=false -Ddebug=true MyBot" "java -jar example_bots/RageBot.jar" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
