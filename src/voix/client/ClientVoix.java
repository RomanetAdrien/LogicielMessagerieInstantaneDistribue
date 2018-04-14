package voix.client;

import javax.swing.*;

public class ClientVoix {
    public static boolean calling = false;

    public static void main(String[] args){
        JFrame frame = new JFrame("app");
        frame.setContentPane(new ClientGui().panel);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
        ClientGui g = new ClientGui();
    }
}
