javac src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map100.txt 1000 1000 log.txt "java -Ddebug=true MyBot" "java -jar example_bots/RageBot.jar" > game.txt 
cat game.txt | java -jar tools/ShowGame.jar
