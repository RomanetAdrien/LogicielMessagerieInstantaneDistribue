package annuaire;

import texte.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class SocketAnnuaire extends Thread{
    /**
     * Variable
     */
    // Le socket pour écouter et envoyer
    protected Socket socket;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    // Un identifiant unique sous forme d'entier (c'est le même que celui de l'utilisateur dans l'annuaire)
    int id;
    // La date de connexion
    private String date;
    // Les pseudos
    private String pseudoDistant;
    private String monPseudo = ApplicationAnnuaire.monPseudo;
    // L'utilisateur parent
    private Utilisateur utilisateur;
    // Adresse et port distant pour l'ouverture d'une connexion
    String ipDistante;
    int portDistant;
    // Le message
    MessageAnnuaire cm;

    /**
     * Constructeur
     */
    // Constructeur pour les connexions entrantes
    public SocketAnnuaire(Socket socket, Utilisateur user){
        // On récupere le parent
        this.utilisateur = user;
        // On créer l'identifiant unique
        id = user.getId();
        // on recupère le socket
        this.socket = socket;
        // Creation du flux
        System.out.println("Le thread essaye de creer les objets de gestion de flux");
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
            // On lit le pseudo
            pseudoDistant = (String) sInput.readObject();
            System.out.println(pseudoDistant+ " s'est connecte.");
            utilisateur.setPseudo(pseudoDistant);
            // On envoit son pseudo
            sOutput.writeObject(monPseudo);
        }
        catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return;
        }
        catch (ClassNotFoundException e) {
        }

        date = new Date().toString() + "\n";
    }

    // Constructeur pour les connexions sortantes
    public SocketAnnuaire(String ip, int port, Utilisateur user){
        // On initialise les informations de connexion
        this.ipDistante = ip;
        this.portDistant = port;
        // On recupere le parent
        this.utilisateur = user;
        // On lance la procédure de connexion
        connexionSortante();
    }

    /**
     * Methodes
     */
    // Boucle principale du thread
    public void run(){
        // Variable d'arret de la boucle
        boolean keepGoing = true;
        while (keepGoing) {
            // On lit le message comme un objet que l'on annalysera ensuite
            try {
                cm = (MessageAnnuaire) sInput.readObject();
            } catch (IOException e) {
                System.out.println(pseudoDistant + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                break;
            }

            // On regarde le type de message
            switch (cm.getType()) {

                case MessageAnnuaire.WHOISIN:
                    // TODO ajouter comportement WHOISIN
                    break;
                case MessageAnnuaire.ALLUSERS:
                    // TODO ajouter comportement ALLUSERS
                    break;
                case MessageAnnuaire.LOGOUT:
                    System.out.println(pseudoDistant + " disconnected with a LOGOUT message.");
                    keepGoing = false;
                    break;
            }
        }
        // On ferme le socket
        close();
    }

    // Créer socket connexion sortante
    private boolean connexionSortante(){
        // On essaye de se connecter au client
        try {
            socket = new Socket(ipDistante, portDistant);
        }
        // Si ce la échoue
        catch(Exception ec) {
            System.out.println("Erreur lors de la connexion au noeud:" + ec);
            return false;
        }
        String msg = "Connexion acceptee " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);

        //On enregistre les information de connexion dans le parent
        utilisateur.setIp(socket.getInetAddress().toString());
        utilisateur.setPort(socket.getPort());

        // On créer les flux
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            System.out.println("Erreur lors de la creation des flux: " + eIO);
            return false;
        }

        // On envoi son pseudo
        try
        {
            sOutput.writeObject(monPseudo);
            // On lit le pseudo
            pseudoDistant = (String) sInput.readObject();
            System.out.println(pseudoDistant+ " s'est connecte.");
            utilisateur.setPseudo(pseudoDistant);
            // On demande la liste des gens
            demandeWHOISIN();
        }
        catch (IOException eIO) {
            System.out.println("Erreur durant la connexion: " + eIO);
            close();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // tout a bien fonctionné
        return true;
    }

    // On ferme le socket
    public void close() {
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {};
        try {
            if(socket != null) socket.close();
        }
        catch (Exception e) {}
        ApplicationAnnuaire.annuaire.supprimerUtilisateur(utilisateur);
    }

    // Methode pour envoyer tous les utilisateurs de l'annuaire quand on recoit une demande WHOISIN
    public boolean enoyerTousLesUtilisateurs(){
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            System.out.println("Le socket est déconnecte");
            close();
            return false;
        }
        // On récupere la liste des utilisateurs et on l'ecrit sur le flux
        try {
            MessageAnnuaire msg = new MessageAnnuaire(2);
            msg.setListeUtilisateurs(ApplicationAnnuaire.annuaire.obtenirTousLesUtilisateurs());
            sOutput.writeObject(msg);
        }
        // Si il y a une erreur on informe l'utilisateur
        catch(IOException e) {
            System.out.println("Erreur lors de l'envoi du message à " + pseudoDistant);
            System.out.println(e.toString());
        }
        return true;
    }

    // Emetre une demande WHOISIN
    public boolean demandeWHOISIN(){
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            System.out.println("Le socket est déconnecte");
            close();
            return false;
        }
        try {
            MessageAnnuaire msg = new MessageAnnuaire(1);
            sOutput.writeObject(msg);
        }
        // Si il y a une erreur on informe l'utilisateur
        catch(IOException e) {
            System.out.println("Erreur lors de l'envoi du message à " + pseudoDistant);
            System.out.println(e.toString());
        }
        return true;
    }
}
