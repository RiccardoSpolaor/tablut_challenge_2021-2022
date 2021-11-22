package it.unibo.ai.didattica.competition.tablut.dualCore;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;

public class dualCoreTablut {
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array = new String[]{"WHITE"};
		TablutClient.main(array);
    }
}
