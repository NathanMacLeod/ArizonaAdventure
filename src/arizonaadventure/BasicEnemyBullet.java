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
    private static Sprite masterSprite = null;
    
    private Sprite sprite;
    
    public BasicEnemyBullet(double x, double y, Vector2D velocity) {
        super(x, y, generateSquareHitbox(14, 12), velocity, 33, false);
        double orientation = velocity.getAngle();
        moveEntity(0, 0, orientation);
        if(masterSprite == null) {
            masterSprite = new Sprite("spritebullet.png", (int)(16 * 2));
        }
        sprite = new Sprite(masterSprite);
    }
    
    public void draw(Graphics2D g) {
        super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
}
