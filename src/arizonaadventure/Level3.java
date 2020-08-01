/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author macle
 */
public class Level3 extends Level{
    
    public Level3(int width, int height) {
        super(width, height);
    } 
    
    protected void preloadSprites() {
        try {
            background = ImageIO.read(new File("./sprites/lvl3bg.png"));
            foreground = null;//ImageIO.read(new File("./sprites/lvl2fg.png"));
            middleground = ImageIO.read(new File("./sprites/lvl3mg.png"));
            backgroundWidth = background.getWidth();
        } catch(IOException e) {
            System.out.println("failed to read level 1 backgrounds");
        }
        backMultiplier = 0.06;
        SubBoss.loadSprites();
    }
    
    protected void setWaves() {
        waves = new ArrayList();
        waves.add(new SpawnPeriod(60, 2, new int[] {20, 25, 5, 4, 0, 10}));
        waves.add(new SpawnPeriod(50, 3, new int[] {9, 16, 2, 0, 0, 7}));
        waves.add(new SpawnPeriod(4, 1, new int[] {0, 0, 0, 3, 0, 0}));
        waves.add(new SpawnPeriod(70, 2.5, new int[] {8, 16, 4, 3, 0, 7}));
        waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1, 0}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        return new SubBoss(game);
    }
}
