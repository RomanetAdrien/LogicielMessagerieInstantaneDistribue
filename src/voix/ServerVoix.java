package voix;

import texte.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Malomek on 07/04/2018.
 */
public class ServerVoix implements Runnable{

    /**
     * Variables
     */
    // un identifiant unique
    protected static int uniqueID;
    // La date et l'heure
    private SimpleDateFormat sdf;
    // Le port d'écoute
    private int port;
    // Le boolean qui permet d'eteindre le serveur
    private boolean keepGoing;
    ServerSocket MyService;
    Socket clientSocket = null;
    InputStream input;
    TargetDataLine targetDataLine;
    OutputStream out;
    AudioFormat audioFormat;
    SourceDataLine sourceDataLine;
    byte tempBuffer[] = new byte[10000];
    static Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

    /**
     * Constructeur
     */
    ServerVoix(int port) throws LineUnavailableException {
        // On initialise le port
        this.port = port;
        // On initialise le format de la date
        sdf = new SimpleDateFormat("HH:mm:ss");


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

    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        @Override
        public void run() {
            keepGoing = true;
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }


            while (keepGoing) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    try {
                        out.write(tempBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Mixer mixer_ = AudioSystem.getMixer(mixerInfo[0]);
                        audioFormat = getAudioFormat();
                        Socket socket = serverSocket.accept();
                        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                        sourceDataLine.open(audioFormat);
                        sourceDataLine.start();
                        MyService = new ServerSocket(500);
                        clientSocket = MyService.accept();
                        SocketVoix v = new SocketVoix(serverSocket.accept());
                        new ConversationVoix(v).captureAudio();
                        input = new BufferedInputStream(clientSocket.getInputStream());
                        out = new BufferedOutputStream(clientSocket.getOutputStream());
                        while (input.read(tempBuffer) != -1) {
                            sourceDataLine.write(tempBuffer, 0, 10000);

                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }

                }
        }
    }



    /**
     * Methodes
     */
    // Boucle du serveur
    @Override
    public void run() {
        keepGoing = true;
        // créer un socket et attendre
        try
        {
            // Le socket du serveur
            ServerSocket serverSocket = new ServerSocket(port);

            // Boucle infini pour attendre une connexion
            while(keepGoing)
            {
                // Attends une connexion
                System.out.println("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                voix.SocketVoix t = new voix.SocketVoix(socket);  // make a thread of it
                ApplicationVoix.socketVoixList.add(t);									// save it in the ArrayList
                t.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for(int i = 0; i < ApplicationVoix.socketVoixList.size(); ++i) {
                    voix.SocketVoix tc = ApplicationVoix.socketVoixList.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                        // not much I can do
                    }
                }
            }
            catch(Exception e) {
                System.out.println("Erreur lors de fermetture du serveur: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < ApplicationVoix.socketVoixList.size(); ++i) {
            voix.SocketVoix ct = ApplicationVoix.socketVoixList.get(i);
            // found it
            if(ct.id == id) {
                ApplicationVoix.socketVoixList.remove(i);
                return;
            }
        }
    }

}
