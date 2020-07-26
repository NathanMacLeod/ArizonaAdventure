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
public class EnemyRocket extends Projectile {
     private static Sprite sprite = null;
    
    private boolean lostLock;
    private double speed;
    private double lockAng;
    private double cosLockAng;
    private double turnRate;
    
    public EnemyRocket(double x, double y, double orientation, double damage) {
        super(x, y, generateSquareHitbox(30, 8), new Vector2D(0, 0), damage, 40, false);
        speed = 300;
        turnRate = 0.75;
        lockAng = Math.PI/9;
        this.orientation = orientation;
        
        if(sprite == null) {
            sprite = new Sprite("pepsirocket.png", (int)(25 * 4));
        }
    }
    
    private double reactToLockedTarget(double timePassed, ArizonaAdventure game) {
        Player player = game.getPlayer();
        Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
        Vector2D relPosUnit = new Vector2D(player.getX() - x, player.getY() - y).getUnitVector();
        
        double cosAng = relPosUnit.dot(orientationVector);
        if (cosAng < cosLockAng) { //lost lock
            lostLock = true;
            return 0;
        }
        
        double turn = turnRate * timePassed;
        
        if(orientationVector.clockwiseOrientation(relPosUnit)) {
            return -turn;
        }
        else {
            return turn;
        }
    }
    
    private void move(double timePassed, ArizonaAdventure game) {
        double dTheta = 0;
        if(!lostLock) {
            dTheta = reactToLockedTarget(timePassed, game);
        }
        
        double vx = Math.cos(orientation) * speed;
        double vy = Math.sin(orientation) * speed;
        
        moveEntity(vx * timePassed, vy * timePassed, dTheta);
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        move(timePassed, game);
        checkForCollision(game);
    }
    
}
