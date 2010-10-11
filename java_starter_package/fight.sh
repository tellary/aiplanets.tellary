javac src/*.java
mv src/*.class .
java -jar tools/PlayGame.jar maps/map10.txt 500 2000 log.txt "java -Dlog= MyBot" "java -jar example_bots/bot20101005.1.jar" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
