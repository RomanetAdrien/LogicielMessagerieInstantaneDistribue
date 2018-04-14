package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class ConversationTexte extends Thread{
    /**
     * Variables
     */
    // Socket
    private SocketChat s;
    // Mon pseudo
    private String monPseudo;
    // L'interface graphique
    private ClientGUI gui;


    /**
     * Constructeurs
     */
    // Contructeur pour les connexions entrantes
    public ConversationTexte(Socket so, int portV){
        s = new SocketChat(so, portV);
        s.start();
    }
    // Constructeur pour les connexions sortantes
    public ConversationTexte(String ip, int portT, int portV){
        s = new SocketChat(ip,portT, portV);
        s.start();
    }

    public void run(){
        gui = new ClientGUI(s);
    }

    public void close(){
        s.close();
    }

    public long getId() {
        return s.getId2();
    }
}

// Interface graphique
class ClientGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    // will first hold "Username:", later on "Enter message"
    private JLabel labelVocal, labelTexte;
    // to hold the Username and later on the messages
    private JTextField tf;
    // to Logout and get the list of the users
    private JButton logout;
    // for the chat room
    private JTextArea ta;
    // if it is for connection
    private boolean connected;
    // Socket
    private SocketChat s;

    // Boutons pour les appels
    private JButton startCallButton;
    private JButton stopCallButton;
    private JButton muteCallButton;

    // Gestion du mute micro
    private boolean mute = false;


    // Constructor connection receiving a socket number
    ClientGUI(SocketChat s) {

        super("Chat Client");

        // On initialise la socket
        this.s = s;

        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3, 1));

        // Partie vocale
        labelVocal = new JLabel("Conversation Vocale", SwingConstants.CENTER);
        northPanel.add(labelVocal);
        JPanel vocal = new JPanel(new GridLayout(1,3));
        startCallButton = new JButton("Appeller");
        startCallButton.addActionListener(this);
        stopCallButton = new JButton("Racrocher");
        stopCallButton.addActionListener(this);
        muteCallButton = new JButton("Muter");
        muteCallButton.addActionListener(this);
        vocal.add(startCallButton);
        vocal.add(stopCallButton);
        vocal.add(muteCallButton);
        northPanel.add(vocal);

        // Partie Texte
        JPanel texte = new JPanel(new GridLayout(2,1));
        labelTexte = new JLabel("Ecrivez le message ci dessous", SwingConstants.CENTER);
        texte.add(labelTexte);
        tf = new JTextField("");
        tf.setBackground(Color.WHITE);
        texte.add(tf);
        northPanel.add(texte);

        add(northPanel, BorderLayout.NORTH);

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);

        // Le bouton pour quitter
        logout = new JButton("Fermer");
        logout.addActionListener(this);
        logout.setEnabled(false);        // you have to login before being able to logout

        JPanel southPanel = new JPanel();
        southPanel.add(logout);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);
        tf.requestFocus();

        // On dit que l'on est connecté
        connected = true;

        // Etat initial des boutons
        startCallButton.setEnabled(true);
        stopCallButton.setEnabled(false);
        muteCallButton.setEnabled(false);
        // On active le bouton de deconnexion
        logout.setEnabled(true);
        // On regarde les actions sur le champ message
        tf.addActionListener(this);
        // on initialise la connxexion socket -> gui
        s.setGui(this);

    }

    // called by the Client to append text in the TextArea
    public void append(String str) {
        ta.append(str);
        ta.append("\n");
        ta.setCaretPosition(ta.getText().length() - 1);
    }

    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        logout.setEnabled(false);
        tf.setText("");
        // don't react to a <CR> after the username
        tf.removeActionListener(this);
        connected = false;
    }

    /*
     * Button or JTextField clicked
     */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        // if it is the Logout button
        if (o == logout) {
            try {
                s.writeMsg(new Message(Message.LOGOUT, ""));
                s.close();
                append("Vous vous êtes déconecté");
            }catch (Exception err){

            }
            dispose();
            return;
        }
        if (o == startCallButton){
            try {
                s.writeMsg(new Message(Message.CALLDEMAND, ""));
                append("Demande d'appel vocal");
            }catch (Exception err){

            }
            return;
        }
        if (o == stopCallButton){
            s.writeMsg(new Message(Message.CALLCLOSE, ""));
            ApplicationTexte.callingRecorder = false;
            ApplicationTexte.callingPlayer = false;
            startCallButton.setEnabled(true);
            stopCallButton.setEnabled(false);
            muteCallButton.setEnabled(false);
        }
        if (o == muteCallButton){
            if(!mute) {
                ApplicationTexte.callingRecorder = false;
                mute = true;
            }
            else if(mute){
                ApplicationTexte.callingRecorder = true;
                s.init_audio();
            }
        }

        // ok it is coming from the JTextField
        if (connected && o!= stopCallButton && o!=muteCallButton) {
            // just have to send the message
            s.writeMsg(new Message(Message.MESSAGE, tf.getText()));
            append("Moi : " + tf.getText());
            tf.setText("");
            return;
        }
    }

    public void setStartCallButton(boolean b){
        startCallButton.setEnabled(b);
    }
    public void setStopCallButton(boolean b){
        stopCallButton.setEnabled(b);
    }
    public void setMuteCallButton(boolean b){
        muteCallButton.setEnabled(b);
    }
}
