package annuaire;

import application.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AnnuaireGUI extends JFrame implements ActionListener {
    /**
     * Variables
     */
    // pour interragir avec
    private ApplicationAnnuaire parent;

    /**
     * Variable swing
     */
    // Les conteneur pour l'adresse et le port d'un noeud du réseau
    private JTextField tfServer, tfPort;
    // Les boutons
    private JButton login, actualiser, txt;
    // Liste des Utilisateurs connectés
    private ArrayList<UtilisateurSimple> listeUtilisateursConnectes;
    private JComboBox<UtilisateurSimple> jComboBox;

    /**
     * Constructor
     */
    public AnnuaireGUI(ApplicationAnnuaire app){
        // On initialise le parent
        parent = app;
        // On initialise la liste des utilisateurs connectés
        jComboBox = new JComboBox<UtilisateurSimple>();
        actualiserListeUtilisateur();

        // Le panneau du nord
        JPanel northPanel = new JPanel(new GridLayout(3,1));
        // Une sous section du panneau nord
        JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
        // On ajoute a cette sous section les champs ip et port
        tfServer = new JTextField("localhost");
        tfPort = new JTextField("1500");
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        serverAndPort.add(new JLabel(""));

        northPanel.add(serverAndPort);
        northPanel.add(jComboBox);


        JPanel boutonsCom = new JPanel(new GridLayout(1,1, 1, 3));
        txt = new JButton("Nouvelle conversation");
        txt.addActionListener(this);
        boutonsCom.add(txt);
        northPanel.add(boutonsCom);
        add(northPanel, BorderLayout.NORTH);

        // the 2 buttons
        login = new JButton("Login");
        login.addActionListener(this);
        actualiser = new JButton("Actualiser");
        actualiser.addActionListener(this);
        actualiser.setEnabled(false);

        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(actualiser);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);
    }

    /**
     * Methodes
     */
    public void actualiserListeUtilisateur(){
        listeUtilisateursConnectes = ApplicationAnnuaire.annuaire.obtenirTousLesUtilisateurs();
        jComboBox.removeAllItems();
        if(listeUtilisateursConnectes.size()>0){
            for(int i = 0; i<listeUtilisateursConnectes.size(); i++){
                jComboBox.addItem(listeUtilisateursConnectes.get(i));
            }
            jComboBox.setSelectedIndex(0);
            jComboBox.addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if( o == login){
            parent.connexion(tfServer.getText(), Integer.parseInt(tfPort.getText()));
            actualiserListeUtilisateur();
            login.setEnabled(false);
            actualiser.setEnabled(true);
        }
        if( o == actualiser){
            actualiserListeUtilisateur();
        }
        if ( o == txt){
            UtilisateurSimple u = listeUtilisateursConnectes.get(jComboBox.getSelectedIndex());
            System.out.println(u.getPseudo());
            System.out.println(Application.portTexte);
            String ip = u.getIp().substring(1,u.getIp().length());
            System.out.println(ip);
            Application.appTexte.nouveauChat(ip,Application.portTexte, Application.portVoix);
        }
    }
}
