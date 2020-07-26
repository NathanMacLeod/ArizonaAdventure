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
    
    static double width = 90;
    static double height = 15;
    
    double hVel;
    double vVel;
    double bulletVel;
    CooldownTimer primaryFire;
    CooldownTimer rocketFire;
    CooldownTimer invincibleTime;
    
    private boolean invincible;
    private double maxHP;
    
    public Player(double x, double y) {
        super(x, y, generateSquareHitbox(width, height), 100, 70);
        maxHP = 100;
        hVel = 250;
        vVel = 250;
        bulletVel = 600;
        primaryFire = new CooldownTimer(3);
        rocketFire = new CooldownTimer(0.33);
        invincibleTime = new CooldownTimer(0.8);
        if(sprite == null) {
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
            game.addNewProjectile(new PrimaryFireBullet(x + width/2.0, y, 20));
        }
        rocketFire.updateTimer(timePassed);
        if(rocketFire.tryToFire()) {
            //game.addNewProjectile(new PlayerRocket(x, y, 80));
        }
    }
    
    private void checkForCollisions(ArizonaAdventure game) {
        if(!invincible) {
            for(KillableEntity e : game.getEnemies()) {
                if(this.hitboxesIntersecting(e)) {
                    e.takeDamage(200);
                    this.takeDamage(50);
                    break;
                }
            }
        }
        for(Pickup p : game.getPickups()) {
            if(this.hitboxesIntersecting(p)) {
                p.setConsumed();
                if(p instanceof HealthPickup) {
                    hp += 50;
                    if (hp >= maxHP) {
                        hp = maxHP;
                    }
                }
            }
        }
    }
    
    public void takeDamage(double damage) {
        if(!invincible) {
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
    
    public void update(double timePassed, ArizonaAdventure game) {
        takeUserInput(timePassed, game);
        checkForCollisions(game);
        shoot(timePassed, game);
        if(invincible) {
            invincibleTime.updateTimer(timePassed);
            if(invincibleTime.tryToFire()) {
                invincible = false;
            }
        }
    }
    
}
