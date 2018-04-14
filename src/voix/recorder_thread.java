package voix;

import voix.client.ClientVoix;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class recorder_thread extends Thread {

    public TargetDataLine audio_in = null;
    public DatagramSocket dout;
    byte byte_buff[] = new byte[512];
    public InetAddress server_ip;
    public int server_port;

    @Override
    public void run(){
        int i = 0;
        while(ClientVoix.calling){
            try {
                audio_in.read(byte_buff, 0, byte_buff.length);
                DatagramPacket data = new DatagramPacket(byte_buff, byte_buff.length, server_ip, server_port);
                System.err.println("send : #"+i++);
                dout.send(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        audio_in.close();
        audio_in.drain();
        System.out.println("Thread recorder stopped");
    }

}