package texte;

import java.io.Serializable;

/**
 * Cette classe définit les échanges attendu entre les clients Texte
 */
public class Message implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    // Les types de messages
    // MESSAGE un simple message texte
    // LOGOUT MessageAnnuaire de déconnexion
    public static final int MESSAGE = 0, LOGOUT = 1;
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
