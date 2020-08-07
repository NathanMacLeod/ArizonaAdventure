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
public class SubBoss extends KillableEntity implements Boss {
    private static final double width = 500;
    private static final double height = 120;
    private static final double startingHP = 4500;
    
    private static Sprite bulletSprite;
    private static Sprite flak;
    private static Sprite body;
    private static Sprite turretBack;
    private static Sprite turretGun;
    private static Sprite turretFront;
    private static Sprite shrapnel;
    private static Sprite missile;
    private static Sprite nuke;
    
    
    private enum State {
        Submerged, Diving, Surfacing, Surfaced;
    }
    
    private int nExplosions;
    private CooldownTimer explosionTimer;
    private double life = 0;
    private final double shakeMagnitude = 10;
    private final double shakePeriod = 3;
    
    private State state;
    private final double surfaceY;
    private final double submergeY;
    private final double speed = 40;
    
    private CooldownTimer gunFire;
    private CooldownTimer surfaceTime;
    private CooldownTimer salvoTimer;
    private CooldownTimer missileRate;
       
    boolean[] usedAttacks;
    private boolean launchFire;
    private final int salvoSize = 9;
    private int missileCount = 0;
    private final int nSalvos = 3;
    private int salvoCount = 0;
    
    private int salvoDir;
    
    private double playerAng = Math.PI;
    
    private final double missileMaxVelocity = 750;
    private final double bigMissileMaxVelocity = 750;
    private static final double missileSize = 100;
    private static final double bigMissileSize = 160;
    private final double missileX;
    private final double turretRX;
    private final double turretRY;
    private final double turretR = 130;
    
    public SubBoss(ArizonaAdventure game) {
        super(0, 0, generateSquareHitbox(width, height), startingHP, 300);
        surfaceY = game.getGameHeight() * 0.93;
        submergeY = game.getHeight() + height * 1.5;
        
        turretRX = width / 20;
        missileX = width/4.0;
        turretRY = -height / 2.3;
        
        
        
        usedAttacks = new boolean[4];
        moveEntity(690, submergeY, 0);
        state = State.Surfacing;
        surfaceTime = new CooldownTimer(1.0/20);
        gunFire = new CooldownTimer(0.74);
        salvoTimer = new CooldownTimer(2.0/3);
        missileRate = new CooldownTimer(4);
        nExplosions = 50;
        explosionTimer = new CooldownTimer(14);
        game.addNewEnemy(this);
    }
    
    public static void loadSprites() {
        bulletSprite = new Sprite("tropicana.png", (int)(24 * 2));
        shrapnel = new Sprite("orange.png", 20);
        flak = new Sprite("flak.png", 65);
        body = new Sprite("tropicanasub.png", (int) (width * 1.2));
        turretBack = new Sprite("orangeback.png", (int) (width * 0.55));
        turretGun = new Sprite("orangegun.png", (int) (width * 0.55));
        turretFront = new Sprite("orangestand.png", (int) (width * 0.55));
        missile = new Sprite("orangemissile.png", (int) (missileSize * 1.5));
        nuke = new Sprite("bigone.png", (int) (bigMissileSize * 2));
    }
    
    private class ExplodingShell extends Projectile {
        private double fuse;
        private int nShrapnel = 7;
        private double shrapnelVel = 250;
        private double cos;
        private double sin;
        
