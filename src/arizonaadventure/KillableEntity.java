/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class KillableEntity extends MoveingEntity {
    protected static final String explosion = "bullethit.wav";
    protected double hp;
    private double size;
    protected boolean nonPlayerCollidable;
    
    public KillableEntity(double x, double y, ArrayList<Vector2D> hitbox, double hp, double size) {
        super(x, y, hitbox, 0);
        this.hp = hp;
        this.size = size;
        nonPlayerCollidable = false;
    }
    
    public boolean getNonPlayerCollidable() {
        return nonPlayerCollidable;
    }
    
    protected void playExplodeSound() {
        SoundManager.play(explosion);
    }
    
    public void explode(ArizonaAdventure game) {
        game.addExplosion(new ExplosionEffect(x, y, (int) (size * 1.5), 0.25));
        playExplodeSound();
    }
    
    public void takeDamage(double damage) {
        hp -= damage;
    }
    
    public void kill() {
        hp = 0;
    }
    
    public boolean isDead() {
        return hp <= 0;
    }
}
