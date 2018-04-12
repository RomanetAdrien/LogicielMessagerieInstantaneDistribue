package annuaire;

import java.net.Socket;

/**
 * Created by adrien on 26/03/2018.
 * modifié par antoine le 31/03/2018.
 */
public class Utilisateur {
    /**
     * Variables
     */
    private String pseudo;
    private String ip;
    private int port;
    private int id;
    private SocketAnnuaire socketAnnuaire;

    /**
     * Contructeurs
     */
    // Créer un utilisateur depuis le serveurAnnuaire
    public Utilisateur(Socket socket){
        this.pseudo = "null";
        this.ip = socket.getInetAddress().toString();
        this.port = socket.getPort();
        id = ++Annuaire.uniqueID;
        socketAnnuaire = new SocketAnnuaire(socket, this);
    }
    // Créer un utlisateur lors de la première connexion
    public Utilisateur(String ip, int port){
        this.pseudo = "null";
        this.ip = ip;
        this.port = port;
        id = ++Annuaire.uniqueID;
        socketAnnuaire = new SocketAnnuaire(ip, port, this);
    }

    /**
     * Getters
     */

    public String getIp() {
        return ip;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    /**
     * Setters
     */
    public void setIp(String ip) {
        this.ip= ip;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Methodes
     */
    // Cette methode renvoie une version de l'utilisateur pouvant etre exporté.
    public UtilisateurSimple creerUtilisateurSimple(){
        UtilisateurSimple u = new UtilisateurSimple(this.ip, this.pseudo);
        return u;
    }

    // Fermer le socket
    public void fermer(){
        socketAnnuaire.close();
    }
}
