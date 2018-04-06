package annuaire;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by adrien on 26/03/2018.
 *
 *
 *
 */
public class Annuaire {
    /** Variables */
    public static ArrayList<Utilisateur> tab = new ArrayList();
    protected static int uniqueID;

    /** Contructeur */
    public Annuaire(){

    }

    /** Getters */

    /** Setter */

    /** Methode */
    // Ajouter un nouveau utilisateur depuis une connexion entrante
    public void ajouterNouveauUtilisateur(Socket socket){
        Utilisateur u = new Utilisateur(socket);
        tab.add(u);
    }

    // Ajouter un utilisateur pour la première connexion
    public void ajouterNouveauUtilisateurDirect(String ip, int port){
        Utilisateur u = new Utilisateur(ip,port);
        tab.add(u);
    }

    // Generer un arraylist contenant tout les utilisateurs
    public ArrayList<UtilisateurSimple> obtenirTousLesUtilisateurs(){
        ArrayList<UtilisateurSimple> l = new ArrayList<UtilisateurSimple>();
        for(int i=0 ; i<tab.size() ; i++){
            l.add(tab.get(i).creerUtilisateurSimple());
        }
        return l;
    }

    // On supprime un utilisateur
    public void supprimerUtilisateur(Utilisateur user){
        user.fermer();
        tab.remove(user);
    }

    // On ferme toutes les connexions
    public void fermerLesConnexions(){
        for(int i=tab.size()-1 ; i>=0 ; i--){
            tab.get(i).fermer();
            tab.remove(i);
        }
    }





}
