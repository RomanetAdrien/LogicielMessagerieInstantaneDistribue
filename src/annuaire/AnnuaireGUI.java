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
    // to hold the server address an the port number
    private JTextField tfServer, tfPort;
    // to Logout and get the list of the users
    private JButton login, logout, actualiser, txt, call;
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

        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3,1));
        // the server name anmd the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
        // the two JTextField with default value for server address and port number
        tfServer = new JTextField("localhost");
        tfPort = new JTextField("1500");
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        serverAndPort.add(new JLabel(""));
        // adds the Server an port field to the GUI
        northPanel.add(serverAndPort);
        northPanel.add(jComboBox);
        JPanel boutonsCom = new JPanel(new GridLayout(1,2, 1, 3));
        txt = new JButton("Conversation texte");
        txt.addActionListener(this);
        call = new JButton("Conversation vocale");
        call.addActionListener(this);
        boutonsCom.add(txt);
        boutonsCom.add(call);
        northPanel.add(boutonsCom);
        add(northPanel, BorderLayout.NORTH);

        // the 2 buttons
        login = new JButton("Login");
        login.addActionListener(this);
        actualiser = new JButton("Actualiser");
        actualiser.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);		// you have to login before being able to logout

        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(actualiser);
        southPanel.add(logout);
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
        }
        if( o == actualiser){
            actualiserListeUtilisateur();
        }
        if ( o == logout){
            ApplicationAnnuaire.annuaire.fermerLesConnexions();
        }
        if ( o == txt){
            UtilisateurSimple u = listeUtilisateursConnectes.get(jComboBox.getSelectedIndex());
            System.out.println(u.getPseudo());
            System.out.println(Application.portTexte);
            String ip = u.getIp().substring(1,u.getIp().length());
            System.out.println(ip);
            Application.appTexte.nouveauChat(ip,Application.portTexte);
        }
        if (o == call){
            // TODO : ajouter appelle voix
        }
    }
}
