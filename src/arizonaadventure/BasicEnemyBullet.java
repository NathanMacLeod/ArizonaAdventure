/*
 * File added by Nathan MacLeod 2020
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
    
    public BasicEnemyBullet(double x, double y, double width, double height, Vector2D velocity, double damage, Sprite sprite) {
        super(x, y, generateSquareHitbox(width, height), velocity, damage, width, false);
        double orientation = velocity.getAngle();
        moveEntity(0, 0, orientation);
        this.sprite = sprite;
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
}
