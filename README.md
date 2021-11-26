# DualCore: Tablut Challenge 2021/2022

A Java-based implementation of an agent able to play the game of Tablut for the "Tablut Challenge 2021/2022" held by the University of Bologna.

## Strategy

Both white and black players have been implemented using a parallelized Negamax algorithm that uses the techniques of Alpha Beta pruning and transposition tables.

## Requirements

You need to have JDK >= 11. From Ubuntu/Debian console, you can install it with these commands:
```console
sudo apt update
sudo apt install openjdk-11-jdk -y
```

## Run
In order to start the white player you must run the command:
```console
./runmyplayer white
```
In order to start the black player you must run the command:
```console
./runmyplayer black
```
You can run players with extra parameters:
```console
./runmyplayer <role> <timeout> <ip-address>
    
    <role> : role of the player in the game (black or white)
    <timeout> : time for the player to perform a move (default: 60, not mandatory)
    <ip-address> : ip address of the server (default: localhost, not mandatory)
```


## Resources & Libraries

* Java 8 [API](https://docs.oracle.com/javase/8/docs/api/)
* Gson 2.2.2 [library](https://www.javadoc.io/doc/com.google.code.gson/gson/2.2.2/com/google/gson/Gson.html)
* commons-cli-1.4.jar [library](https://mvnrepository.com/artifact/commons-cli/commons-cli/1.4)

## Group members
 |        Name       |  Surname  |              Email               |                         Github                          |
 | :---------------: | :-------: | :------------------------------: | :-----------------------------------------------------: |
 |      Riccardo     |  Spolaor  | riccardo.spolaor@studio.unibo.it | [_RiccardoSpolaor_](https://github.com/RiccardoSpolaor) |         
 |  Facundo Nicolas  |  Maidana  | facundo.maidana@studio.unibo.it  |      [_maidacundo_](https://github.com/maidacundo)      |