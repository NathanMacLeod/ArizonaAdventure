/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
/**
 *
 * @author macle
 */
public abstract class Level implements Updatable, Drawable {
    
    private int width;
    private int height;
    protected ArrayList<SpawnPeriod> waves;
    private SpawnPeriod currentWave;
    private int currentIndex;
    private CooldownTimer postBossWait;
    private double t;
    private boolean zoom;
    private boolean playerDead;
    
    private Boss boss;
    private boolean bossFight;
    private boolean bossDead;
    
    protected int backgroundWidth;
    protected BufferedImage foreground;
    protected BufferedImage middleground;
    protected BufferedImage background;
    
    private double panSpeed;
    protected double backMultiplier = 0.2;
    protected double midMultiplier = 0.27;
    
    private double foreX;
    private double middleX;
    private double backX;
    
    public Level(int width, int height) {
        setWaves();
        preloadSprites();
        boss = null;
        bossFight = false;
        bossDead = false;
        currentIndex = 0;
        currentWave = null;
        this.width = width;
        this.height = height;
        postBossWait = new CooldownTimer(1.0/5);
        postBossWait.resetTimer();
        zoom = false;
        panSpeed = 600;
        playerDead = false;
    }
    
    protected abstract void preloadSprites();
    
    protected abstract void setWaves();
    
    protected abstract Boss spawnBoss(ArizonaAdventure game);
    
    public boolean levelComplete() {
        return bossDead;
    }
    
    private void updatePans(double timePassed) {
        foreX += panSpeed * timePassed;
        middleX += panSpeed * timePassed * midMultiplier;
        backX += panSpeed * timePassed  * backMultiplier;
        
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
        if(foreground != null) {
            g.drawImage(foreground, -(int) foreX, 0, null);
            if(backgroundWidth - foreX < 1000) {
                g.drawImage(foreground, backgroundWidth - (int) foreX, 0, null);
            }
        }
        
        if(bossFight && !bossDead) {
            double hpWidth = 600;
            int hpX = (int) ((width - hpWidth)/2.0);
            int hpY = (int) 30;
            int hpHeight = 10;
            
            double hpRat = boss.getHealthPercentage();
            if(hpRat < 0) {
                hpRat = 0;
            }

            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(hpX + (int) (hpWidth * hpRat), hpY, (int) hpWidth - (int) (hpWidth * hpRat), hpHeight);
            g.setColor(new Color(230, 0, 0, 120));
            g.fillRect(hpX, hpY, (int) (hpWidth * hpRat), hpHeight);
        }
    }
    
    public void playerDead() {
        t = 3;
        playerDead = true;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        if(playerDead) {
            t -= timePassed;
            if(t <= 0) {
                game.returnFromGame(false);
            }
        }
        if(zoom) {
            panSpeed -= 1.5 * panSpeed * timePassed;
            t -= timePassed;
            if(t <= 0) {
                game.returnFromGame(true);
            }
        }
        updatePans(timePassed);
        if(bossDead) {
            postBossWait.updateTimer(timePassed);
            if(postBossWait.tryToFire()) {
                t = 3;
                zoom = true;
                game.getPlayer().startZoom();
            }
        }
        else if(bossFight) {
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
