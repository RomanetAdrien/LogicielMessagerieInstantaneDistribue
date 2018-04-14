package voix.server;

import voix.player_thread;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerGui {

    int port = 8888;

    public ServerGui() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init_audio();
            }
        });
    }

    public static AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        int sampleSizeInbits = 16;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
    }

    public SourceDataLine audio_out;


    private JButton startButton;
    public JPanel panel1;

    public void init_audio(){
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
            if(!AudioSystem.isLineSupported(info_out)){
                System.out.println("no support");
                System.exit(0);
            }
            audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
            audio_out.open(format);
            audio_out.start();
            player_thread p = new player_thread();
            p.din = new DatagramSocket(port);
            p.audio_out = audio_out;
            ServerVoix.calling = true;
            p.start();
            startButton.setEnabled(false);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
}
