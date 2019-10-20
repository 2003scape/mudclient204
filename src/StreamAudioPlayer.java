import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class StreamAudioPlayer {
    private final static int SAMPLE_RATE = 8000;
    private final static int BIT_SAMPLE_SIZE = 8;
    private final static int CHANNELS = 1;

    private AudioFormat ulawFormat = new AudioFormat(AudioFormat.Encoding.ULAW, SAMPLE_RATE, BIT_SAMPLE_SIZE, CHANNELS,
            1, SAMPLE_RATE, false);

    private AudioFormat outputFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, ulawFormat.getSampleRate(),
            ulawFormat.getSampleSizeInBits() * 2, ulawFormat.getChannels(), ulawFormat.getFrameSize() * 2,
            ulawFormat.getFrameRate(), false);

    private SourceDataLine sourceLine;

    public StreamAudioPlayer() {
        try {
            sourceLine = AudioSystem.getSourceDataLine(outputFormat);
            sourceLine.open(outputFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopPlayer() {
        sourceLine.close();
    }

    public synchronized void writeStream(byte buf[], int off, int len) {
        AudioInputStream ulawStream = new AudioInputStream(new ByteArrayInputStream(buf, off, len), ulawFormat, len);
        AudioInputStream pcmStream = AudioSystem.getAudioInputStream(outputFormat, ulawStream);

        new Thread(() -> {
            try {
                byte[] pcmBytes = pcmStream.readAllBytes();
                sourceLine.start();
                sourceLine.write(pcmBytes, 0, pcmBytes.length);
                sourceLine.drain();

                do { 
                    Thread.sleep(100);
                } while (sourceLine.isRunning());

                sourceLine.stop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