        public ExplodingShell(double x, double y, Vector2D velocity, double fuse) {
            super(x, y, generateSquareHitbox(26, 14), velocity, 33, 80, false);
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
            game.addExplosion(new ExplosionEffect(flak, x, y, 60, 0.15));
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
    
    private class Missile extends Projectile {
        private Vector2D dir;
        private double acceleration = 350;
        private double currVelocity;
        private double maxVelocity;
        private int nShrapnel = 30;
        private boolean bigOne;
        private boolean armed;
        
        private double distTraveled;
        private double targetDist;
        
        private double cos;
        private double sin;
        
        public Missile(double x, double y, double targetX, double targetY, double vel, boolean bigOne, boolean armed) {
            super(x, y, generateSquareHitbox(missileSize, missileSize/5.0), new Vector2D(0, 0), 33, (bigOne)? 350 : 120, false);
            this.bigOne = bigOne;
            this.armed = armed;
            Vector2D traj = new Vector2D(targetX - x, targetY - y);
            targetDist = Math.sqrt(traj.getMagnitudeSquared());
            dir = traj.scale(1.0/targetDist);
            maxVelocity = missileMaxVelocity;
            
            if(bigOne) {
                double dTheta = 2 * Math.PI / nShrapnel;
                cos = Math.cos(dTheta);
                sin = Math.sin(dTheta);
                acceleration /= 2;
                maxVelocity = bigMissileMaxVelocity;
            }
            
            distTraveled = 0;
            currVelocity = vel;
            double orientation = dir.getAngle();
            moveEntity(0, 0, orientation);
        }
        
        private void detonate(ArizonaAdventure game) {
            expired = true;
            Vector2D curr = new Vector2D(0, -270);
            
            for(int i = 0; i < nShrapnel; i++) {
                game.addNewProjectile(new ExplodingShell(x, y, new Vector2D(curr.x, curr.y), Math.random() * 2));
                double newX = curr.x * cos - curr.y * sin;
                double newY = curr.x * sin + curr.y * cos;
                curr.x = newX;
                curr.y = newY;
            }
            game.addEffect(new ColorFlash(0.1, 2.0, (int) game.getGameWidth(), (int) game.getGameHeight(), 1.0f, 1.0f, 1.0f));
            game.addExplosion(new ExplosionEffect(x, y, 250, 0.25));
        }
        
        public boolean entityOutOfBounds(ArizonaAdventure game) {
            return distTraveled >= targetDist && super.entityOutOfBounds(game);
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            if(currVelocity < maxVelocity) {
                currVelocity += acceleration * timePassed;
            }
            distTraveled += currVelocity * timePassed;
            if(bigOne && armed && distTraveled > targetDist) {
                detonate(game);
            }
            Vector2D move = dir.scale(currVelocity * timePassed);
            moveEntity(move.x, move.y, 0);
            checkForCollision(game);
        }
        
        public void draw(Graphics2D g) {
            //super.draw(g);
            if(bigOne) {
                nuke.draw(g, x, y, orientation);
            }
            else {
                missile.draw(g, x, y, orientation);
            }
        }
    }
    
    public double getHealthPercentage() {
        return hp/startingHP;
    }
    
    public void shoot(double timePassed, ArizonaAdventure game) {
        Player player = game.getPlayer();
        double tx = x + turretRX;
        double ty = y + turretRY;
        playerAng = new Vector2D(player.x - tx, player.y - ty).getAngle();
        
        gunFire.updateTimer(timePassed);
        if(gunFire.tryToFire()) {
            
            double bulletSpeed = 270;
            Vector2D toTarget = new Vector2D(player.x - tx, player.y - ty);
            double len = Math.sqrt(toTarget.getMagnitudeSquared());
            double fuse = (len - turretR)/bulletSpeed;
            Vector2D vel = toTarget.scale(bulletSpeed/len);
            double bX = turretR * Math.cos(playerAng);
            double bY = turretR * Math.sin(playerAng);
            game.addNewProjectile(new ExplodingShell(tx + bX, ty + bY, vel, fuse));
        }
    }
    
    private void initSubmerged() {
        salvoCount = 3;
        if(hp/startingHP < 0.5) {
            salvoCount++;
        }
        for(int i = 0; i < usedAttacks.length; i++) {
            usedAttacks[i] = false;
        }
        launchFire = true;
    }
    
    private boolean doMissileAttacks(double timePassed, ArizonaAdventure game) {
        if(salvoCount == 0 && missileCount == 0) {
                if(launchFire) {
                    launchFire = false;
                    salvoCount = 3;
                    if(hp/startingHP < 0.5) {
                        salvoCount++;
                    }
                }
                else {
                    return true;
                }
            }
            else {
                if(missileCount == 0) {
                    salvoTimer.updateTimer(timePassed);
                    if(salvoTimer.tryToFire()) {
                        salvoCount--;
                        
                        missileCount = salvoSize;
                        
                        if(!launchFire) {
                            salvoDir = (int) (Math.random() * 4);
                            while(usedAttacks[salvoDir]) {
                                salvoDir = (int) (Math.random() * 4);
                            }
                            usedAttacks[salvoDir] = true;
                        }
                        
                        
                    } 
                }
                else {
                    missileRate.updateTimer(timePassed);
                    if(missileRate.tryToFire()) {
                        missileCount--;
                        
                        double spawnX = 0;
                        double spawnY = 0;
                        double targetX = 0;
                        double targetY = 0;
                        double spawnVel = 0;
                        
                        boolean fireBigOne = salvoCount == 0 && (hp/startingHP < 0.5);
                        if(fireBigOne && missileCount != salvoSize - 1) {
                            return false;
                        }
                        
                        if(launchFire) {
                            double var = 100;
                            spawnVel = 0;
                            spawnX = x + missileX;
                            spawnY = y;
                            targetX = x + missileX + var * (Math.random() - 0.5);
                            targetY = -bigMissileSize; 
                        }
                        else if(fireBigOne) {
                            spawnX = Math.random() * game.getGameWidth() * 0.25;
                            if(Math.random() > 0.5) {
                                spawnX = game.getGameWidth() - spawnX;
                            }
                            spawnY = -bigMissileSize;
                            targetX = spawnX;
                            targetY = game.getGameHeight() * (0.33 + 0.66 * Math.random());
                        }
                        else {
                            Player p = game.getPlayer();
                            spawnVel = missileMaxVelocity;
                            switch(salvoDir) {
                                case 0:
                                    spawnX = -missileSize;
                                    spawnY = p.y + 300 * (Math.random() - 0.5);//Math.random() * game.getGameHeight();
                                    targetX = 1;
                                    targetY = spawnY;
                                    break;
                                case 1:
                                    spawnX = game.getGameWidth() + missileSize;
                                    spawnY = p.y + 300 * (Math.random() - 0.5);//Math.random() * game.getGameHeight();
                                    targetX = 1;
                                    targetY = spawnY;
                                    break;
                                case 2:
                                    spawnX = p.x + 300 * (Math.random() - 0.5);
                                    spawnY = 0 - missileSize;
                                    targetX = spawnX;
                                    targetY = 1;
                                    break;
                                case 3:
                                    spawnX = Math.random() * game.getGameWidth();
                                    spawnY = 0 - missileSize;
                                    targetX = p.x;
                                    targetY = p.y;
                                    break;
                            }
                        }
                        
                        game.addNewProjectile(new Missile(spawnX, spawnY, targetX, targetY, spawnVel, fireBigOne, !launchFire));
                    }
                }
            }
        return false;
        }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        double tx = x + turretRX;
        double ty = y + turretRY;
        body.draw(g, x, y, 0);
        turretBack.draw(g, tx, ty, 0);
        turretGun.draw(g, tx, ty, playerAng);
        turretFront.draw(g, tx, ty, 0);
    }
    
