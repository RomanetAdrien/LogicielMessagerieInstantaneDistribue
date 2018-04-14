package application;

import annuaire.ApplicationAnnuaire;
import chat.ApplicationTexte;

import javax.swing.*;

public class Application {
    public static ApplicationAnnuaire annuaire;
    public static ApplicationTexte appTexte;
    public static int portTexte = 2000;
    public static int portAnnuaire = 1500;
    public static int portVoix = 8888;
    public static String userName = "Aerlai";


    public static void main(String[] args) {
        userName = JOptionPane.showInputDialog("Entrer votre pseudo");
        annuaire = new ApplicationAnnuaire(userName,portAnnuaire);
        appTexte = new ApplicationTexte(portTexte, portVoix, userName);
    }
}
