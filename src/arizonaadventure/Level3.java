/*
 * File added by Nathan MacLeod 2020
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
    
    private SpikeObstacle mechDisappearSpike;
    private SpikeObstacle mechReappearSpike;
    private boolean bossHide = false;
    private boolean bossReapper = false;
    private boolean transitioned = false;
    
    public Level3(int width, int height, ArizonaAdventure game) {
        super(width, height, game);
    } 
    
    protected Music setTrack() {
        return new Music("lvl3bgtheme.wav");
    }
    
    protected void preloadSprites() {
        try {
            background = ImageIO.read(new File("./sprites/lvl3bg.png"));
            foreground = null;//ImageIO.read(new File("./sprites/lvl2fg.png"));
            middleground = ImageIO.read(new File("./sprites/lvl3mg.png"));
            transMid = ImageIO.read(new File("./sprites/lvl3transmg.png"));
            transMidfore = ImageIO.read(new File("./sprites/lvl3transffg.png"));
            newFore = null;
            newMid = ImageIO.read(new File("./sprites/lvl3labbg.png"));
            newMidfore = null;
            newBack = null;
            transWidth = transMid.getWidth();
            backgroundWidth = background.getWidth();
        } catch(IOException e) {
            System.out.println("failed to read level 3 backgrounds");
        }
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
        mechDisappearSpike = new SpikeObstacle(x, game, 0);
         x += width;
        
        //pattern;
        for(int i = 0; i < 7; i++) {
            new SpikeObstacle(x, game, 3);
            x += width;
            new SpikeObstacle(x, game, 0);
            x += width;
        }

        mechReappearSpike = new SpikeObstacle(x, game, 3);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        super.update(timePassed, game);
        
        if(!transitioned && finalWave()) {
            transitioned = true;
            transition();
        }
        
        if(bossFight && !bossHide && mechDisappearSpike.getX() < game.getGameWidth()/2.0) {
            ((MechBoss) boss).dissapearToLeft();
            bossHide = true;
        } 
        else if(bossFight && !bossReapper && mechReappearSpike.getX() < game.getGameWidth()/3.0) {
            game.addPickup(new HealthPickup(game.getGameWidth() + 170, game.getGameHeight()/2.0));
            ((MechBoss) boss).appearOnRight(game);
            bossReapper = true;
        }
    }
    
    protected void setWaves() {
        waves = new ArrayList(); 
        waves.add(new SpawnPeriod(60, 2, new int[] {20, 25, 5, 4, 0, 10, 0}));
        waves.add(new SpawnPeriod(40, 2, new int[] {7, 16, 4, 2, 0, 6, 9}));
        waves.add(new SpawnPeriod(5, 1, new int[] {0, 0, 0, 0, 0, 0, 0, 2}));
        waves.add(new SpawnPeriod(3, 1, new int[] {0, 0, 0, 0, 1, 0, 0, 0}));
        waves.add(new SpawnPeriod(75, 2.2, new int[] {8, 14, 4, 2, 0, 6, 5, 2}));
        waves.add(new SpawnPeriod(3, 1, new int[] {0, 0, 0, 0, 1, 0, 0, 0}));
        waves.add(new SpawnPeriod(50, 1, new int[] {0, 25, 0, 0, 0, 0, 0, 0}));
    }
    
    protected Boss spawnBoss(ArizonaAdventure game) {
        spawnSpikes(game);
        MechBoss b = new MechBoss(game);
        panSpeed = 7.0/2 * MechBoss.getWalkSpeed();
        //b.appearOnRight(game);
        return b;
    }
}
