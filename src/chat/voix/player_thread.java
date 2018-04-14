package chat.voix;

import application.Application;
import chat.ApplicationTexte;
import voix.server.ServerVoix;

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class player_thread extends Thread {
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[512];

    @Override
    public void run(){
        DatagramPacket incomming = new DatagramPacket(buffer, buffer.length);
        while (ApplicationTexte.callingPlayer){
            try {
                din.receive(incomming);
                buffer = incomming.getData();
                audio_out.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        audio_out.close();
        audio_out.drain();
        System.out.println("thread player stopped");
    }
}
