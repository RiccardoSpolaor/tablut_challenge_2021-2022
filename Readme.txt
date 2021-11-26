## Requirements: ##
* Java 8 (https://docs.oracle.com/javase/8/docs/api/)
* Gson 2.2.2 (https://www.javadoc.io/doc/com.google.code.gson/gson/2.2.2/com/google/gson/Gson.html)
* commons-cli-1.4.jar (https://mvnrepository.com/artifact/commons-cli/commons-cli/1.4)

## Running: ##
In order to start the white player move to the "jars" folder and run the command:
java -jar runmyplayer.jar white

In order to start the black player move to the "jars" folder and run the command:
java -jar runmyplayer.jar black

You can run players with extra parameters:
java -jar runmyplayer.jar <role> <timeout> <ip-address>

* <role> : role of the player in the game (black or white)
* <timeout> : time for the player to perform a move (default: 60, not mandatory)
* <ip-address> : ip address of the server (default: 127.0.0.1, not mandatory)

## Group members: ##
* Riccardo Spolaor (riccardo.spolaor@studio.unibo.it)
* Facundo Nicolas Maidana (facundo.maidana@studio.unibo.it)