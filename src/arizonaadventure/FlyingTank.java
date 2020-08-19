/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import static arizonaadventure.MoveingEntity.generateSquareHitbox;
import java.awt.Graphics2D;

/**
 *
 * @author macle
 */
public class FlyingTank extends KillableEntity {

    private static double finalX = 780;
    private static int width = 130;
    private static int height = 130;
    private final double shakeMagnitude = 20;
    private final double shakePeriod = 3;
    
    private static final String boom = "cannonboom.wav";
    private static Sprite sprite = new Sprite("laystank.png", width * 2);
    private static Sprite bulletSprite = new Sprite("bullet.png", 47);
    private static Sprite gun = new Sprite("laystankbarrel.png", width * 2);
    private static Sprite shrapnel = new Sprite("chip.png", 24);
    
    private double life = 0;
    private double gunX = -width/4.0;
    private double gunY = height/6.0;
    private double gunAng;
    private double gunR = width/1.3;
    private double speed;
    private CooldownTimer fire;
    
    public FlyingTank(double x, double y) {
        super(x, y, generateSquareHitbox(width, height), 2700, 150);
        speed = 75;
        fire = new CooldownTimer(0.36);
        gunAng = Math.PI;
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        fire.updateTimer(timePassed);
        Player player = game.getPlayer();
        gunAng = new Vector2D(player.x - x, player.y - y).getAngle();
        if(fire.tryToFire() && !entityOutOfBounds(game)) {
            SoundManager.play(boom);
            double dist =  Math.sqrt(new Vector2D(player.x - x, player.y - y).getMagnitudeSquared());
            Vector2D velocity = new Vector2D(player.x - x, player.y - y).scale(250/dist);
            game.addNewProjectile(new ExplodingShell(x + gunX + Math.cos(gunAng) * gunR, y + gunY + Math.sin(gunAng) * gunR, velocity, (dist - gunR)/250));
        }
    }
    
    private class ExplodingShell extends Projectile {
        private double fuse;
        private int nShrapnel = 6;
        private double shrapnelVel = 200;
        private double cos;
        private double sin;
        
        public ExplodingShell(double x, double y, Vector2D velocity, double fuse) {
            super(x, y, generateSquareHitbox(28, 10), velocity, 33, 80, false);
            double orientation = velocity.getAngle();
            moveEntity(0, 0, orientation);
            this.fuse = fuse;
            double dTheta = 2 * Math.PI / nShrapnel;
            cos = Math.cos(dTheta);
            sin = Math.sin(dTheta);
        }
        
        private void detonate(ArizonaAdventure game) {
            expired = true;
            Vector2D curr = new Vector2D(0, -shrapnelVel);
            for(int i = 0; i < nShrapnel; i++) {
                game.addNewProjectile(new BasicEnemyBullet(x, y, new Vector2D(curr.x, curr.y), shrapnel));
                double newX = curr.x * cos - curr.y * sin;
                double newY = curr.x * sin + curr.y * cos;
                curr.x = newX;
                curr.y = newY;
            }
            game.addExplosion(new ExplosionEffect(x, y, 60, 0.15));
            SoundManager.play(explosion);
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            fuse -= timePassed;
            if(fuse <= 0) {
                detonate(game);
            }
            else {
                super.update(timePassed, game);
            }
        }
        
        public void draw(Graphics2D g) {
            //super.draw(g);
            bulletSprite.draw(g, x, y, orientation);
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        gun.draw(g, x + gunX, y + gunY, gunAng);
        sprite.draw(g, x, y, 0);
    }
    
    private void move(double timePassed) {
        if(x > finalX) {
            moveEntity(-speed * timePassed, 0, 0);
        } 
        
        double jostleVel = shakeMagnitude * Math.PI / shakePeriod * Math.cos(life * Math.PI / (shakePeriod));
        moveEntity(0, -jostleVel * timePassed, 0);
        
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        move(timePassed);
        shoot(timePassed, game);
    }
}
