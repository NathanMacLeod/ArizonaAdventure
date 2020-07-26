/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 *
 * @author macle
 */
public class LaserEnemy extends KillableEntity {
    private Sprite sprite;
    
    private static double width = 65;
    private double speed;
    private boolean reachedX = false;
    private boolean charging = false;
    private boolean firing = false;
    private final double finalXPor = 5.0/6;
    private double finalXCord = -1;
    private double turnRate = 0.5;
    private double fireTolCos;
    private double laserWidth;
    private double damage = 40;
    private CooldownTimer chargeTime;
    private CooldownTimer fireTime;
    private CooldownTimer cooldown;
    
    public LaserEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(width, 25), 350, 70);
        orientation = Math.PI;
        speed = 75;
        chargeTime = new CooldownTimer(1);
        fireTime = new CooldownTimer(1);
        cooldown = new CooldownTimer(0.2);
        fireTolCos = Math.cos(Math.PI/180);
        laserWidth = 25;
        if(sprite == null) {
            sprite = new Sprite("redbulllite.png", (int)(width * 1.5));
        }
        sprite = new Sprite(sprite);
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
        else if(charging) {
            chargeTime.updateTimer(timePassed);
            if(chargeTime.tryToFire()) {
                fireTime.resetTimer();
                charging = false;
                firing = true;
            }
        }
        else if(firing) {
            fireTime.updateTimer(timePassed);
            if(fireTime.tryToFire()) {
                firing = false;
                cooldown.resetTimer();
            }
            else {
                Player player = game.getPlayer();
                Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
                Vector2D relPos = new Vector2D(player.getX() - x, player.getY() - y);
                double dot = relPos.dot(orientationVector);
                if(dot > 0) {
                    double dist = Math.sqrt(relPos.sub(orientationVector.scale(dot)).getMagnitudeSquared());
                    if(dist < laserWidth/2.0) {
                        player.takeDamage(damage);
                    }
                }
            }
        }
        else  {
            cooldown.updateTimer(timePassed);
            Player player = game.getPlayer();
            Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
            Vector2D relPosUnit = new Vector2D(player.getX() - x, player.getY() - y).getUnitVector();

            double cosAng = relPosUnit.dot(orientationVector);
            if (cooldown.readyToFire() && cosAng > fireTolCos) { //lost lock
                charging = true;
                chargeTime.resetTimer();
                return;
            }

            double turn = turnRate * timePassed;

            if(orientationVector.clockwiseOrientation(relPosUnit)) {
                turn *= -1;
            }
            moveEntity(0, 0, turn);
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        
        sprite.draw(g, x, y, orientation);
        
        if(firing || charging) {
            double dist = 2000;
            Vector2D dir = new Vector2D(Math.cos(orientation), Math.sin(orientation));
            Vector2D n = dir.getNorm();

            double drawWidth = (firing)? laserWidth/2.0 : laserWidth/4.0;
            Vector2D i = dir.scale(dist);
            Vector2D k = dir.scale(width/2.0);
            Vector2D j = n.scale(drawWidth);

            int[] xP = new int[4];
            int[] yP = new int[4];

            Vector2D p1 = i.sub(j);
            Vector2D p2 = i.add(j);
            Vector2D p3 = k.add(j);
            Vector2D p4 = k.sub(j);

            xP[0] = (int) (x + p1.x);
            yP[0] = (int) (y + p1.y);

            xP[1] = (int) (x + p2.x);
            yP[1] = (int) (y + p2.y);

            xP[2] = (int) (x + p3.x);
            yP[2] = (int) (y + p3.y);

            xP[3] = (int) (x + p4.x);
            yP[3] = (int) (y + p4.y);

            if(firing) {
                g.setColor(Color.red);
            }
            else {
                g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
            }
            
            g.fillPolygon(xP, yP, 4);
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveAndShoot(timePassed, game);
    }
}
