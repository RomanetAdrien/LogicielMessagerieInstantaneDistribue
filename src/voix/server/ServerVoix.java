package voix.server;


import javax.swing.*;

public class ServerVoix {

    public static boolean calling = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("app");
        frame.setContentPane(new ServerGui().panel1);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
        ServerVoix g = new ServerVoix();
    }
}
