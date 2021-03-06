/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class LobProjectile extends Projectile {
    private static double gravity = 350;
    
    //Projectile(double x, double y, ArrayList<Vector2D> hitbox, Vector2D velocity, double damage, double size, boolean isFriendly)
    public LobProjectile(double x, double y, Vector2D velocity, double width, double height, double damage, double size, boolean isFriendly) {
        super(x, y, generateSquareHitbox(width, height), velocity, damage, size, isFriendly);
    }
    
    public static double getGravity() {
        return gravity;
    }
    
    public boolean entityOutOfBounds(ArizonaAdventure game) {
        return y - sizeRadius > game.getGameHeight() 
                || x + sizeRadius < 0 || x - sizeRadius > game.getGameWidth();
    }
    
    protected void move(double timePassed) {
        velocity.y += gravity * timePassed;
        orientation = velocity.getAngle();
        snapToOrientation();
        super.move(timePassed);
    }
}
