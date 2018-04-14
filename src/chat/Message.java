package chat;

import java.io.Serializable;

/**
 * Cette classe définit les échanges attendu entre les clients Texte
 */
public class Message implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    // Les types de messages
    // MESSAGE un simple message texte
    // LOGOUT MessageAnnuaire de déconnexion
    // CALLDEMAND
    // CALLACCEPT
    // CALLCLOSE
    public static final int MESSAGE = 0, LOGOUT = 1, CALLDEMAND = 2, CALLACCEPT = 3, CALLCLOSE = 4;
    private int type;
    private String message;

    /** constructor */
    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    /** getters */
    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }


}
