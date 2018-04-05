package texte;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;


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
    String monPseudo = ApplicationTexte.monPseudo;
    // Adresse et port distant
    String ip;
    int port;

    // lien vers l'interface
    private ClientGUI gui;

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
        display("Le thread essaye de creer les objets de gestion de flux");
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            display(usernameClient + " s'est connecte.");
            // On envoit son pseudo
            sOutput.writeObject(monPseudo);
        }
        catch (IOException e) {
            display("Exception creating new Input/output Streams: " + e);
            return;
        }
        catch (ClassNotFoundException e) {
        }

        date = new Date().toString() + "\n";
    }

    // Socket de connexion sortante
    public SocketTexte(String server, int port){
        this.ip = server;
        this.port = port;
        boolean s = connexionSortante();
    }


    /**
     * Getter
     */
    public int getId2() {
        return this.id;
    }
    /**
     * Setter
     */
    public void setGui(ClientGUI gui) {
        this.gui = gui;
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
                    cm = (Message) sInput.readObject();
                } catch (IOException e) {
                    display(usernameClient + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // On récupère le string du message
                String message = cm.getMessage();

                // On regarde le type de message
                switch (cm.getType()) {

                    case Message.MESSAGE:
                        display(usernameClient + " : " + message);
                        break;
                    case Message.LOGOUT:
                        display(usernameClient + " disconnected with a LOGOUT message.");
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
            display("Error sending message to " + usernameClient);
            display(e.toString());
        }
        return true;
    }

    // Envoyer un message
    protected boolean writeMsg(Message msg) {
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            display("?");
            close();
            return false;
        }
        // Ecrire le message sur le flux
        try {
            sOutput.writeObject(msg);
            //display("envoyé a " + socket.getInetAddress());
        }
        // Si il y a une erreur on informe l'utilisateur
        catch(IOException e) {
            display("Error sending message to " + usernameClient);
            display(e.toString());
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
            display("Error connectiong to server:" + ec);
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
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // On envoi son pseudo
        try
        {
            sOutput.writeObject(monPseudo);
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            display(usernameClient + " s'est connecte.");
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            close();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // tout a bien fonctionné
        return true;
    }

    public void display(String str){
        if(gui != null) gui.append(str);
        System.out.println(str);
    }
}