    private boolean exploding() {
        return super.isDead();
    }
        
    public boolean isDead() {
        return exploding() && nExplosions <= 0;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        
        if(exploding() && nExplosions > 0) {
            explosionTimer.updateTimer(timePassed);
            if(explosionTimer.tryToFire()) {
                double xC = x - width/2.0 + Math.random() * width;
                double yC = y - height/2.0 + Math.random() * height;
                game.addExplosion(new ExplosionEffect(xC, yC, (int) (100 + Math.random() * 60), 0.35));
                nExplosions--;
            }
        }
        else {
            switch(state) {
                case Surfacing:
                    double move = -speed * timePassed;
                    if(y + move < surfaceY) {
                        surfaceTime.resetTimer();
                        state = State.Surfaced;
                        move = surfaceY - y;
                    }
                    moveEntity(0, move, 0);
                    break;
                case Diving:
                    move = speed * timePassed;
                    if(y + move > submergeY) {
                        state = State.Submerged;
                        move = submergeY - y;
                        initSubmerged();
                    }
                    moveEntity(0, move, 0);
                    break;
                case Surfaced:
                    shoot(timePassed, game);

                    double jostleVel = shakeMagnitude * Math.PI / shakePeriod * Math.cos(life * Math.PI / (shakePeriod));
                    move = -jostleVel * timePassed;
                    moveEntity(0, move, 0);

                    surfaceTime.updateTimer(timePassed);
                    if(surfaceTime.tryToFire()) {
                        state = State.Diving;
                    }
                    break;
                case Submerged:
                    boolean missileAttacksDone = doMissileAttacks(timePassed, game);
                    if(missileAttacksDone) {
                        this.state = State.Surfacing;
                    }
                    break;
            }
        }
    }
    
    public boolean bossDefeated() {
        return isDead();
    }
}
