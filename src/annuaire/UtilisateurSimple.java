package annuaire;

public class UtilisateurSimple {
    /**
     * Variables
     */
    private String ip;
    private String pseudo;

    /**
     * Constructeurs
     */
    public UtilisateurSimple(String ip, String pseudo){
        this.ip = ip;
        this.pseudo = pseudo;
    }

    /**
     * getters
     */
    public String getIp() {
        return ip;
    }

    public String getPseudo() {
        return pseudo;
    }
}
