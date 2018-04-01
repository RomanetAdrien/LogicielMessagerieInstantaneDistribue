package texte;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationTexte{
    /**
     * Variable
     */
    // L'executor pour la gestion des threads
    private static ExecutorService executor;
    // Une liste de client
    protected static ArrayList<SocketTexte> socketTexteList;
    // Le server de reception des nouvelles connexions
    ServerTexte serverTexte;
    // Mon nom
    private String usernameMoi;

    /**
     * Constructeur
     */
    public ApplicationTexte(int port, String usernameMoi){
        // On initialise l'executor
        executor = Executors.newFixedThreadPool(2);
        // On initialise le tableau de socket
        socketTexteList = new ArrayList<SocketTexte>();
        // On initialise le serveur de connexion entrante
        serverTexte = new ServerTexte(port);
        // On initialise le pseudo
        this.usernameMoi = usernameMoi;
        // on lance le serveur dans un thread via l'executor
        executor.execute(serverTexte);
    }

    /**
     * Methodes
     */

    public void nouveauChat(String serverAddress, int portNumber){
        SocketTexte s = new SocketTexte(serverAddress,portNumber,usernameMoi);
        socketTexteList.add(s);
        s.start();
    }
}
