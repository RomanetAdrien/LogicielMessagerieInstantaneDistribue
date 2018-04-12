package voix;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static javax.sound.sampled.AudioSystem.getTargetEncodings;
import static javax.sound.sampled.FloatControl.Type.SAMPLE_RATE;

public class Client {
    boolean stopCapture = false;
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;
    BufferedOutputStream out = null;
    BufferedInputStream in = null;
    Socket sock = null;
    SourceDataLine sourceDataLine;

    public static void main(String[] args) {
        Client tx = new Client();
        tx.captureAudio();
    }
    public static void displayMixerInfo()
    {
        Mixer.Info [] mixersInfo = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixersInfo)
        {
            System.out.println("Mixer: " + mixerInfo.getName());

            Mixer mixer = AudioSystem.getMixer(mixerInfo);

            Line.Info [] sourceLineInfo = mixer.getSourceLineInfo();
            for (Line.Info info : sourceLineInfo)
                showLineInfo(info);

            Line.Info [] targetLineInfo = mixer.getTargetLineInfo();
            for (Line.Info info : targetLineInfo)
                showLineInfo(info);
        }
    }
    private static void showLineInfo(final Line.Info lineInfo)
    {
        System.out.println("  " + lineInfo.toString());

        if (lineInfo instanceof DataLine.Info)
        {
            DataLine.Info dataLineInfo = (DataLine.Info)lineInfo;

            AudioFormat [] formats = dataLineInfo.getFormats();
            for (final AudioFormat format : formats)
                System.out.println("    " + format.toString());
        }
    }

    private void captureAudio() {
        displayMixerInfo();
        try {
            sock = new Socket("192.168.0.125", 500);
            out = new BufferedOutputStream(sock.getOutputStream());
            in = new BufferedInputStream(sock.getInputStream());

            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            System.out.println("Available mixers:");
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                System.out.println(mixerInfo[cnt].getName());
            }
            audioFormat = getAudioFormat();
            //AudioFormat.Encoding[] encoding = getTargetEncodings(audioFormat);


            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);


           /* Mixer mixer = AudioSystem.getMixer(null); // default mixer
            mixer.open();
            System.out.printf("Supported SourceDataLines of default mixer (%s):\n\n", mixer.getMixerInfo().getName());
            for(Line.Info info : mixer.getSourceLineInfo()) {
                if(SourceDataLine.class.isAssignableFrom(info.getLineClass())) {
                    SourceDataLine.Info info2 = (SourceDataLine.Info) info;
                    System.out.println(info2);
                    System.out.printf("  max buffer size: \t%d\n", info2.getMaxBufferSize());
                    System.out.printf("  min buffer size: \t%d\n", info2.getMinBufferSize());
                    AudioFormat[] formats = info2.getFormats();
                    System.out.println("  Supported Audio formats: ");
                    for(AudioFormat format : formats) {
                        System.out.println("    "+format);
                    }
                    System.out.println();
                } else {
                    System.out.println(info.toString());
                }
                System.out.println();
            }*/


            Mixer mixer = AudioSystem.getMixer(mixerInfo[2]);

            if(!AudioSystem.isLineSupported(dataLineInfo)){

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                if (AudioSystem.isLineSupported(info)) { sourceDataLine = AudioSystem.getSourceDataLine(audioFormat, null ); }

                targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);

            }
            targetDataLine=AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new CaptureThread();
            captureThread.start();

            DataLine.Info dataLineInfo1 = new DataLine.Info(
                    SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem
                    .getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            Thread playThread = new PlayThread();
            playThread.start();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        @Override
        public void run() {
            byteArrayOutputStream = new ByteArrayOutputStream();
            stopCapture = false;
            try {
                while (!stopCapture) {

                    int cnt = targetDataLine.read(tempBuffer, 0,
                            tempBuffer.length);

                    out.write(tempBuffer);

                    if (cnt > 0) {

                        byteArrayOutputStream.write(tempBuffer, 0, cnt);

                    }
                }
                byteArrayOutputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //float sampleRate = format.getSampleRate();

        int sampleSizeInBits = 8;

        int channels = 1;

        boolean signed = true;

        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        //return new AudioFormat(8000.0f, 16, 1, true, true);
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        @Override
        public void run() {
            try {
                while (in.read(tempBuffer) != -1) {
                    sourceDataLine.write(tempBuffer, 0, 10000);

                }
                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}