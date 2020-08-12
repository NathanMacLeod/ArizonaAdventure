/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author macle
 */
public class PlayerRocket extends Projectile {
    private static final String igniteSound = "playermissile.wav";
    private static final String boom = "missileexplode.wav";
    private static Sprite sprite = null;
    private static Sprite preignite = null;
    
    private KillableEntity target;
    private double maxSpeed = 800;
    private double liveTime;
    private double accelerationTime = 1;
    private double deadTime = 0.33;
    private double currEjectVelocity;
    private double ejectVelocity = 100;
    private double ejectDecellTime = 1.5;
    private double currVelocity;
    private double lockAng = Math.PI/6;
    private double cosLockAng;
    private double turnRate = 1.5;
    private int sfxID = -1;
    
    public PlayerRocket(double x, double y, double damage) {
        super(x, y, generateSquareHitbox(30, 7), new Vector2D(0, 0), damage, 40, true);
        target = null;
        currVelocity = 0;
        
        liveTime = 0;
        cosLockAng = Math.cos(lockAng);
        currEjectVelocity = ejectVelocity;
        if(sprite == null) {
            sprite = new Sprite("rocket.png", (int)30 * 3);
            preignite = new Sprite("rocketpreignite.png", (int)30 * 3);
        }
    }
   
    private void accelerate(double timePassed) {
        currEjectVelocity -= timePassed * ejectVelocity / ejectDecellTime;
        if (currEjectVelocity < 0) {
            currEjectVelocity = 0;
        }
        if(liveTime > deadTime) {
            currVelocity += timePassed * maxSpeed / accelerationTime;
            if (currVelocity > maxSpeed) {
                currVelocity = maxSpeed;
            }
        }
    }
    
    protected void addExplosion(ArizonaAdventure game) {
        super.addExplosion(game);
         SoundManager.play(boom);
    }
    
    public void deleteActions() {
        if(sfxID != -1) {
            SoundManager.terminateSFX(sfxID);
        }
    }
    
    private KillableEntity findTarget(ArizonaAdventure game) {
        double closestDist = -1;
        Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
        
        KillableEntity target = null;
        
        for(KillableEntity enemy : game.getEnemies()) {
            Vector2D relPos = new Vector2D(enemy.getX() - x, enemy.getY() - y);
            double dist = relPos.getMagnitudeSquared();
            
            if(closestDist == -1 || dist < closestDist) {
                Vector2D relPosUnit = relPos.getUnitVector();
                if(relPosUnit.dot(orientationVector) > cosLockAng) {
                    closestDist = dist;
                    target = enemy;
                }
            }
        }
        
        return target;
    }
    
    private double reactToLockedTarget(double timePassed) {
        Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
        Vector2D relPosUnit = new Vector2D(target.getX() - x, target.getY() - y).getUnitVector();
        
        double cosAng = relPosUnit.dot(orientationVector);
        if (cosAng < cosLockAng || target.isDead()) { //lost lock
            target = null;
            return 0;
        }
        
        double turn = turnRate * timePassed * currVelocity / maxSpeed;
        
        if(orientationVector.clockwiseOrientation(relPosUnit)) {
            return -turn;
        }
        else {
            return turn;
        }
    }
    
    public void move(double timePassed) {
        double dTheta = 0;
        if(target != null) {
            dTheta = reactToLockedTarget(timePassed);
        }
        
        double vx = Math.cos(orientation) * currVelocity;
        double vy = Math.sin(orientation) * currVelocity + currEjectVelocity;
        
        moveEntity(vx * timePassed, vy * timePassed, dTheta);
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        if(liveTime < deadTime) {
            preignite.draw(g, x, y, orientation);
        }
        else {
            sprite.draw(g, x, y, orientation);
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        liveTime += timePassed;
        if(liveTime > deadTime && liveTime - deadTime < timePassed) {
            sfxID =  SoundManager.play(igniteSound);
        } 
        if(currVelocity < maxSpeed && currEjectVelocity > 0) {
            accelerate(timePassed);
        }
        if(liveTime > deadTime && target == null) {
            target = findTarget(game);
        }
        move(timePassed);
        checkForCollision(game);
    }
}
