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
public class BasicEnemyBullet extends Projectile {
    private Sprite sprite;
    
    public BasicEnemyBullet(double x, double y, Vector2D velocity, Sprite sprite) {
        super(x, y, generateSquareHitbox(14, 12), velocity, 20, 20, false);
        double orientation = velocity.getAngle();
        moveEntity(0, 0, orientation);
        this.sprite = sprite;
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
}
