package chat;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationTexte{
    /**
     * Variable
     */
    // L'executor pour la gestion des threads
    private static ExecutorService executor;
    // Une liste de client
    protected static ArrayList<ConversationTexte> ConversationTexteList;
    // Le server de reception des nouvelles connexions
    ServerChat serverChat;
    // Mon nom
    public static String monPseudo;

    // Pour savoir si on a un appel en cours
    public static boolean callingPlayer = false;
    public static boolean callingRecorder = false;

    /**
     * Constructeur
     */
    public ApplicationTexte(int portT,int portVoix, String monPseudo){
        // On initialise l'executor
        executor = Executors.newFixedThreadPool(2);
        // On initialise le tableau de socket
        ConversationTexteList = new ArrayList<ConversationTexte>();
        // On initialise le serveur de connexion entrante
        serverChat = new ServerChat(portT, portVoix);
        // On initialise le pseudo
        this.monPseudo = monPseudo;
        // on lance le serveur dans un thread via l'executor
        executor.execute(serverChat);
    }

    /**
     * Methodes
     */

    public void nouveauChat(String serverAddress, int portNumber, int portVoix){
        ConversationTexte c = new ConversationTexte(serverAddress,portNumber, portVoix);
        ConversationTexteList.add(c);
        c.start();
    }

    public static AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        int sampleSizeInbits = 16;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
    }
}
