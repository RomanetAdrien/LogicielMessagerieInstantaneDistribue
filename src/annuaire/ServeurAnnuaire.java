package annuaire;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServeurAnnuaire extends Thread{
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
    public ServeurAnnuaire(int port){
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
                System.out.println("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ApplicationAnnuaire.annuaire.ajouterNouveauUtilisateur(socket);
            }
            // I was asked to stop
            try {
                serverSocket.close();
                ApplicationAnnuaire.annuaire.fermerLesConnexions();
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
}
