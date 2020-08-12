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
public class Level4 extends Level{
    
    public Level4(int width, int height, ArizonaAdventure game) {
        super(width, height, game);
        panSpeed = 120;
    } 
    
    public void unload() {
        super.unload();
    }
    
    protected Music setTrack() {
        return new Music("lvl4bgtheme.wav");
    }
    
    protected void preloadSprites() {
        try {
            background = ImageIO.read(new File("./sprites/lvl4bg.png"));
            foreground = null;
            middleground = null;
            backgroundWidth = background.getWidth();
        } catch(IOException e) {
            System.out.println("failed to read level 4 backgrounds");
        }
        TheTower.loadSprite();
    }
    
    public void playerDead(ArizonaAdventure game) {
        if(!bossFight) {
            super.playerDead(game);
        }
    }
    
    protected void setWaves() {
        waves = new ArrayList();
        waves.add(new SpawnPeriod(95, 1.7, new int[] {8, 14, 6, 3, 0, 6, 5, 4}));
        waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1, 0}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        return new TheTower(game);
    }
    
}
