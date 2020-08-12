/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.Color;
import java.awt.Graphics2D;
import javafx.concurrent.Worker;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class MechBoss extends KillableEntity implements Boss {
    private double life;
    private static double startingHP = 28000;
    private static int size = 550;
     private static double spawnX = -size;
    private static double width = size / 4.5;
    private static double height = size / 1.7;
    private static double flyBoxHeight = height/2.0;
    private static double strideWidth = size * 30.0 / 500;
    private static double strideHeight = size * 50.0 / 500;
    private static double stridePeriod = 0.75;
    private static double flyMagnitude = size / 6.0;
    private static double flyPeriod = 3.5;
    private static double pausePeriod = 0.5;
    private static double gunR = size * 250.0 / 500;
    private static double gunA = -Math.PI / 32;
    private static double targetX = 70;
    private static double targetXLeft = 800;
    
    private static double buddyWidth = 40;
    private static double buddyHeight = 40;
    private static double buddySpinRate = 0.25;
    private static double buddyRadius = 150;
    private static double buddyBirthTime = 2;
    private static double laserWidth = 50;
    private static double buddyBallSpeed = 215;
    private static double buddyBallRadius = 45;
    private static double buddyBallFormTime = 1.5;
    private static int maxLaserSounds = 2;
    private double buddyBallT;
    private double buddyDCos;
    private double buddyDSin;
    private int buddiesLasering;
    private Vector2D buddyBallCoord;
    private Vector2D buddyBallV;
    private ArrayList<LaserBuddy> buddyBall;
    
    private static double walkSpeed = 2 * strideWidth / (stridePeriod + pausePeriod);
    private double baseY;
    private double headX;
    private double headY;
    private double gunX;
    private double gunY;
    private double foreLegX;
    private double foreLegY;
    private double rearLegX;
    private double rearLegY;
    private double rocketX;
    private double rocketY;
    private double legRestX;
    private double legRestY;
    private double gunAng;
    private double buddyAng;
    
    private static Sprite headS;
    private static Sprite gunS;
    private static double flashTime = 0.3;
    private static Sprite gunFire;
    private static Sprite rearArmS;
    private static Sprite legS;
    private static Sprite slug;
    private static Sprite shotPellet;
    private static Sprite bomb;
    private static Sprite shrapnel;
    private static Sprite rocket;
    private static Sprite hoverHead;
    private static Sprite laserCan;
    private static Sprite laserBolt;
    private static Sprite laserStart;
    private static Sprite laserSect;
    
    private static Music chaseTheme;
    private static Music secondTheme;
    private boolean secondThemeStarted = false;
    private static final String bigBoom = "nuke.wav";
    private static final String shotgunSound = "shotgun.wav";
    private static final String stomp = "stomp.wav";
    private static final String rocketSound = "rocketlaunch.wav";
    private static final String buddyBasic = "blaster.wav";
    private static final String laser = "laser.wav";
    
    private GunState gunState;
    private BossState state;
    private BuddyPattern buddyState;
    private GunState prevGunState;
    private BuddyPattern prevBuddyState;
    
    private static int nShots = 4;
    private int shotCount;
    private CooldownTimer fireTimer;
    private CooldownTimer reloadTimer;
    private CooldownTimer slowAttack;
    private CooldownTimer buddyReleaseRate;
    private CooldownTimer buddyCooldown;
    private CooldownTimer quickBuddyFireRate;
    private CooldownTimer laserPatternRate;
    private CooldownTimer secondThemeDelay;
    private boolean canFlash = true;
    private boolean onRight;
    private boolean playStomp;
    
    //shotgun stuff
    private double shotCone = Math.PI/12;
    private double shotConeCos;
    private double shotConeSin;
    private double shotDCos;
    private double shotDSin;
    private int nShotProj = 8;
    private int buddyI;
    private int nPatternRepetitions;
    
    private boolean releaseBuddies;
    private int nBuddies;
    private ArrayList<LaserBuddy> buddies;
    
    private int nExplosions;
    private CooldownTimer explosionTimer;
    
    public MechBoss(ArizonaAdventure game) {
        super(spawnX, 420, generateSquareHitbox(width, height), startingHP, size);
        baseY = y;
        onRight = true;
        legRestX = size * 20.0 / 500;
        legRestY = size * 120.0 / 500;
        headY = -size * 60.0 / 500;
        gunX = size * 45.0 / 500;
        gunY = -size * 30.0 / 500;
        rocketX = size * 20.0 / 500;
        rocketY = -size * 130.0 / 500;
        
        nExplosions = 50;
        explosionTimer = new CooldownTimer(14);
        
        state = BossState.FirstWalkOn;
        gunState = GunState.Reload;
        prevGunState = GunState.Reload;
        prevBuddyState = BuddyPattern.Cooldown;
        fireTimer = new CooldownTimer(1);
        reloadTimer = new CooldownTimer(0.56);
        slowAttack = new CooldownTimer(0.4);
        buddyReleaseRate = new CooldownTimer(1.5);
        buddyCooldown = new CooldownTimer(1.0/8);
        quickBuddyFireRate = new CooldownTimer(2);
        laserPatternRate = new CooldownTimer(0.25);
        secondThemeDelay = new CooldownTimer(1/4.0);
        secondThemeDelay.resetTimer();
        buddies = new ArrayList();
        buddyBall = new ArrayList();
        nBuddies = 8;
        
        shotConeCos = Math.cos(-shotCone/2.0);
        shotConeSin = Math.sin(-shotCone/2.0);
        double del = shotCone / (nShotProj + 1);
        shotDCos = Math.cos(del);
        shotDSin = Math.sin(del);
        
        flip();
        game.addNewEnemy(this);
        game.addSound(chaseTheme);
        game.addSound(secondTheme);
        chaseTheme.fadeIn(3.3);
    }
    
    private enum BuddyPattern {
        Cooldown, QuickLaser, LaserPattern, BuddyBall
    }
    
    private enum GunState {
        Reload, Slug, Shotgun, Bomb, Rocket;
    }
    
    private enum BossState {
        FirstWalkOn, FirstWalkOnRight, DissapearToLeft, Walk, Fly
    }
    
    public void dissapearToLeft() {
        state = BossState.DissapearToLeft;
        chaseTheme.fadeOut(8);
    }
    
    public void appearOnRight(ArizonaAdventure game) {
        flip();
        moveEntity(game.getGameWidth() - spawnX - x, 0, 0);
        state = BossState.FirstWalkOnRight;
        chaseTheme.fadeIn(4);
    }
    
    public static double getWalkSpeed() {
        return walkSpeed;
    }
    
    public static void loadSprites() {
        headS = new Sprite("mechHead.png", size);
        gunS = new Sprite("mechGun.png", size);
        gunFire = new Sprite("mechGunFire.png", (int) (size * 1.5));
        rearArmS = new Sprite("mechRearArm.png", size);
        legS = new Sprite("mechLeg.png", size);
        slug = new Sprite("gfuelcan.png", 78);
        shotPellet = new Sprite("gfuelpacket.png", 40);
        bomb = new Sprite("gfueldoom.png", 60);
        shrapnel = new Sprite("gfuelcanred.png", 30);
        rocket = new Sprite("gfuelrocket.png", 130);
        hoverHead = new Sprite("mechheadfloating.png", (int) (size / 1.25));
        laserCan = new Sprite("ringedcan.png", (int) (buddyWidth * 1.8));
        laserBolt = new Sprite("laserbolt.png", 50);
        laserStart = new Sprite("laserStartSection.png", (int) laserWidth);
        laserSect = new Sprite("laserSection.png", (int) laserWidth);
        
        chaseTheme = new Music("sirkoto51bosstheme.wav");
        secondTheme = new Music("boss3Theme2.wav");
    }
    
    public static void unloadAssets() {
        headS = null;
        gunS = null;
        gunFire = null;
        rearArmS = null;
        legS = null;
        slug = null;
        shotPellet = null;
        bomb = null;
        shrapnel = null;
        rocket = null;
        hoverHead = null;
        laserCan = null;
        laserBolt = null;
        laserStart = null;
        laserSect = null;
        
        chaseTheme.close();
        chaseTheme = null;
        secondTheme.close();
        secondTheme = null;
    }
            
    private void flip() {
        headS.flip();
        gunS.flip();
        gunFire.flip();
        rearArmS.flip();
        legS.flip();
        shotPellet.flip();
        bomb.flip();
        shrapnel.flip();
        rocket.flip();
        
        headX *= -1;
        gunX *= -1;
        rocketX *= -1;
        legRestX *= -1;
        onRight = !onRight;
    }
    
    public double getHealthPercentage() {
        return hp/startingHP;
    }
    
    public boolean bossDefeated() {
        return isDead();
    }
    
    private class BombProjectile extends LobProjectile {
            private double fuse;
            private double cone = Math.PI / 3;
            private double coneCos;
            private double coneSin;
            private double dCos;
            private double dSin;
            private int nProj = 4;
            
            public BombProjectile(double x, double y, double vx, double vy, double fuse) {
                super(x, y, new Vector2D(vx, vy), 55, 25, 35, 150, false);
                this.fuse = fuse;
                
                coneCos = Math.cos(-cone/2.0);
                coneSin = Math.sin(-cone/2.0);
                double del = cone / (nProj + 1);
                dCos = Math.cos(del);
                dSin = Math.sin(del);
            }
            
            private void detonate(ArizonaAdventure game) {
                expired = true;
                Vector2D curr = new Vector2D(0, 350);
                double nX = curr.x * coneCos - curr.y * coneSin;
                double nY = curr.x * coneSin + curr.y * coneCos;
                curr.x = nX;
                curr.y = nY;
                for(int i = 0; i < nProj; i++) {
                    nX = curr.x * dCos - curr.y * dSin;
                    nY = curr.x * dSin + curr.y * dCos;
                    curr.x = nX;
                    curr.y = nY;
                    game.addNewProjectile(new BasicEnemyBullet(x, y, 25, 8, new Vector2D(curr.x, curr.y).scale(0.7 + 0.3 * Math.random()), 30, shrapnel));
                }
                SoundManager.play(explosion);
                game.addExplosion(new ExplosionEffect(x, y, 150, 0.25));
            }
            
            public void update(double timePassed, ArizonaAdventure game) {
                super.update(timePassed, game);
                fuse -= timePassed;
                if(fuse <= 0) {
                    detonate(game);
                }
            }
            
            public void draw(Graphics2D g) {
                super.draw(g);
                bomb.draw(g, x, y, orientation);
            }
        }
    
    private void aim(ArizonaAdventure game) {
        Player p = game.getPlayer();
        if(!onRight) {
            gunAng = new Vector2D(p.x - x - gunX, p.y - baseY - gunY).getAngle();
        }
        else {
            gunAng = (gunState == GunState.Bomb)? Math.PI * 1.0/4 : Math.PI + new Vector2D(p.x - x - gunX, p.y - baseY - gunY).getAngle();
        }
    }
    
    private void shoot(double timePassed, ArizonaAdventure game) {
        Player p = game.getPlayer();

        if(!onRight) {
            slowAttack.updateTimer(timePassed);
            if(slowAttack.tryToFire()) {
                double barrelX = x + headX + gunX + gunR * Math.cos(gunAng - gunA);
                double barrelY = y + headY + gunY + gunR * Math.sin(gunAng - gunA);
                Vector2D dir = new Vector2D(p.x - barrelX, p.y - barrelY).getUnitVector();
                Projectile proj = new BasicEnemyBullet(barrelX, barrelY, 55, 25, dir.scale(500), 40, slug);
                game.addNewProjectile(proj);
                if(!proj.entityOutOfBounds(game))
                    SoundManager.play(shotgunSound);
            }
            return;
        }
        
        fireTimer.updateTimer(timePassed);

        if(gunState == GunState.Reload) {
            reloadTimer.updateTimer(timePassed);
            if(reloadTimer.tryToFire()) {
                GunState newAttack = GunState.Reload;
                do {
                    int fireType = (int) (Math.random() * 4);
                    shotCount = nShots;
                    switch(fireType) {
                        case 0:
                            newAttack = GunState.Slug;
                            break;
                        case 1:
                            newAttack = GunState.Shotgun;
                            break;
                        case 2:
                            newAttack = GunState.Bomb;
                            break;
                        case 3:
                            newAttack = GunState.Rocket;
                            break;
                    }
                } while(newAttack == prevGunState);
                gunState = newAttack;
                prevGunState = newAttack;
            }
        }
        else {
            if(fireTimer.tryToFire()) {
                double barrelX = x + headX + gunX + gunR * Math.cos(gunAng + Math.PI + gunA);
                double barrelY = y + headY + gunY + gunR * Math.sin(gunAng + Math.PI + gunA);
                Vector2D dir = new Vector2D(p.x - barrelX, p.y - barrelY).getUnitVector();
                
                canFlash = true;
                
                switch(gunState) {
                    case Slug:
                        SoundManager.play(shotgunSound);
                        game.addNewProjectile(new BasicEnemyBullet(barrelX, barrelY, 55, 25, dir.scale(830), 40, slug));
                        break;
                    case Shotgun:
                        SoundManager.play(shotgunSound);
                        Vector2D curr = dir.scale(850);
                        double nX = curr.x * shotConeCos - curr.y * shotConeSin;
                        double nY = curr.x * shotConeSin + curr.y * shotConeCos;
                        curr.x = nX;
                        curr.y = nY;
                        for(int i = 0; i < nShotProj; i++) {
                            nX = curr.x * shotDCos - curr.y * shotDSin;
                            nY = curr.x * shotDSin + curr.y * shotDCos;
                            curr.x = nX;
                            curr.y = nY;
                            game.addNewProjectile(new BasicEnemyBullet(barrelX, barrelY, 30, 10, new Vector2D(curr.x, curr.y).scale(0.7 + 0.3 * Math.random()), 30, shotPellet));
                        }
                        break;
                    case Bomb:
                        SoundManager.play(shotgunSound);
                        double air = 120;
                        double detHeight = 200;
                        double yH = p.y - detHeight - air - barrelY;
                        if(p.y - detHeight < 0) {
                            yH = -barrelY - air;
                        }
                        if(yH > 0) {
                            yH = -air;
                            air = p.y -detHeight - yH - barrelY;
                        }
                        double xH = p.x - barrelX;
                        double g = LobProjectile.getGravity();
                        double vY = -Math.sqrt(-2 * g * yH);
                        double t = Math.sqrt(2 * air / g) - vY / g;
                        double vX = xH/t;
                        game.addNewProjectile(new BombProjectile(barrelX, barrelY, vX, vY, t));
                        break;
                        
                    case Rocket:
                        canFlash = false;
                        game.addNewProjectile(new EnemyRocket(x + headX + rocketX, y + headY + rocketY, -Math.PI * 3/4.0, 450, Math.PI * 2/3.0, 1.4, 35, rocket));
                        SoundManager.play(rocketSound);
                        break;
                    }
                
                shotCount--;
                if(shotCount <= 0) {
                    gunState = GunState.Reload;
                }
            }
        }
    }
    
    private void move(double timePassed, ArizonaAdventure game) {
        
        switch(state) {
            
            case DissapearToLeft:
                foreLegY = legRestY;
                rearLegY = legRestY;
                moveEntity(-walkSpeed * timePassed, 0, 0);
                break;
                
            case FirstWalkOnRight:
                foreLegY = legRestY;
                rearLegY = legRestY;
                moveEntity(-walkSpeed * timePassed, 0, 0);
                if(x < targetXLeft) {
                    state = BossState.Walk;
                    
                    //FOR TESTING
                    //startFlying(game);
                }
                break;
            
            case Walk:
            case FirstWalkOn:
                double t = life % (stridePeriod * 2 + pausePeriod * 2);
                double effectiveT ;
                if(t > stridePeriod && t < stridePeriod + pausePeriod) {
                    if(timePassed - t + stridePeriod > 0) {
                        SoundManager.play(stomp);
                    }
                    effectiveT = stridePeriod;
                }
                else if(t > stridePeriod + pausePeriod && t < stridePeriod * 2 + pausePeriod) {
                    effectiveT = t - pausePeriod;
                }
                else if(t > stridePeriod * 2 + pausePeriod) {
                    if(timePassed - t + stridePeriod * 2 + pausePeriod > 0) {
                        SoundManager.play(stomp);
                    }
                    effectiveT = stridePeriod * 2;
                }
                else {
                    effectiveT = t;
                }

                double foreT = effectiveT * Math.PI / stridePeriod;
                double rearT = Math.PI + effectiveT * Math.PI / stridePeriod;

                double foreCos = Math.cos(foreT);
                double foreSin = Math.sin(foreT);
                double rearCos = Math.cos(rearT);
                double rearSin = Math.sin(rearT);

                foreLegX = legRestX + strideWidth * foreCos;
                rearLegX = legRestX + strideWidth * rearCos;
                foreLegY = legRestY + strideHeight/2.0 * ((foreSin < 0)? 0 : foreSin);
                rearLegY = legRestY + strideHeight/2.0 * ((rearSin < 0)? 0 : rearSin);

                baseY = y - strideHeight/2.0 * Math.abs(foreSin);
                double xV = Math.abs(foreSin) * strideWidth * Math.PI/stridePeriod;

                if(state == BossState.FirstWalkOn && x < targetX) {
                    moveEntity(xV * timePassed, 0, 0);
                    if(x > targetX) {
                        state = BossState.Walk;
                    }
                }
                else {
                    moveEntity((xV - walkSpeed) * timePassed, 0, 0);
                }
                break;
            case Fly:
                double yV = flyMagnitude * 2 * Math.PI * Math.cos(2 * Math.PI * life / flyPeriod) / flyPeriod;
                moveEntity(0, yV * timePassed, 0);
                break; 
        }
    }
    
    private enum BuddyState {
        Circling, TravelToTarget, AtTarget, Return, Born
    }
    
    private void startFlying(ArizonaAdventure game) {
        game.addPickup(new HealthPickup(game.getGameWidth() + 170, game.getGameHeight()/2.0));
        state = BossState.Fly;
        life = 0;
        hp = startingHP;
        moveEntity(0, game.getGameHeight() / 2.0 - y, 0);
        updateHitbox(generateSquareHitbox(width, flyBoxHeight));
        releaseBuddies(8);
        buddyState = BuddyPattern.Cooldown;
        buddyCooldown.resetTimer();
    }
    
    private class LaserBuddy extends KillableEntity {
        
        private CooldownTimer fire;
        private BuddyState state;
        private double life;
        private double accelX;
        private double accelY;
        private double targetX;
        private double targetY;
        private double timeTraveled;
        private double travelTime;
        private boolean chargeLaser;
        private boolean fireLaser;
        private double laserTime;
        private int number;
        private int laserSFXID = -1;
        
        public LaserBuddy(int n) {
            super(MechBoss.this.x, MechBoss.this.y, generateSquareHitbox(buddyWidth, buddyHeight), 1200, 60);
            state = BuddyState.Born;
            fire = new CooldownTimer(0.3);
            nonPlayerCollidable = true;
            life = 0;
            number = n;
        }  
        
        public void accelerateToCoord(double tX, double tY, double travelTime, boolean returnTrip) {
            state = (returnTrip)? BuddyState.Return : BuddyState.TravelToTarget;
            targetX = tX;
            targetY = tY;
            this.travelTime = travelTime;
            double dY = tY - y;
            double dX = tX - x;
            accelY = dY * 6.0 / (travelTime * travelTime);
            accelX = dX * 6.0 / (travelTime * travelTime);
            timeTraveled = 0;
        }
        
        public void teleportToCoord(double tX, double tY) {
            moveEntity(tX - x, tY - y, 0);
        }
        
        private void returnToCircle(ArizonaAdventure game) {
            Vector2D coord = getBuddyOrbitCoord(number, 1.5, game);
            accelerateToCoord(coord.x, coord.y, 1.5, true);
            fireLaser = false;
            chargeLaser = false;
            if(laserSFXID != -1) {
                SoundManager.terminateSFX(laserSFXID);
            }
        }
        
        public void setOrientation(double theta) {
            moveEntity(0, 0, theta - orientation);
        }
        
        public void takeDamage(double damage) {
            super.takeDamage(damage);
            if(isDead() && laserSFXID != -1) {
                SoundManager.terminateSFX(laserSFXID);
            }
        }
        
        public void update(double timePassed, ArizonaAdventure game) {
            life += timePassed;
            Player p = game.getPlayer();
            
            if(state != BuddyState.AtTarget) {
                double newOrientation = new Vector2D(p.x - x, p.y - y).getAngle();
                moveEntity(0, 0, newOrientation - orientation);
            }
            
            switch(state) {
                case Born:
                    if(life >= buddyBirthTime) {
                        state = BuddyState.Circling;
                    }
                    break;
                case Circling:
                    if(MechBoss.this.buddyState == BuddyPattern.Cooldown) {
                        fire.updateTimer(timePassed);
                        if(fire.tryToFire()) {
                            SoundManager.play(buddyBasic);
                            Vector2D dir = new Vector2D(p.x - x, p.y - y).getUnitVector().scale(300);
                            game.addNewProjectile(new BasicEnemyBullet(x, y, dir, laserBolt));
                        }
                    }
                    break;
                case TravelToTarget:
                case Return:
                    timeTraveled += timePassed;
                    double dy = timePassed * timeTraveled * accelY * (1 -timeTraveled / travelTime); 
                    double dx = timePassed * timeTraveled * accelX * (1 -timeTraveled / travelTime); 
                    
                    if(timeTraveled >= travelTime) {
                        dx = targetX - x;
                        dy = targetY - y;
                        if(state == BuddyState.Return) {
                            state = BuddyState.Circling;
                        }
                        else {
                            laserTime = 0;
                            state = BuddyState.AtTarget;
                        }
                    }
                    
                    moveEntity(dx, dy, 0);
                    break;
                case AtTarget:
                    switch(MechBoss.this.buddyState) {
                        case QuickLaser:
                            laserTime += timePassed;
                            if(laserTime <= 0.75) {
                                chargeLaser = true;
                            }
                            else if(laserTime <= 1.5) {
                                chargeLaser = false;
                                if(!fireLaser){
                                    buddiesLasering++;
                                    laserSFXID = SoundManager.play(laser);
                                }
                                fireLaser = true;
                            }
                            else {
                                if(fireLaser && laserSFXID != -1) {
                                    buddiesLasering--;
                                    SoundManager.terminateSFX(laserSFXID);
                                    laserSFXID = -1;
                                }
                                fireLaser = false;
                                chargeLaser = false;
                                returnToCircle(game);
                            }
                            break;
                        case LaserPattern:
                            moveEntity(0, 0, Math.PI - orientation);
                            laserTime += timePassed;
                            if(laserTime <= 0.8) {
                                chargeLaser = true;
                            }
                            else if(laserTime <= 2) {
                                chargeLaser = false;
                                if(!fireLaser && buddiesLasering < maxLaserSounds) {
                                    buddiesLasering++;
                                    laserSFXID = SoundManager.play(laser);
                                }
                                fireLaser = true;
                            }
                            else {
                                if(fireLaser && laserSFXID != -1) {
                                    buddiesLasering--;
                                    SoundManager.terminateSFX(laserSFXID);
                                    laserSFXID = -1;
                                }
                                fireLaser = false;
                                chargeLaser = false;
                            }
                            break;
                        case BuddyBall:
                            laserTime += timePassed;
                            laserTime %= 3.0;
                            if(laserTime <= 1.6) {
                                if(fireLaser && laserSFXID != -1) {
                                    SoundManager.terminateSFX(laserSFXID);
                                    buddiesLasering--;
                                    laserSFXID = -1;
                                }
                                fireLaser = false;
                                chargeLaser = true;
                            }
                            else {
                                if(!fireLaser && buddiesLasering < maxLaserSounds) {
                                    buddiesLasering++;
                                    laserSFXID = SoundManager.play(laser);
                                }
                                fireLaser = true;
                                chargeLaser = false;
                            }
                            break;
                    }
                    break;
            }
            
            if(fireLaser) {
                Vector2D orientationVector = new Vector2D(Math.cos(orientation), Math.sin(orientation));
                Vector2D relPos = new Vector2D(p.getX() - x, p.getY() - y);
                double dot = relPos.dot(orientationVector);
                if(dot > 0) {
                    double dist = Math.sqrt(relPos.sub(orientationVector.scale(dot)).getMagnitudeSquared());
                    if(dist < laserWidth/2.0) {
                        p.takeDamage(35);
                    }
                }
            }
        }
        
        public void draw(Graphics2D g) {
            //super.draw(g);
            laserCan.draw(g, x, y, orientation);
            
            if(chargeLaser) {
                double dist = 2000;
                Vector2D dir = new Vector2D(Math.cos(orientation), Math.sin(orientation));
                Vector2D n = dir.getNorm();

                double drawWidth = laserWidth/6.0;
                Vector2D i = dir.scale(dist);
                Vector2D k = dir.scale(buddyWidth/2.0);
                Vector2D j = n.scale(drawWidth);

                int[] xP = new int[4];
                int[] yP = new int[4];

                Vector2D p1 = i.sub(j);
                Vector2D p2 = i.add(j);
                Vector2D p3 = k.add(j);
                Vector2D p4 = k.sub(j);

                xP[0] = (int) (x + p1.x);
                yP[0] = (int) (y + p1.y);

                xP[1] = (int) (x + p2.x);
                yP[1] = (int) (y + p2.y);

                xP[2] = (int) (x + p3.x);
                yP[2] = (int) (y + p3.y);

                xP[3] = (int) (x + p4.x);
                yP[3] = (int) (y + p4.y);

                if(fireLaser) {
                    g.setColor(Color.red);
                }
                else {
                    g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
                }

                g.fillPolygon(xP, yP, 4);
            }
            
            if(fireLaser) {
                Vector2D dir = new Vector2D(Math.cos(orientation), Math.sin(orientation));
                Vector2D n = dir.getUnitVector();
                Vector2D incr = n.scale(laserWidth);

                Vector2D curr = new Vector2D(x, y);
                curr = curr.add(n.scale(buddyWidth/2.0)).add(incr.scale(0.5));

                double toll = 1.42 * laserWidth;
                boolean first = true;
                while(curr.x > - toll && curr.x < 1000 + toll && curr.y > -toll && curr.y < 600 + toll) {
                    if(first) {
                        first = false;
                        laserStart.draw(g, curr.x, curr.y, orientation);
                    }
                    else {
                        laserSect.draw(g, curr.x, curr.y, orientation);
                    }
                    curr = curr.add(incr);
                }
            }
        }
    }
    
    private void releaseBuddies(int nBuddies) {
        buddyState = BuddyPattern.Cooldown;
        buddyCooldown.resetTimer();
        for(LaserBuddy b : buddies) {
            b.kill();
        }
        buddies.clear();
        
        buddyReleaseRate.resetTimer();
        
        releaseBuddies = true;
        this.nBuddies = nBuddies;
        double dTheta = Math.PI * 2 / nBuddies;
        buddyDCos = Math.cos(dTheta);
        buddyDSin = Math.sin(dTheta);
        buddyBall.clear();
    }
    
    private Vector2D getBuddyOrbitCoord(int buddyNum, double travelTime, ArizonaAdventure game) {
        double bAng = buddyAng + Math.PI * 2 * (buddySpinRate * travelTime + (double) buddyNum / nBuddies);
        double bY = game.getGameHeight()/2 + flyMagnitude * Math.sin(2 * Math.PI * (life + travelTime) / flyPeriod);
        return new Vector2D(x + buddyRadius * Math.cos(bAng), bY + buddyRadius * Math.sin(bAng));
    } 
    
    private void initBuddyBall(ArizonaAdventure game) {
        int ballSize = 6;
        int i = 0;
        int n = 0;
        while(i < buddies.size() && n < ballSize && n < nBuddies) {
            LaserBuddy b = buddies.get(i);
            i++;
            if(b.isDead()) {
                continue;
            }
            n++;
            buddyBall.add(b);
        }
        buddyBallT = -buddyBallFormTime;
        buddyBallCoord = new Vector2D(buddyBallRadius + Math.random() * (game.getGameWidth() - 2 * buddyBallRadius),
                                        buddyBallRadius + Math.random() * (game.getGameHeight() - 2 * buddyBallRadius));
        double dir = Math.random() * 2 * Math.PI;
        buddyBallV = new Vector2D(Math.cos(dir), Math.sin(dir)).scale(buddyBallSpeed);
        
        double ang = 0;
        for(int j = 0; j < buddyBall.size(); j++) {
            LaserBuddy b = buddyBall.get(j);
            b.accelerateToCoord(buddyBallCoord.x + Math.cos(ang) * buddyBallRadius, buddyBallCoord.y + buddyBallRadius * Math.sin(ang), buddyBallFormTime, false);
            ang += Math.PI * 2.0 / buddyBall.size();
        }
    }
    
    private void manageBuddies(double timePassed, ArizonaAdventure game) {
        
        //spin buddies around self
        buddyAng += Math.PI * 2 * buddySpinRate * timePassed;
        Vector2D buddyPos = new Vector2D(Math.cos(buddyAng), Math.sin(buddyAng)).scale(buddyRadius);
        
        for(int i = 0; i < buddies.size(); i++) {
            LaserBuddy b = buddies.get(i);
            if(!b.isDead()) {
               if(b.state == BuddyState.Circling) {
                   b.teleportToCoord(x + buddyPos.x, y + buddyPos.y);
               }
               else if(b.state == BuddyState.Born) {
                   b.teleportToCoord(x + buddyPos.x * b.life / buddyBirthTime, y + buddyPos.y * b.life / buddyBirthTime);
               }
            }
            double newX = buddyPos.x * buddyDCos - buddyPos.y * buddyDSin;
            double newY = buddyPos.x * buddyDSin + buddyPos.y * buddyDCos;
            buddyPos.x = newX;
            buddyPos.y = newY;
        }
        
        //spawn new waves of buddies
        if(releaseBuddies) {
            buddyReleaseRate.updateTimer(timePassed);
            if(buddyReleaseRate.tryToFire()) {
                LaserBuddy b = new LaserBuddy(buddies.size()); 
                game.addNewEnemy(b);
                buddies.add(b);
                if(buddies.size() == nBuddies) {
                    releaseBuddies = false;
                }
            }
        }
        else if(buddies.size() > 0) {  
            //manage attack patterns
            switch(buddyState) {
                case Cooldown:
                    buddyCooldown.updateTimer(timePassed);
                    if(buddyCooldown.tryToFire()) {
                        buddyI = 0;
                        buddiesLasering = 0;
                        
                        BuddyPattern newPattern = null;
                        do {
                            int newAttack = (int) (Math.random() * 3);
                            switch(newAttack) {
                                case 0:
                                    newPattern = BuddyPattern.QuickLaser;
                                    break;
                                case 1:
                                    nPatternRepetitions = 3;
                                    newPattern = BuddyPattern.LaserPattern;
                                    break;
                                case 2:
                                    newPattern = BuddyPattern.BuddyBall;
                                    break;
                            }
                        } while(newPattern == prevBuddyState);
                        buddyState = newPattern;
                        prevBuddyState = newPattern;
                        if(buddyState == BuddyPattern.BuddyBall) {
                            initBuddyBall(game);
                        }
                    }
                    break;
                case QuickLaser:
                    quickBuddyFireRate.updateTimer(timePassed);
                    if(quickBuddyFireRate.tryToFire()) {
                        LaserBuddy b = null;
                        while((b == null || b.isDead()) && buddyI < buddies.size()) {
                            b = buddies.get(buddyI);
                            buddyI++;
                        }
                        
                        if(b != null && !b.isDead()) {
                            double targetX = 0.6 * Math.random() * game.getGameWidth();
                            double targetY = Math.random() * game.getGameHeight();
                            b.accelerateToCoord(targetX, targetY, 1.0, false);
                        }
                        
                        if(buddyI >= buddies.size()) {
                            
                            boolean attackDone = true;
                            for(LaserBuddy buddy : buddies) {
                                if(!buddy.isDead() && buddy.state != BuddyState.Circling) {
                                    attackDone = false;
                                    break;
                                }
                            }
                            if(attackDone) {
                                buddyState = BuddyPattern.Cooldown;
                                for(LaserBuddy buddy : buddies) {
                                    buddy.state = BuddyState.Circling;
                                }
                            }
                        }
                        
                    }
                    
                    
                    break;
                case LaserPattern:
                    laserPatternRate.updateTimer(timePassed);
                    if(laserPatternRate.tryToFire()) {
                        int nBuddies = 9;
                        
                        if(nPatternRepetitions == 0) {
                            for(LaserBuddy b : buddies) {
                                if(b.state != BuddyState.Circling) {
                                    b.returnToCircle(game);
                                }
                            }
                            buddyState = BuddyPattern.Cooldown;
                        }
                        else {
                            nPatternRepetitions--;
                            int nSlots = (int) (game.getGameHeight() / laserWidth);
                            boolean[] occupied = new boolean[nSlots];
                            for(int i = 0; i < occupied.length; i++) {
                                occupied[i] = false;
                            }
                            
                            int n = 0;
                            int i = 0;
                            while(i < buddies.size() && n < nBuddies) {
                                LaserBuddy b = buddies.get(i);
                                i++;
                                if(b.isDead()) {
                                    continue;
                                }
                                n++;
                                int slot;
                                do {
                                    slot = (int) (Math.random() * nSlots);
                                } while(occupied[slot]);
                                occupied[slot] = true;
                                b.accelerateToCoord(game.getGameWidth() - 2 * buddyWidth, laserWidth * (slot + 0.5), 1.2, false);
                            }
                        }
                    }
                    break;
                    
                case BuddyBall:
                    buddyBallT += timePassed;
                    
                    if(buddyBallT >= 0) {
                        buddyBallCoord.x += timePassed * buddyBallV.x;
                        buddyBallCoord.y += timePassed * buddyBallV.y;

                        if(buddyBallCoord.x < buddyBallRadius) {
                            buddyBallCoord.x = buddyBallRadius;
                            buddyBallV.x *= -1;
                        }
                        else if(buddyBallCoord.x > game.getGameWidth() - buddyBallRadius) {
                            buddyBallCoord.x = game.getGameWidth() - buddyBallRadius;
                            buddyBallV.x *= -1;
                        }
                        if(buddyBallCoord.y < buddyBallRadius) {
                            buddyBallCoord.y = buddyBallRadius;
                            buddyBallV.y *= -1;
                        }
                        else if(buddyBallCoord.y > game.getGameHeight() - buddyBallRadius) {
                            buddyBallCoord.y = game.getGameHeight() - buddyBallRadius;
                            buddyBallV.y *= -1;
                        }
                            
                        double ang = buddyBallT / 2;
                        for(int j = 0; j < buddyBall.size(); j++) {
                            LaserBuddy b = buddyBall.get(j);
                            b.teleportToCoord(buddyBallCoord.x + buddyBallRadius * Math.cos(ang), buddyBallCoord.y + buddyBallRadius * Math.sin(ang));
                            b.setOrientation(ang);
                            ang += Math.PI * 2.0 / buddyBall.size();
                        }

                        if(buddyBallT >= 14) {
                            for(LaserBuddy b : buddyBall) {
                                if(b.state != BuddyState.Circling) {
                                    b.returnToCircle(game);
                                }
                            }
                            buddyBall.clear();
                            buddyState = BuddyPattern.Cooldown;
                        }

                    }
                    break;
            }
        }
    }
    
    public boolean entityOffLeft() {
        return false;
    }
    
    public void takeDamage(double damage) {
        double hpBefore = hp;
        super.takeDamage(damage);
        if(state == BossState.Fly) {
            if(hpBefore/startingHP > 2.0/3 &&
                    hp/startingHP <= 2.0/3) {
                releaseBuddies(12);
            }
            else if(hpBefore/startingHP > 1.0/3 &&
                    hp/startingHP <= 1.0/3) {
                releaseBuddies(16);
            }
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        
        if(exploding() && nExplosions > 0) {
            if(buddies.size() > 0) {
                for(LaserBuddy b : buddies) {
                    b.kill();
                }
                buddies.clear();
            }
            explosionTimer.updateTimer(timePassed);
            if(explosionTimer.tryToFire()) {
                double xC = x - width/2.0 + Math.random() * width;
                double chosenHeight = ((state == BossState.Fly)? flyBoxHeight : this.height);
                double yC = y - chosenHeight/2.0 + Math.random() * chosenHeight;
                game.addExplosion(new ExplosionEffect(xC, yC, (int) (100 + Math.random() * 60), 0.35));
                SoundManager.play(explosion);
                nExplosions--;
                if(nExplosions == 0 && state != BossState.Fly) {
                    game.addExplosion(new ExplosionEffect(x, y, size, 0.5));
                    game.addEffect(new ColorFlash(0.5, 5.0, (int) game.getGameWidth(), (int) game.getGameHeight(), 1.0f, 1.0f, 1.0f));
                    SoundManager.play(bigBoom);
                    chaseTheme.fadeOut(3);
                }
            }
        }
        else {
        
            move(timePassed, game);
            if(state == BossState.Fly) {
                manageBuddies(timePassed, game);
                if(!secondThemeStarted) {
                    secondThemeDelay.updateTimer(timePassed);
                    if(secondThemeDelay.tryToFire()) {
                        secondTheme.play();
                        secondThemeStarted = true;
                    }
                }
            }
            else {
                aim(game);
                if(state == BossState.Walk || x < game.getGameWidth()) {
                    shoot(timePassed, game);
                }
                if(hp <= 0) {
                    nExplosions = 50;
                    startFlying(game);
                } 
            }
        }
    }
    
    public void endMusic() {
        secondTheme.fadeOut(6.0);
    }
    
    private boolean exploding() {
        return super.isDead();
    }
    
    public boolean isDead() {
        return super.isDead() && state == BossState.Fly && nExplosions == 0;
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
        if(state == BossState.Fly) {
            hoverHead.draw(g, x, y, orientation);
        }
        else {
            legS.draw(g, x + headX + rearLegX, baseY + headY + rearLegY, 0);
            rearArmS.draw(g, x + headX + gunX, baseY + headY + gunY, gunAng);
            headS.draw(g, x + headX, baseY + headY, 0);
            legS.draw(g, x + headX + foreLegX, baseY + headY + foreLegY, 0);

            Sprite gunSprite;
            if(onRight) {
                gunSprite = (canFlash && fireTimer.getTimeElapsed() < flashTime)? gunFire : gunS;
            }
            else {
                gunSprite = (slowAttack.getTimeElapsed() < flashTime)? gunFire : gunS;
            }
            gunSprite.draw(g, x + headX + gunX, baseY + headY + gunY, gunAng);
        }
    }
}
