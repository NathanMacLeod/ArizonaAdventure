/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;

/**
 *
 * @author macle
 */
public class SoundManager {
    private static final int buffSize = 64;
    private static Clip[] buff = new Clip[buffSize];
    private static int buffI = 0;
        
        
        public static void stopAudio() {
            for(int i = 0; i < buffSize; i++) {
                terminateSFX(i);
            }
        }
        
        private static class AudioThread implements Runnable {
            private int ID;
            private boolean looped;
            private String effect;
            private Clip sfx;
            
            public AudioThread(int ID, boolean looped, String effect) {
                this.ID = ID;
                this.looped = looped;
                this.effect = "./sfx/" + effect;
                new Thread(this).start();
            }             
            
            public void run() {
                try {
                    AudioInputStream input = AudioSystem.getAudioInputStream(new File(effect));
                    sfx = AudioSystem.getClip();
                    sfx.open(input);
                    if(looped) {
                        sfx.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    sfx.start();
                    buff[ID] = sfx;
                    sfx.addLineListener(new LineListener() {
                    public void update(LineEvent myLineEvent) {
                        if (myLineEvent.getType() == LineEvent.Type.STOP) {
                            sfx.close();
                        }
                    }
                  });
                    
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            }
        
        }
        
        private static class ClipTerminateThread implements Runnable {
            Clip clip;
            
            public ClipTerminateThread(Clip clip) {
                this.clip = clip;
                new Thread(this).start();
            }
            
            public void run() {
                clip.close();
            }
        }
        
        public static void terminateSFX(int id) {
            if(buff[id] != null) {
                new ClipTerminateThread(buff[id]);
                buff[id] = null;
            }
        }
        
        private static int play(boolean looped, String effect) {
            int i = buffI;
            new AudioThread(buffI, looped, effect);
            buffI++;
            if(buffI >= buffSize) {
                buffI = 0;
            }
            return i;
        }
        
        public static int play(String effect) {
            return play(false, effect);
        }
        
        public static int playLooped( String effect) {
            return play(true, effect);
        }
}
