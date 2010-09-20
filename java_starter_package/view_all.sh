javac src/*.java
mv src/*.class .

for map in maps/*
do
echo $map
java -jar tools/PlayGame.jar $map 1000 200 log.txt "java -Ddebug=true MyBot" "java -jar example_bots/ProspectorBot.jar" > game.txt 
cat game.txt | java -jar tools/ShowGame.jar
done

