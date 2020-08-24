/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Color;
import java.util.ArrayList;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class Player extends KillableEntity {   
    private static Sprite sprite = null;
    private static Sprite halfSprite = null;
    private static final String primary = "playerpew.wav";
    private static final String damageTaken = "damage.wav";
    private static final String healSound = "heal.wav";
    private static boolean spriteMini;
    
    private static double defWidth = 90;
    private static double defHeight = 15;
    private static double mini = 0.67;
    
    private double width;
    private double height;
    private double hVel;
    private double vVel;
    private double bulletVel;
    private int nProjectiles;
    private CooldownTimer primaryFire;
    private CooldownTimer rocketFire;
    private CooldownTimer invincibleTime;
    
    private boolean hasRockets;
    private boolean zoomOut;
    private boolean zoomIn;
    private double currSpeed;
    private boolean invincible;
    private double maxHP;
    
    public Player(double x, double y, UpgradeList upgrades) {
        super(x, y, generateSquareHitbox(defWidth * ((upgrades.miniShip)? mini : 1), defHeight * ((upgrades.miniShip)? mini : 1)), 100, 70);
        width = defWidth * ((upgrades.miniShip)? mini : 1);
        height = defHeight * ((upgrades.miniShip)? mini : 1);
        maxHP = 100 * (1 + 0.25 * upgrades.health) * ((upgrades.miniShip)? 0.7 : 1);
        hp = maxHP;
        hVel = 270;
        vVel = hVel;
        bulletVel = 600;
        nProjectiles = 1 + upgrades.fireVolume;
        double fireRate = 3 + 0.33 * upgrades.fireRate;
        primaryFire = new CooldownTimer(fireRate);
        rocketFire = new CooldownTimer(fireRate / 10);
        invincibleTime = new CooldownTimer(0.8);
        hasRockets = upgrades.missiles;
        if(sprite == null || upgrades.miniShip != spriteMini) {
            spriteMini = upgrades.miniShip;
            sprite = new Sprite("arizona15.png", (int) (width * 1.25));
            halfSprite = new Sprite("arizona15trans.png", (int) (width * 1.25));
        }
    }
    
    private void takeUserInput(double timePassed, ArizonaAdventure game) {

        double xVel = 0, yVel = 0;
        if(game.getW() && y > 0) {
            yVel = -vVel;
        }
        else if(game.getS() && y < game.getGameHeight()) {
            yVel = vVel;
        }
        if(game.getA() && x > 0) {
            xVel = -hVel;
        }
        else if(game.getD() && x < game.getGameWidth()) {
            xVel = hVel;
        }
        this.moveEntity(xVel * timePassed, yVel * timePassed, 0);
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        primaryFire.updateTimer(timePassed);
        if(primaryFire.tryToFire()) {
            //primary.play();
            double bulletSpacing = 25;
            double space = nProjectiles * bulletSpacing;
            
            for(int i = 0; i < nProjectiles; i++) {
                game.addNewProjectile(new PrimaryFireBullet(x + width/2.0, y + space * (-0.5 + (double)(i + 1)/(nProjectiles + 1)), 20));
            }
        }
        if(hasRockets) {
            rocketFire.updateTimer(timePassed);
            if(rocketFire.tryToFire()) {
                game.addNewProjectile(new PlayerRocket(x, y, 80));
            }
        }
    }
    
    private void checkForCollisions(ArizonaAdventure game) {
        if(!invincible) {
            for(KillableEntity e : game.getEnemies()) {
                if(!e.nonPlayerCollidable && this.hitboxesIntersecting(e)) {
                    e.takeDamage(200);
                    this.takeDamage(35);
                    break;
                }
            }
        }
        for(Pickup p : game.getPickups()) {
            if(this.hitboxesIntersecting(p)) {
                p.setConsumed();
                if(p instanceof HealthPickup) {
                    SoundManager.play(healSound);
                    hp = maxHP;
                }
            }
        }
    }
    
    public void takeDamage(double damage) {
        if(!invincible) {
            SoundManager.play(damageTaken);
            super.takeDamage(damage);
            invincible = true;
            invincibleTime.resetTimer();
        }
    }
    
    private void drawHP(Graphics2D g) {
        if(hp < maxHP) {
            
            double hpWidth = 0.8 * width;
            int hpX = (int) (x - hpWidth/2.0);
            int hpY = (int) (y + height);
            int hpHeight = 7;
            
            g.setColor(new Color(255, 0, 0, 120));
            g.fillRect(hpX + (int) (hpWidth * hp/maxHP), hpY, (int) hpWidth - (int) (hpWidth * hp/maxHP), hpHeight);
            g.setColor(new Color(0, 255, 0, 120));
            g.fillRect(hpX, hpY, (int) (hpWidth * hp/maxHP), hpHeight);
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        if(invincible)
            halfSprite.draw(g, x, y, orientation);
        else
            sprite.draw(g, x, y, orientation);
        drawHP(g);
    }
    
    public void startZoom() {
        zoomOut = true;
        currSpeed = -1000;
    }
    
    public void startZoomIn() {
        zoomIn = true;
        currSpeed = 100;
    }
    
    private void zoomIn(double timePassed) {
        moveEntity(currSpeed * timePassed, 0, 0);
        if (x >= 100) {
            zoomIn = false;
        }
    }
    
    private void zoom(double timePassed) {
        currSpeed += timePassed * 2000;
        moveEntity(currSpeed * timePassed, 0, (currSpeed < 0)? 0 : currSpeed * timePassed / 50);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        if(zoomOut) {
            zoom(timePassed);
        }
        else if(zoomIn) {
            zoomIn(timePassed);
        }
        else {
            takeUserInput(timePassed, game);
            shoot(timePassed, game);
            checkForCollisions(game);
        }    
        if(invincible) {
            invincibleTime.updateTimer(timePassed);
            if(invincibleTime.tryToFire()) {
                invincible = false;
            }
        }
    }
    
}
