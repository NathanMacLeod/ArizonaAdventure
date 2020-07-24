/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class Player extends KillableEntity {   
    private static Sprite sprite = null;
    
    static double width = 90;
    static double height = 15;
    
    double hVel;
    double vVel;
    double bulletVel;
    CooldownTimer primaryFire;
    CooldownTimer rocketFire;
    
    public Player(double x, double y) {
        super(x, y, generateSquareHitbox(width, height), 100);
        hVel = 500;
        vVel = 500;
        bulletVel = 600;
        primaryFire = new CooldownTimer(3);
        rocketFire = new CooldownTimer(0.33);
        if(sprite == null) {
            sprite = new Sprite("arizona15.png", (int) (width * 1.25));
        }
    }
    
    private void takeUserInput(double timePassed, ArizonaAdventure game) {
        double hVel = 250 * timePassed;
        double vVel = 200 * timePassed;

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
        this.moveEntity(xVel, yVel, 0);
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        primaryFire.updateTimer(timePassed);
        if(primaryFire.tryToFire()) {
            game.addNewProjectile(new PrimaryFireBullet(x + width/2.0, y, 20));
        }
        /*rocketFire.updateTimer(timePassed);
        if(rocketFire.tryToFire()) {
            game.addNewProjectile(new PlayerRocket(x, y, 80));
        }*/
    }
    
    private void checkForCollisions(ArizonaAdventure game) {
        for(KillableEntity e : game.getEnemies()) {
            if(this.hitboxesIntersecting(e)) {
                e.takeDamage(200);
                this.takeDamage(50);
            }
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        takeUserInput(timePassed, game);
        checkForCollisions(game);
        shoot(timePassed, game);
    }
    
}
