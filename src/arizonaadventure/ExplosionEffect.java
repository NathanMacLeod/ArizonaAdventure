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
public class ExplosionEffect implements Updatable, Drawable {
    private static Sprite masterSprite = null;
    
    private Sprite sprite;
    private double x;
    private double y;
    private double life;
    
    public ExplosionEffect(double x, double y, int size, double life) {
        this.x = x;
        this.y = y;
        if(masterSprite == null) {
            masterSprite = new Sprite("explosion.png", 512);
        }
        sprite = new Sprite(masterSprite, size);
        this.life = life;
    }
    
    public ExplosionEffect(Sprite alternate, double x, double y, int size, double life) {
        this.x = x;
        this.y = y;
        sprite = new Sprite(alternate, size);
        this.life = life;
    }
    
    public boolean pastLife() {
        return life <= 0;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
       life -= timePassed; 
    }
    
    public void draw(Graphics2D g) {
        sprite.draw(g, x, y, 0);
    }
}
