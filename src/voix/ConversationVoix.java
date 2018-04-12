package voix;

import texte.Message;
import texte.SocketTexte;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by Malomek on 07/04/2018.
 */
public class ConversationVoix implements Runnable {
    private SocketVoix s;
    InputStream input;
    TargetDataLine targetDataLine;
    OutputStream out;
    AudioFormat audioFormat;
    SourceDataLine sourceDataLine;
    byte tempBuffer[] = new byte[10000];
    static Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

    public ConversationVoix(SocketVoix s){
        this.s = s;
    }

    public void captureAudio() {

        try {

            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, audioFormat);
            System.out.println(dataLineInfo.getFormats());
            Mixer mixer = null;
            System.out.println("Available mixers:");
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                mixer = AudioSystem.getMixer(mixerInfo[3]);
                if (mixer.isLineSupported(dataLineInfo)) {
                    System.out.println(mixerInfo[cnt].getName());
                    targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                }
            }
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new ServerVoix.CaptureThread();
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public AudioFormat getAudioFormat() {

        float sampleRate = 8000.0F;
        //float sampleRate = format.getSampleRate();
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
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
