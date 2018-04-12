package annuaire;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Cette classe définit les échanges attendu entre les clients Annuaire
 */
public class MessageAnnuaire implements Serializable{
    protected static final long serialVersionUID = 1112122200L;

    // Les types de messages
    // WHOISIN demande la liste des utilisateurs
    // ALLUSERS message de réponse a la demande de la liste des utilisateurs
    // LOGOUT message de déconnexion
    public static final int WHOISIN = 1, ALLUSERS = 2, LOGOUT = 3;
    private int type;
    private ArrayList<UtilisateurSimple> listeUtilisateurs;

    /** constructor */
    public MessageAnnuaire(int type) {
        this.type = type;
    }

    /** getters */
    public int getType() {
        return type;
    }

    public ArrayList<UtilisateurSimple> getListeUtilisateurs() {
        return listeUtilisateurs;
    }

    /**
     * Setters
     */
    public void setListeUtilisateurs(ArrayList<UtilisateurSimple> listeUtilisateurs) {
        this.listeUtilisateurs = listeUtilisateurs;
    }
}
