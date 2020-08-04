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
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class Level3 extends Level{
    
    private SpikeObstacle finalSpike;
    private boolean bossSecondPhase = false;
    
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
        panSpeed = 7.0/2 * MechBoss.getWalkSpeed();
        backMultiplier = 0.06;
        MechBoss.loadSprites();
    }
    
    private void spawnSpikes(ArizonaAdventure game) {
        double width = SpikeObstacle.getWidth() * 1.2;
        double x = game.getGameWidth() + width;
        
        //introduction
        
        new SpikeObstacle(x, game, 0);
        x += width * 4;
        new SpikeObstacle(x, game, 0);
        x += width * 4;
        new SpikeObstacle(x, game, 0);
        
        new SpikeObstacle(x, game, 0);
        x += width;
        new SpikeObstacle(x, game, 1);
        x += width;
        new SpikeObstacle(x, game, 2);
        x += width;
        new SpikeObstacle(x, game, 3);
        x += width * 4;
        
        //wave
        double t = 0;
        for(int i = 0; i < 10; i++) {
            new SpikeObstacle(x, game, t);
            t += 1.45;
            x += width;
        }
        
        x += width * 4;
        //pattern;
        
        for(int i = 0; i < 3; i++) {
            new SpikeObstacle(x, game, 0);
            x += width;
            new SpikeObstacle(x, game, 0);
            x += width;
            new SpikeObstacle(x, game, 0);
            x += width;

            new SpikeObstacle(x, game, 3);
            x += width;
            new SpikeObstacle(x, game, 3);
            x += width;
            new SpikeObstacle(x, game, 3);
            x += width;
        }
        
        x += width * 3;
        finalSpike = new SpikeObstacle(x, game, 0);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        super.update(timePassed, game);
        if(bossFight && !bossSecondPhase && finalSpike.getX() < game.getGameWidth()/2.0) {
            game.addPickup(new HealthPickup(game.getGameWidth() + 170, game.getGameHeight()/2.0));
            ((MechBoss) boss).dissapearToLeft();
            bossSecondPhase = true;
        } 
    }
    
    protected void setWaves() {
        waves = new ArrayList();
        //waves.add(new SpawnPeriod(60, 2, new int[] {20, 25, 5, 4, 0, 10, 100}));
        //waves.add(new SpawnPeriod(50, 3, new int[] {9, 16, 2, 0, 0, 7}));
        //waves.add(new SpawnPeriod(4, 1, new int[] {0, 0, 0, 3, 0, 0}));
        //waves.add(new SpawnPeriod(70, 2.5, new int[] {8, 16, 4, 3, 0, 7}));
        //waves.add(new SpawnPeriod(6, 1, new int[] {0, 0, 0, 0, 1, 0}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        spawnSpikes(game);
        return new MechBoss(game);
    }
}
