package texte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.Scanner;

public class ConversationTexte extends Thread{
    /**
     * Variables
     */
    // Socket
    private SocketTexte s;
    // Mon pseudo
    private String monPseudo;
    // L'interface graphique
    private ClientGUI gui;


    /**
     * Constructeurs
     */
    // Contructeur pour les connexions entrantes
    public ConversationTexte(Socket so){
        s = new SocketTexte(so);
        s.start();
    }
    // Constructeur pour les connexions sortantes
    public ConversationTexte(String ip, int port){
        s = new SocketTexte(ip,port);
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
    private JLabel label;
    // to hold the Username and later on the messages
    private JTextField tf;
    // to Logout and get the list of the users
    private JButton logout;
    // for the chat room
    private JTextArea ta;
    // if it is for connection
    private boolean connected;
    // Socket
    private SocketTexte s;

    // Constructor connection receiving a socket number
    ClientGUI(SocketTexte s) {

        super("Chat Client");

        // On initialise la socket
        this.s = s;

        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3, 1));

        // the Label and the TextField
        label = new JLabel("Ecrivez le message ci dessous", SwingConstants.CENTER);
        northPanel.add(label);
        tf = new JTextField("");
        tf.setBackground(Color.WHITE);
        northPanel.add(tf);
        add(northPanel, BorderLayout.NORTH);

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);

        // the 3 buttons
        logout = new JButton("Logout");
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

        // ok it is coming from the JTextField
        if (connected) {
            // just have to send the message
            s.writeMsg(new Message(Message.MESSAGE, tf.getText()));
            append("Moi : " + tf.getText());
            tf.setText("");
            return;
        }
    }
}
