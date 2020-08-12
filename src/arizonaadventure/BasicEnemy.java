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
public class BasicEnemy extends KillableEntity {
    private static Sprite sprite = null;
    private static final String fireSound = "pew.wav";
    private static Sprite bulletSprite;
    
    private double speed;
    private CooldownTimer fire;
    
    public BasicEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(50, 35), 100, 55);
        speed = 75;
        fire = new CooldownTimer(0.65);
        if(sprite == null) {
            sprite = new Sprite("spritesprey.png", (int)(50 * 2));
            bulletSprite = new Sprite("spritebullet.png", (int)(16 * 2));
        }
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        fire.updateTimer(timePassed);
        if(fire.tryToFire() && x < game.getGameWidth()) {
            SoundManager.play(fireSound);
            Player player = game.getPlayer();
            Vector2D velocity = new Vector2D(player.x - x, player.y - y).getUnitVector().scale(250);
            game.addNewProjectile(new BasicEnemyBullet(x, y, velocity, bulletSprite));
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        sprite.draw(g, x, y, orientation);
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        moveEntity(-speed * timePassed, 0, 0);
        shoot(timePassed, game);
    }
}
