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
    
    protected Boss boss;
    protected boolean bossFight;
    private boolean bossDead;
    
    protected int backgroundWidth;
    protected BufferedImage foreground;
    protected BufferedImage middleground;
    protected BufferedImage background;
    protected BufferedImage foremidGround;
    
    private boolean toTransition;
    protected boolean inTransition;
    protected int transWidth;
    protected BufferedImage transMid;
    protected BufferedImage transMidfore;
    
    protected BufferedImage newFore;
    protected BufferedImage newMid;
    protected BufferedImage newBack;
    protected BufferedImage newMidfore;
    
    protected double panSpeed;
    protected double backMultiplier = 0.2;
    protected double midMultiplier = 0.27;
    
    private double foreX;
    private double middleX;
    private double backX;
    
    public Level(int width, int height) {
        foremidGround = null;
        panSpeed = 600;
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
        playerDead = false;
    }
    
    protected boolean finalWave() {
        return currentIndex >= waves.size();
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
        
        if(inTransition) {
            if(middleX > transWidth) {
                middleX -= transWidth;
                inTransition = false;
                foreground = newFore;
                middleground = newMid;
                foremidGround = newMidfore;
                background = newBack;
                backgroundWidth = middleground.getWidth();
            }
        }
        else if(middleX > backgroundWidth) {
            middleX -= backgroundWidth;
            if(toTransition) {
               toTransition = false;
                inTransition = true;
            }
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
        
        if(toTransition) {
            g.drawImage(middleground, - (int) middleX, 0, null);
            if(backgroundWidth - middleX < 1000) {
                g.drawImage(transMid, backgroundWidth - (int) middleX, 0, null);
            }
        }
        else if(inTransition) {
            g.drawImage(transMid, - (int) middleX, 0, null);
            if(transWidth - middleX < 1000) {
                g.drawImage(newMid, transWidth - (int) middleX, 0, null);
            }
        }
        else if(middleground != null) {
            g.drawImage(middleground, -(int) middleX, 0, null);
            if(backgroundWidth - middleX < 1000) {
                g.drawImage(middleground, backgroundWidth - (int) middleX, 0, null);
            }
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
    
    public void transition() {
        //Transitions sensitive to timing, can teleport onscreen with current implementation
        //to fix play with spawn period timings
        toTransition = true;
    }
    
    public void drawForeMid(Graphics2D g) {
        if(toTransition) {
            if(foremidGround != null) {
                g.drawImage(foremidGround, - (int) middleX, 0, null);
            }
            if(transMidfore != null && backgroundWidth - middleX < 1000) {
                g.drawImage(transMidfore, backgroundWidth - (int) middleX, 0, null);
            }
        }
        else if(inTransition) {
            if(transMidfore != null) {
                g.drawImage(transMidfore, - (int) middleX, 0, null);
            }
            if(newMidfore != null && transWidth - middleX < 1000) {
                g.drawImage(newMidfore, transWidth - (int) middleX, 0, null);
            }
        }
        else if(foremidGround != null) {
            g.drawImage(foremidGround, -(int) middleX, 0, null);
            if(backgroundWidth - middleX < 1000) {
                g.drawImage(foremidGround, backgroundWidth - (int) middleX, 0, null);
            }
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
            if(!(boss instanceof KillableEntity)) {
                boss.update(timePassed, game);
            }
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
