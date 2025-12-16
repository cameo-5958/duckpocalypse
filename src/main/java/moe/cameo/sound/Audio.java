package moe.cameo.sound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {  
    // NON-STATIC OBJECT DATA
    private final String url;

    public Audio(String url) {
        this.url = url;
    }

    public final void play() {
        try {
            // Load the clip and get audio stream
            Clip clip = AudioSystem.getClip();
            AudioInputStream input_stream = AudioSystem.getAudioInputStream(
                Audio.class.getResourceAsStream("/sounds/" + url + ".mp4")
            );

            // Play the sound
            clip.open(input_stream);
            clip.start();
        } catch (LineUnavailableException e) {
            System.out.println("Error happened.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file was unsupported.");
        } catch (IOException e) {
            System.out.println("IO Exception occured.");
        }
    }
}
