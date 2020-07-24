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
public class PrimaryFireBullet extends Projectile {
    private static Sprite masterSprite = null;
    
    private Sprite sprite;
    
    public PrimaryFireBullet(double x, double y, double damage) {
        super(x, y, generateSquareHitbox(25, 10), new Vector2D(600, 0), damage, true);
        if(masterSprite == null) {
            masterSprite = new Sprite("playerbullet.png", (int)25);
        }
        sprite = new Sprite(masterSprite);
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
}
