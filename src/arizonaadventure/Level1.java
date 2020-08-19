/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
/**
 *
 * @author macle
 */
public class Level1 extends Level {
    
    public Level1(int width, int height, ArizonaAdventure game) {
        super(width, height, game);
    } 
    
    public void unload() {
        super.unload();
        TrainBoss.unloadAssets();
    }
    
    protected Music setTrack() {
        return new Music("lvl1bgtheme.wav");
    }
    
    protected void preloadSprites() {
        try {
            background = ImageIO.read(new File("./sprites/lvl1bg.png"));
            foreground = ImageIO.read(new File("./sprites/lvl1fg.png"));
            middleground = ImageIO.read(new File("./sprites/lvl1mg.png"));
            backgroundWidth = background.getWidth();
        } catch(IOException e) {
            System.out.println("failed to read level 1 backgrounds");
        }
        TrainBoss.loadSprites();
    }
    
    protected void setWaves() {
        waves = new ArrayList();
        waves.add(new SpawnPeriod(20, 3, new int[] {0, 9, 0, 0, 0, 0}));
        waves.add(new SpawnPeriod(50, 3, new int[] {10, 16, 0, 0, 0, 0}));
        waves.add(new SpawnPeriod(4, 1, new int[] {0, 0, 1, 0, 0, 0}));
        waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1, 0}));
        waves.add(new SpawnPeriod(70, 2, new int[] {14, 25, 1, 0, 0, 0}));
        waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1, 0}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        return new TrainBoss(game);
    }
    
}
