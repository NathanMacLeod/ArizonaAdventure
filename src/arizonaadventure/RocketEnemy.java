/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class RocketEnemy extends KillableEntity {
    private static final String rocketLaunch = "rocketLaunch.wav";
    private static Sprite sprite = new Sprite("pepsicopter.png", (int)(65 * 2));
    
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
        super(x, y, generateSquareHitbox(65, 50), 420, 70);
        speed = 115;
        rocketFire = new CooldownTimer(0.5);
        rocketCount = 0;
        timeTraveled = 0;
        
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
            double dy = timePassed * timeTraveled * accelFactor * (1 -timeTraveled / travelTime); 
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
                    SoundManager.play(rocketLaunch);
                    Player player = game.getPlayer();
                    Vector2D dir = new Vector2D(player.x - x, player.y - y);
                    game.addNewProjectile(new EnemyRocket(x, y, dir.getAngle(), 25));
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
                accelFactor = dy * 6.0 / (travelTime * travelTime);
                travelingToY = true;
            }
        }
    }
    
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveAndShoot(timePassed, game);
    }
}
