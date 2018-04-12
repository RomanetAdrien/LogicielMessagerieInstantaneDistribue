package voix;

import texte.ServerTexte;
import texte.SocketTexte;

import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Malomek on 07/04/2018.
 */
public class ApplicationVoix {

    /**
     * Variable
     */
    // L'executor pour la gestion des threads
    private static ExecutorService executor;
    // Une liste de client
    protected static ArrayList<SocketVoix> socketVoixList;
    // Le server de reception des nouvelles connexions
    ServerVoix serverVoix;
    // Mon nom
    private String usernameMoi;

    /**
     * Constructeur
     */
    public ApplicationVoix(int port, String usernameMoi) throws LineUnavailableException {
        // On initialise l'executor
        executor = Executors.newFixedThreadPool(2);
        // On initialise le tableau de socket
        socketVoixList = new ArrayList<SocketVoix>();
        // On initialise le serveur de connexion entrante
        try {
            serverVoix = new ServerVoix(port);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        // On initialise le pseudo
        this.usernameMoi = usernameMoi;
        // on lance le serveur dans un thread via l'executor
        executor.execute(serverVoix);
    }

    /**
     * Methodes
     */

    public void nouveauChat(String serverAddress, int portNumber){
        SocketVoix s = new SocketVoix(serverAddress,portNumber,usernameMoi);
        socketVoixList.add(s);
        s.start();
    }
}
