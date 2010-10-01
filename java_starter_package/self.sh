javac -g src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map15.txt 50000 200 log.txt "java -Dlog= MyBot" "java NeutralBot" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
