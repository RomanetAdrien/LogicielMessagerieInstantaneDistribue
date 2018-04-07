package texte;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Créer par Antoine le 01/04/2018
 * Le server qui attends la connexion d'un client pour discuter
 */
public class ServerTexte implements Runnable {
    /**
     * Variables
     */
    // un identifiant unique
    protected static int uniqueID;
    // La date et l'heure
    private SimpleDateFormat sdf;
    // Le port d'écoute
    private int port;
    // Le boolean qui permet d'eteindre le serveur
    private boolean keepGoing;

    /**
     * Constructeur
     */
    public ServerTexte(int port){
        // On initialise le port
        this.port = port;
        // On initialise le format de la date
        sdf = new SimpleDateFormat("HH:mm:ss");
    }


    /**
     * Methodes
     */
    // Boucle du serveur
    @Override
    public void run() {
        keepGoing = true;
        // créer un socket et attendre
        try
        {
            // Le socket du serveur
            ServerSocket serverSocket = new ServerSocket(port);

            // Boucle infini pour attendre une connexion
            while(keepGoing)
            {
                // Attends une connexion
                System.out.println("Server Texte waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ConversationTexte t = new ConversationTexte(socket);  // make a thread of it
                ApplicationTexte.ConversationTexteList.add(t);									// save it in the ArrayList
                t.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for(int i = 0; i < ApplicationTexte.ConversationTexteList.size(); ++i) {
                    ConversationTexte tc = ApplicationTexte.ConversationTexteList.get(i);
                    tc.close();
                }
            }
            catch(Exception e) {
                System.out.println("Erreur lors de fermetture du serveur: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < ApplicationTexte.ConversationTexteList.size(); ++i) {
            ConversationTexte ct = ApplicationTexte.ConversationTexteList.get(i);
            // found it
            if(ct.getId() == id) {
                ApplicationTexte.ConversationTexteList.remove(i);
                return;
            }
        }
    }


}
