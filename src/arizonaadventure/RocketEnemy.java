/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class RocketEnemy extends KillableEntity {
    private static Sprite sprite = null;
    
    private double speed;
    private boolean reachedX = false;
    private boolean travelingToY = false;
    private final double travelTime = 1;
    private double timeTraveled;
    private final double finalXPor = 3.0/4;
    private double finalXCord = -1;
    private final int rocketsToFire = 2;
    private int rocketCount;
    private CooldownTimer rocketFire;
    private double accelFactor;
    
    public RocketEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(65, 50), 500);
        speed = 75;
        rocketFire = new CooldownTimer(0.75);
        rocketCount = 0;
        timeTraveled = 0;
        
        if(sprite == null) {
            sprite = new Sprite("pepsicopter.png", (int)(65 * 2));
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    private void moveAndShoot(double timePassed, ArizonaAdventure game) {
        
        if(!reachedX) {
            if(finalXCord == -1) {
                finalXCord = game.getWidth() * finalXPor;
            }
            double move = -speed * timePassed;
            if(move + x < finalXCord) {
                move = finalXCord - x;
                reachedX = true;
            }
            moveEntity(move, 0, 0);
        }
        else if(travelingToY) {
            timeTraveled += timePassed;
            double dy = timePassed * accelFactor * (-timeTraveled * timeTraveled + timeTraveled);
            if(timeTraveled >= travelTime) {
                travelingToY = false;
                timeTraveled = 0;
            }
            moveEntity(0, dy, 0);
        }
        else {
            if(rocketCount < rocketsToFire) {
                rocketFire.updateTimer(timePassed);
                if(rocketFire.tryToFire()) {
                    Player player = game.getPlayer();
                    Vector2D dir = new Vector2D(player.x - x, player.y - y);
                    game.addNewProjectile(new EnemyRocket(x, y, dir.getAngle(), 20));
                    rocketCount++;
                }
            }
            else {
                rocketCount = 0;
                rocketFire.cutTimer();
                double targetY = getRadius() + Math.random() * (game.getGameHeight() * 0.5 - getRadius());
                if(y < game.getGameHeight() * 0.5) {
                    targetY = game.getGameHeight() - targetY;
                }
                double dy = targetY - y;
                accelFactor = dy / (travelTime * travelTime / 2.0 - travelTime * travelTime * travelTime / 3.0);
                travelingToY = true;
            }
        }
    }
    
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveAndShoot(timePassed, game);
    }
}
