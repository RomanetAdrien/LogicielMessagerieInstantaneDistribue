package chat;

import application.Application;
import voix.client.ClientVoix;
import chat.voix.player_thread;
import chat.voix.recorder_thread;
import voix.server.ServerVoix;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Date;


/**
 * Créer par Antoine le 01/04/2018
 * Socket de communication texte entre 2 clients
 *
 */

public class SocketChat extends Thread{
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
    int port_text;
    int port_vocal;

    TargetDataLine audio_in;
    public SourceDataLine audio_out;

    // lien vers l'interface
    private ClientGUI gui;

    /**
     * Constructeur
     */
    // Socket de connexion entrante
    public SocketChat(Socket socket, int portV){
        this.port_vocal = portV;
        // On créer l'identifiant unique
        id = ++ServerChat.uniqueID;
        // on recupère le socket
        this.socket = socket;
        // Creation du flux
        display("Le thread essaye de creer les objets de gestion de flux", 2);
        try
        {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            display(usernameClient + " s'est connecte.", 1);
            // On envoit son pseudo
            sOutput.writeObject(monPseudo);
        }
        catch (IOException e) {
            display("Exception creating new Input/output Streams: " + e, 2);
            return;
        }
        catch (ClassNotFoundException e) {
        }

        date = new Date().toString() + "\n";
    }

    // Socket de connexion sortante
    public SocketChat(String server, int portT, int portV){
        this.ip = server;
        this.port_text = portT;
        this.port_vocal = portV;
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
                    if (ApplicationTexte.calling){
                        close_audio();
                        gui.append("Fin de l'appel");
                    }
                    display(usernameClient + " Exception reading Streams: " + e, 2);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // On récupère le string du message
                String message = cm.getMessage();

                // On regarde le type de message
                switch (cm.getType()) {

                    case Message.MESSAGE:
                        display(usernameClient + " : " + message, 1);
                        break;
                    case Message.LOGOUT:
                        display(usernameClient + " Deconnecte avec un LOGOUT message.", 1);
                        if(ApplicationTexte.calling){
                            display(usernameClient + " Fin de l'appel", 1);
                            close_audio();
                        }
                        keepGoing = false;
                        break;
                    case Message.CALLDEMAND:
                        display(usernameClient + " Appel entrant", 1);
                        int a = JOptionPane.showConfirmDialog(gui, "Accepter l'appel", "Appelle entrant de "+usernameClient, JOptionPane.YES_NO_OPTION);
                        if(a==JOptionPane.YES_OPTION){
                            Message msg = new Message(Message.CALLACCEPT, "");
                            writeMsg(msg);
                            init_audio();
                        }
                        break;
                    case Message.CALLACCEPT:
                        display(usernameClient + " Appel accepté", 1);
                        init_audio();
                        break;
                    case Message.CALLCLOSE:
                        display(usernameClient + " Fin de l'appel", 1);
                        close_audio();
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
            display("Error sending message to " + usernameClient, 1);
            display(e.toString(), 1);
        }
        return true;
    }

    // Envoyer un message
    protected boolean writeMsg(Message msg) {
        // Si le client est toujours connecté
        if(!socket.isConnected()) {
            display("?", 2);
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
            display("Error sending message to " + usernameClient, 1);
            display(e.toString(), 1);
        }
        return true;
    }

    // Créer socket connexion sortante
    private boolean connexionSortante(){
        // On essaye de se connecter au client
        try {
            socket = new Socket(ip, port_text);
        }
        // Si ce la échoue
        catch(Exception ec) {
            display("Error connectiong to server:" + ec, 2);
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
            display("Exception creating new Input/output Streams: " + eIO, 2);
            return false;
        }

        // On envoi son pseudo
        try
        {
            sOutput.writeObject(monPseudo);
            // On lit le pseudo
            usernameClient = (String) sInput.readObject();
            display(usernameClient + " s'est connecte.", 2);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO, 2);
            close();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // tout a bien fonctionné
        return true;
    }

    public void display(String str, int type){
        if (type == 1){
            if(gui != null) gui.append(str);
        }
        else if (type == 2){
            System.out.println(str);
        }
    }

    public void init_audio(){
        try {
            ApplicationTexte.calling = true;
            AudioFormat format = ApplicationTexte.getAudioFormat();
            // Audio entrant
            DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
            if(!AudioSystem.isLineSupported(info_in)){
                System.out.println("no support");
                System.exit(0);
            }
            audio_in = (TargetDataLine) AudioSystem.getLine(info_in);
            audio_in.open(format);
            audio_in.start();
            recorder_thread r = new recorder_thread();
            InetAddress inet = socket.getInetAddress();
            r.audio_in = audio_in;
            r.dout = new DatagramSocket();
            r.server_ip = inet;
            r.server_port = port_vocal;
            r.start();

            // Audio sortant
            DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
            if(!AudioSystem.isLineSupported(info_out)){
                System.out.println("no support");
                System.exit(0);
            }
            audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
            audio_out.open(format);
            audio_out.start();
            player_thread p = new player_thread();
            p.din = new DatagramSocket(port_vocal);
            p.audio_out = audio_out;
            p.start();

            gui.setStartCallButton(false);
            gui.setStopCallButton(true);
            gui.setMuteCallButton(true);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void close_audio(){
        audio_in.close();
        audio_in.drain();
        audio_out.close();
        audio_out.drain();
        ApplicationTexte.calling =false;
        gui.setMuteCallButton(false);
        gui.setStopCallButton(false);
        gui.setStartCallButton(true);
    }
}
