package texte;

import annuaire.Utilisateur;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Créer par Antoine le 01/04/2018
 * Socket de communication texte entre 2 clients
 *
 */

public class SocketTexte extends Thread{
    /**
     * Variables
     */
    // Le socket pour écouter et envoyer
    protected Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    // Un identifiant unique sous forme d'entier (c'est le même que celui de l'utilisateur dans l'annuaire)
    int id;
    // Le message
    Message cm;
    // La date de connexion
    String date;
    // Le pseudo
    String usernameClient;
    String usernameMoi;
    // Adresse et port distant
    String ip;
    int port;
    // Exexutor pour la gestion des threads
    private static ExecutorService executor = Executors.newFixedThreadPool(2);
    ConversationTexte conv;

    /**
     * Constructeur
     */
    // Socket de connexion entrante
    public SocketTexte(Socket socket){
        // On créer l'identifiant unique
        id = ++ServerTexte.uniqueID;
        // on recupère le socket
        this.socket = socket;
        // Creation du flux
        System.out.println("Le thread essaye de creer les objets de gestion de flux");
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            System.out.println(usernameClient + " s'est connecte.");
            // On envoit son pseudo
            sOutput.writeObject(usernameMoi);
        }
        catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return;
        }
        catch (ClassNotFoundException e) {
        }

        date = new Date().toString() + "\n";
        conv = new ConversationTexte(this);
        executor.execute(conv);
    }

    // Socket de connexion sortante
    public SocketTexte(String server, int port, String username){
        this.ip = server;
        this.port = port;
        this.usernameMoi = username;
        boolean s = connexionSortante();

        conv = new ConversationTexte(this);
        executor.execute(conv);
    }


    /**
     * Getter
     */

    /**
     * Setter
     */

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
                    cm = (Message) sInput.readObject();
                } catch (IOException e) {
                    System.out.println(usernameClient + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // On récupère le string du message
                String message = cm.getMessage();

                // On regarde le type de message
                switch (cm.getType()) {

                    case Message.MESSAGE:
                        System.out.println(usernameClient + " : " + message);
                        break;
                    case Message.LOGOUT:
                        System.out.println(usernameClient + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;

                }
            }
            // On ferme le socket
            close();
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
    }

    // Envoyer un message
    protected boolean writeMsg(String msg) {
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            close();
            return false;
        }
        // Ecrire le message sur le flux
        try {
            sOutput.writeObject(msg);
        }
        // Si il y a une erreur on informe l'utilisateur
        catch(IOException e) {
            System.out.println("Error sending message to " + usernameClient);
            System.out.println(e.toString());
        }
        return true;
    }

    // Envoyer un message
    protected boolean writeMsg(Message msg) {
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            System.out.println("?");
            close();
            return false;
        }
        // Ecrire le message sur le flux
        try {
            sOutput.writeObject(msg);
            //System.out.println("envoyé a " + socket.getInetAddress());
        }
        // Si il y a une erreur on informe l'utilisateur
        catch(IOException e) {
            System.out.println("Error sending message to " + usernameClient);
            System.out.println(e.toString());
        }
        return true;
    }

    // Créer socket connexion sortante
    private boolean connexionSortante(){
        // On essaye de se connecter au client
        try {
            socket = new Socket(ip, port);
        }
        // Si ce la échoue
        catch(Exception ec) {
            System.out.println("Error connectiong to server:" + ec);
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);

        // On créer les flux
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            System.out.println("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // On envoi son pseudo
        try
        {
            sOutput.writeObject(usernameMoi);
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            System.out.println(usernameClient + " s'est connecte.");
        }
        catch (IOException eIO) {
            System.out.println("Exception doing login : " + eIO);
            close();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // tout a bien fonctionné
        return true;
    }
}
