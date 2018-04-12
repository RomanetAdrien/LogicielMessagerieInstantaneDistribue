package application;

import texte.ApplicationTexte;
import voix.ApplicationVoix;

import javax.sound.sampled.LineUnavailableException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by adrie on 26/03/2018.
 */
public class App {
    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws LineUnavailableException {
        String userName = "Aerlai";
        ApplicationVoix app = new ApplicationVoix(1500, userName);
        int portNumber = 1500;
        String serverAddress = "192.168.0.125";
        //String serverAddress = "localhost";

        app.nouveauChat(serverAddress,portNumber);
    }
}
