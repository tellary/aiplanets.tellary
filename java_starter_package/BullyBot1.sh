javac src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map1.txt 500 200 log.txt "java -Dlog=true MyBot" "java -jar example_bots/BullyBot.jar" > game.txt 
cat game.txt | java -jar tools/ShowGame.jar
