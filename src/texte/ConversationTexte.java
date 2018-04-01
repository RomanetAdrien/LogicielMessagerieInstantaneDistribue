package texte;

import java.util.Scanner;

public class ConversationTexte implements Runnable{
    private SocketTexte s;

    public ConversationTexte(SocketTexte s){
        this.s = s;
    }

    @Override
    public void run() {
        // wait for messages from user
        Scanner scan = new Scanner(System.in);
        // loop forever for message from the user
        while(true) {
            System.out.print("> ");
            // read message from user
            String msg = scan.nextLine();
            // logout if message is LOGOUT
            if(msg.equalsIgnoreCase("LOGOUT")) {
                s.writeMsg(new Message(Message.LOGOUT, ""));
                // break to do the disconnect
                break;
            }
            else {				// default to ordinary message
                s.writeMsg(new Message(Message.MESSAGE, msg));
            }
        }
        // done disconnect
        s.close();
    }
}
