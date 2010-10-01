javac src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map23.txt 50000 200 log.txt "java -Dlog= MyBot" "java -jar example_bots/RageBot.jar" > game.txt 
cat game.txt | java -jar tools/ShowGame.jar
