package application;

import annuaire.ApplicationAnnuaire;
import texte.ApplicationTexte;

public class Application2 {
    public static ApplicationAnnuaire annuaire;
    public static ApplicationTexte appTexte;
    public static int portTexte = 2000;
    public static int portAnnuaire = 1500;
    public static int portVoix = 1500;
    public static String userName = "Elos";


    public static void main(String[] args) {


        annuaire = new ApplicationAnnuaire(userName,portAnnuaire);
        appTexte = new ApplicationTexte(portTexte, userName);
    }
}
