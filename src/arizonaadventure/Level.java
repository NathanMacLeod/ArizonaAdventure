/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
 *
 * @author macle
 */
public abstract class Level implements Updatable, Drawable {
    
    protected ArrayList<SpawnPeriod> waves;
    private SpawnPeriod currentWave;
    private int currentIndex;
    
    private Boss boss;
    private boolean bossFight;
    private boolean bossDead;
    
    protected int backgroundWidth;
    protected BufferedImage foreground;
    protected BufferedImage middleground;
    protected BufferedImage background;
    
    private double panSpeed;
    
    private double foreX;
    private double middleX;
    private double backX;
    
    public Level() {
        setWaves();
        preloadSprites();
        boss = null;
        bossFight = false;
        bossDead = false;
        currentIndex = 0;
        currentWave = null;
        
        panSpeed = 600;
    }
    
    protected abstract void preloadSprites();
    
    protected abstract void setWaves();
    
    protected abstract Boss spawnBoss(ArizonaAdventure game);
    
    public boolean levelComplete() {
        return bossDead;
    }
    
    private void updatePans(double timePassed) {
        foreX += panSpeed * timePassed;
        middleX += panSpeed * timePassed * 0.2;
        backX += panSpeed * timePassed  * 0.15;
        
        if(foreX > backgroundWidth) {
            foreX -= backgroundWidth;
        }
        if(middleX > backgroundWidth) {
            middleX -= backgroundWidth;
        }
        if(backX > backgroundWidth) {
            backX -= backgroundWidth;
        }
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(background, (int) -backX, 0, null);
        if(backgroundWidth - backX < 1000) {
            g.drawImage(background, backgroundWidth - (int) backX, 0, null);
        }
        g.drawImage(middleground, - (int) middleX, 0, null);
        if(backgroundWidth - middleX < 1000) {
            g.drawImage(middleground, backgroundWidth - (int) middleX, 0, null);
        }
        g.drawImage(foreground, -(int) foreX, 0, null);
        if(backgroundWidth - foreX < 1000) {
            g.drawImage(foreground, backgroundWidth - (int) foreX, 0, null);
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        updatePans(timePassed);
        if(bossFight && !bossDead) {
            boss.update(timePassed, game);
            if(boss.bossDefeated()) {
                bossDead = true;
            }
        }
        else if(!bossFight) {
            if(currentWave == null) {
                if(currentIndex >= waves.size()) {
                    bossFight = true;
                    boss = spawnBoss(game);
                }
                else {
                    currentWave = waves.get(currentIndex);
                    currentIndex++;
                }
            }
            else {
                currentWave.update(timePassed, game);
                if(currentWave.periodFinished() && game.allEnemiesDead()) {
                    currentWave = null;
                }
            }
        }
    }
    
}
