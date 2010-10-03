javac src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map92.txt 500 200 log.txt "java -Dlog= MyBot" "java -jar example_bots/RageBot.jar" > game.txt 
cat game.txt | java -jar tools/ShowGame.jar
