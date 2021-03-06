/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import static arizonaadventure.MoveingEntity.generateSquareHitbox;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class AuraEnemy extends KillableEntity {
    private static final String zap = "zap.wav";
    private static Sprite sprite = new Sprite("electricgatorade.png", (int)(70 * 1.5));
    private static Sprite bulletSprite = new Sprite("thunderbolt.png", 60);
    
    private int nProj = 6;
    private double projVel = 250;
    private double cos;
    private double sin;
    
    private double speed;
    private CooldownTimer fire;
    
    public AuraEnemy(double x, double y) {
        super(x, y, generateSquareHitbox(70, 45), 250, 55);
        speed = 75;
        fire = new CooldownTimer(0.65);
        double dTheta = 2 * Math.PI / nProj;
        cos = Math.cos(dTheta);
        sin = Math.sin(dTheta);
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        fire.updateTimer(timePassed);
        if(fire.tryToFire() && !entityOutOfBounds(game)) {
            SoundManager.play(zap);
            Vector2D curr = new Vector2D(0, -projVel);
            for(int i = 0; i < nProj; i++) {
                game.addNewProjectile(new BasicEnemyBullet(x, y, new Vector2D(curr.x, curr.y), bulletSprite));
                double newX = curr.x * cos - curr.y * sin;
                double newY = curr.x * sin + curr.y * cos;
                curr.x = newX;
                curr.y = newY;
            }
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
