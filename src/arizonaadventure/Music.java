/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
/**
 *
 * @author macle
 */
public class Music implements Audible {
    private Clip music;
    private double vol;
    private boolean fadding = false;
    private boolean fadeIn = false;
    private double fadeRate;
    
    public Music(String file) {
        try {
            File f = new File("./sfx/" + file);
            AudioInputStream input = AudioSystem.getAudioInputStream(f);
            music = AudioSystem.getClip();
            music.open(input);
            FloatControl musicVol = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
            musicVol.setValue(Float.NEGATIVE_INFINITY);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public void stopAudio() {
        stop();
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        if(fadding || fadeIn) {
            vol += ((fadding)? -1 : 1) * fadeRate * timePassed;
            if(fadding && vol <= 0) {
                stop();
                fadding = false;
            }
            else if(fadeIn && vol >= 1) {
                fadeIn = false;
                vol = 1;
            }
            FloatControl musicVol = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(vol) / Math.log(10.0) * 20);
            if(dB < -80) {
                dB = -80;
            }
            musicVol.setValue(dB);
        }
    }
    
    public void play() {
        music.loop(Clip.LOOP_CONTINUOUSLY);
        //music.setFramePosition(0);
        music.start();
        FloatControl musicVol = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
        musicVol.setValue(0);
    }
    
    public void fadeOut(double fadeTime) {
        fadding = true;
        vol = 1.0;
        fadeRate = 1.0/fadeTime;
    }
    
    public void resume() {
        FloatControl musicVol = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
        musicVol.setValue(0);
        music.start();
    }
    
    public void fadeIn(double time) {
        music.start();
        music.loop(Clip.LOOP_CONTINUOUSLY);
        music.setFramePosition(0);
        FloatControl musicVol = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
        musicVol.setValue(Float.NEGATIVE_INFINITY);
        vol = 0;
        fadeRate = 1.0/time;
        fadeIn = true;    
    }
    
    public void stop() {
        music.stop();
    }
    
    public void close() {
        music.close();
    }
}
