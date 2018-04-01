package annuaire;

/**
 * Created by adrien on 26/03/2018.
 * modifié par antoine le 31/03/2018.
 */
public class Utilisateur {
    // Variables
    private String pseudo;
    private String ipv4;
    private String ipv6;
    private int statut;
    private int id;

    /**
     * Contructeurs
     */
    // Créer un utilisateur déja renseigné
    public Utilisateur(String pseudo, String ipv4, String ipv6, int statut, int id){
        this.ipv4 = ipv4;
        this.ipv6 = ipv6;
        this.pseudo = pseudo;
        this.statut = statut;
        this.id = id;
    }

    // Créer un utilisateur vide
    public Utilisateur(){

    }

    // Getters
    public int getStatut() {
        return statut;
    }

    public String getIpv4() {
        return ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getId() {
        return id;
    }

    // Setters
    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Methodes

    // Surcharge de toString
    @Override
    public String toString() {
        return "Pseudo : " +pseudo+ ", statut : " +statut+ ", IPv4 : " +ipv4+ ", IPv6 : " +ipv6+ ", id : " +id;
    }
}
