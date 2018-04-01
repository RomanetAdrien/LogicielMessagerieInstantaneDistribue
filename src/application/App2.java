package application;

import texte.ApplicationTexte;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by adrie on 26/03/2018.
 */
public class App2 {
    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        String userName = "Elos";
        ApplicationTexte appTexte = new ApplicationTexte(1500, userName);
        int portNumber = 1500;
        String serverAddress = "192.168.137.184";
        //String serverAddress = "localhost";
        //appTexte.nouveauChat(serverAddress,portNumber,userName);
    }
}
