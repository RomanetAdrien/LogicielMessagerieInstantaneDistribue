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
    protected static ArrayList<ConversationTexte> ConversationTexteList;
    // Le server de reception des nouvelles connexions
    ServerTexte serverTexte;
    // Mon nom
    public static String monPseudo;

    /**
     * Constructeur
     */
    public ApplicationTexte(int port, String monPseudo){
        // On initialise l'executor
        executor = Executors.newFixedThreadPool(2);
        // On initialise le tableau de socket
        ConversationTexteList = new ArrayList<ConversationTexte>();
        // On initialise le serveur de connexion entrante
        serverTexte = new ServerTexte(port);
        // On initialise le pseudo
        this.monPseudo = monPseudo;
        // on lance le serveur dans un thread via l'executor
        executor.execute(serverTexte);
    }

    /**
     * Methodes
     */

    public void nouveauChat(String serverAddress, int portNumber){
        ConversationTexte c = new ConversationTexte(serverAddress,portNumber);
        ConversationTexteList.add(c);
        c.start();
    }
}
