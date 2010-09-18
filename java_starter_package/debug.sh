javac src/*.java
cp src/*.class .
java -jar tools/PlayGame.jar maps/map7.txt 100000000 10000000 log.txt "java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 MyBot" "java -jar example_bots/DualBot.jar" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
