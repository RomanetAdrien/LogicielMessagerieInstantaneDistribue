package annuaire;

public class ApplicationAnnuaire {
    /**
     * Variable
     */
    protected static Annuaire annuaire;
    public static String monPseudo;
    private ServeurAnnuaire serveur;
    public static AnnuaireGUI gui;

    /**
     * Constructeur
     */
    public ApplicationAnnuaire(String pseudo,int port){
        annuaire = new Annuaire();
        monPseudo = pseudo;
        gui = new AnnuaireGUI(this);
        serveur = new ServeurAnnuaire(port);
        // On demmare le serveur
        serveur.start();
    }

    /**
     * Methodes
     */
    // Connexion a un noeud pour se synchroniser.
    public void connexion(String ip, int port){
        annuaire.ajouterNouveauUtilisateurDirect(ip, port);
    }

    // Recherche de l'ancre TODO
    public void synchroniserAuReseau(){

    }
}
