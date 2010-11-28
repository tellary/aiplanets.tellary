rm *.class
javac src/main/java/*.java
mv src/main/java/*.class .
java -jar tools/PlayGame.jar maps/map10.txt 500000 200 log.txt "java -Dlog=false -Ddebug=true MyBot" "java -jar example_bots/bot20101011.jar" > game.txt

cat game.txt | java -jar tools/ShowGame.jar
