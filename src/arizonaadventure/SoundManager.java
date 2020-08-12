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
            
            public AudioThread(int ID, boolean looped, String effect) {
                this.ID = ID;
                this.looped = looped;
                this.effect = "./sfx/" + effect;
                new Thread(this).start();
            } 
            
            public void run() {
                try {
                    AudioInputStream input = AudioSystem.getAudioInputStream(new File(effect));
                    Clip sfx = AudioSystem.getClip();
                    sfx.open(input);
                    if(looped) {
                        sfx.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    sfx.start();
                    buff[ID] = sfx;
                    
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            }
        
        }
        
        public static void terminateSFX(int id) {
            if(buff[id] != null) {
                buff[id].close();
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
