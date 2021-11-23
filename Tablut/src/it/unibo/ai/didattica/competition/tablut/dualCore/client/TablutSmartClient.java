package it.unibo.ai.didattica.competition.tablut.dualCore.client;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.dualCore.game.Player;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutSmartClient extends TablutClient {

    private final static String ERROR_MESSAGE = "usage: ./runmyplayer {black,white} [timeout] [ip_address]\n";
    private static int timeout = 60;

    public TablutSmartClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String player = "", name = "Dual Core", ipAddress = "127.0.0.1";
        if (args.length == 0) {
            System.out.println("the following arguments are required: role (WHITE or BLACK)!\n");
            System.out.println(TablutSmartClient.ERROR_MESSAGE);
            System.exit(-1);
        }

        else {
            player = args[0];
        }

        if (args.length > 1) {
            if (args.length >= 4){
                System.out.println("too many arguments.\n");
                System.out.println(TablutSmartClient.ERROR_MESSAGE);
                System.exit(-1);
            }
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                System.out.println("timeout must be a positive integer value!\n");
                System.out.println(TablutSmartClient.ERROR_MESSAGE);
                System.exit(-1);
            }

            if (args.length == 3) {
                ipAddress = args[2];
            }
        }

        System.out.println("Selected role: " + args[0] + "\n");

        TablutSmartClient client = new TablutSmartClient(player, name, timeout, ipAddress);

        client.run();
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Player player = new Player(getPlayer(), timeout);
        State state;

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }

            state = this.getCurrentState();
            player.saveState(state);

            State.Turn turn = state.getTurn();

            if (turn.equals(this.getPlayer())) {
                Action action = player.chooseMove(state);
                System.out.println("Selected move: " + action + "\n");
                try {
                    this.write(action);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }

            else if (turn.equals(State.Turn.BLACK) || turn.equals(State.Turn.WHITE)) {
                System.out.println("Adversary turn starting...\n");
            }

            else if (turn.equals(StateTablut.Turn.WHITEWIN)) {
                if (this.getPlayer().equals(State.Turn.WHITE)) {
                    System.out.println("The match result is: victory!\n");
                } else {
                    System.out.println("The match result is: loss!\n");
                }
                System.exit(0);
            }

            else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                if (this.getPlayer().equals(State.Turn.BLACK)) {
                    System.out.println("The match result is: victory!\n");
                } else {
                    System.out.println("The match result is: loss!\n");
                }
                System.exit(0);
            }

            else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                System.out.println("The match result is: draw!\n");
                System.exit(0);
            }
        }
    }
}
