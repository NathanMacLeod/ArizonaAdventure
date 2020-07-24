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
public class FodderEnemy extends KillableEntity {
    private static Sprite masterSprite = null;
    
    private Sprite sprite;
    
    private double speed;
    
    public FodderEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(60, 30), 10);
        speed = 225;
        if(masterSprite == null) {
            masterSprite = new Sprite("cokeplane.png", (int)(60 * 1.1));
        }
        sprite = new Sprite(masterSprite);
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
    }
    
}
