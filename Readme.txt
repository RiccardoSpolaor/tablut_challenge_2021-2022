## Requirements: ##
* Java 8 [API](https://docs.oracle.com/javase/8/docs/api/)
* Gson 2.2.2 [library](https://www.javadoc.io/doc/com.google.code.gson/gson/2.2.2/com/google/gson/Gson.html)
* commons-cli-1.4.jar [library](https://mvnrepository.com/artifact/commons-cli/commons-cli/1.4)

## Running: ##
In order to start the white player you must run the command:
./runmyplayer white

In order to start the black player you must run the command:
./runmyplayer black

You can run players with extra parameters:
./runmyplayer <role> <timeout> <ip-address>

* <role> : role of the player in the game (black or white)
* <timeout> : time for the player to perform a move (default: 60, not mandatory)
* <ip-address> : ip address of the server (default: localhost, not mandatory)

## Group members: ##
* Riccardo Spolaor (riccardo.spolaor@studio.unibo.it)
* Facundo Nicolas Maidana (facundo.maidana@studio.unibo.it)