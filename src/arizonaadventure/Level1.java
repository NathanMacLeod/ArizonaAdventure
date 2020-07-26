/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        //waves.add(new SpawnPeriod(20, 3, new int[] {0, 9, 0, 0, 0}));
        //waves.add(new SpawnPeriod(50, 3, new int[] {8, 16, 0, 0, 0}));
        //waves.add(new SpawnPeriod(4, 1, new int[] {0, 0, 1, 0, 0}));
        //waves.add(new SpawnPeriod(70, 2, new int[] {12, 25, 1, 0, 0}));
        //waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        return new TrainBoss(game);
    }
    
}
