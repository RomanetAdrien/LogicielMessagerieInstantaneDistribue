package annuaire;

public class ApplicationAnnuaire {
    /**
     * Variable
     */
    protected static Annuaire annuaire;
    private ServeurAnnuaire serveur;

    /**
     * Constructeur
     */
    public ApplicationAnnuaire(int port){
        annuaire = new Annuaire();
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
