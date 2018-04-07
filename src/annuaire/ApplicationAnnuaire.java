package annuaire;

public class ApplicationAnnuaire {
    /**
     * Variable
     */
    protected static Annuaire annuaire;
    public static String monPseudo;
    private ServeurAnnuaire serveur;
    private AnnuaireGUI gui;

    /**
     * Constructeur
     */
    public ApplicationAnnuaire(String pseudo,int port){
        annuaire = new Annuaire();
        serveur = new ServeurAnnuaire(port);
        monPseudo = pseudo;
        // On demmare le serveur
        serveur.start();
        gui = new AnnuaireGUI(this);
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
