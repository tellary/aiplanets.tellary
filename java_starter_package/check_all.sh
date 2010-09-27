javac src/*.java
mv src/*.class .

for map in maps/*
do
map_name=`echo $map | sed 's/maps\///'`
echo $map_name
for bot in example_bots/*.jar
do
bot_name=`echo $bot | sed 's/example_bots\///'`
echo $bot_name on $map_name
java -jar tools/PlayGame.jar $map 500 200 log.txt "java MyBot" "java -jar $bot" 2> result_"$bot_name"_on_$map_name 1> check_game_"$bot_name"_on_$map_name
done
done

