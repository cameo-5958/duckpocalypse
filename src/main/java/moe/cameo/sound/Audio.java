package moe.cameo.sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {  
    // NON-STATIC OBJECT DATA
    private Clip clip;

    public Audio(String url) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(
                Audio.class.getResource("/sounds/" + url + ".wav")
            );

            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            clip = (Clip) AudioSystem.getLine(info); 
            clip.open(stream);

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("SOMETHING FAILED");
        }
    }


    public void play() {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.start();
    }
}
