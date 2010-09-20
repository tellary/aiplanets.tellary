javac -g src/*.java
cp src/*.class .
java -jar tools/PlayGame.jar maps/map37.txt 100000000 10000000 log.txt "java -Ddebug=true -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 MyBot" "java -jar example_bots/BullyBot.jar" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
