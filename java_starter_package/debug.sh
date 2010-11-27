javac -g src/main/java/*.java
cp src/main/java/*.class .
java -jar tools/PlayGame.jar maps/map23_small.txt 100000000 10000000 log.txt "java -Ddebug=true -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 MyBot" "java -jar example_bots/bot20101010.jar" > game.txt
cat game.txt | java -jar tools/ShowGame.jar
