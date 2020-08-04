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
public class MechBoss extends KillableEntity implements Boss {
    private double life;
    private static double startingHP = 28000;
    private static int size = 550;
     private static double spawnX = -size;
    private static double width = size / 4.5;
    private static double height = size / 1.7;
    private static double strideWidth = size * 30.0 / 500;
    private static double strideHeight = size * 50.0 / 500;
    private static double stridePeriod = 0.75;
    private static double pausePeriod = 0.5;
    private static double gunR = size * 250.0 / 500;
    private static double gunA = -Math.PI / 32;
    private static double targetX = 70;
    private static double targetXLeft = 800;
    private boolean firstWalkOn = true;
    private boolean firstWalkOnRight = false;
    private boolean dissapearToLeft = false;
    
    
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
    
    private GunState gunState;
    private GunState prevAttack;
    
    private static int nShots = 4;
    private int shotCount;
    private CooldownTimer fireTimer;
    private CooldownTimer reloadTimer;
    private CooldownTimer slowAttack;
    private boolean canFlash = true;
    boolean onRight;
    
    //shotgun stuff
    private double shotCone = Math.PI/12;
    private double shotConeCos;
    private double shotConeSin;
    private double shotDCos;
    private double shotDSin;
    private int nShotProj = 8;
    
    
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
        
        prevAttack = GunState.Reload;
        gunState = GunState.Reload;
        fireTimer = new CooldownTimer(1);
        reloadTimer = new CooldownTimer(0.56);
        slowAttack = new CooldownTimer(0.4);
        
        shotConeCos = Math.cos(-shotCone/2.0);
        shotConeSin = Math.sin(-shotCone/2.0);
        double del = shotCone / (nShotProj + 1);
        shotDCos = Math.cos(del);
        shotDSin = Math.sin(del);
        
        flip();
        game.addNewEnemy(this);
    }
    
    private enum GunState {
        Reload, Slug, Shotgun, Bomb, Rocket;
    }
    
    public void dissapearToLeft() {
        dissapearToLeft = true;
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
        return hp <= 0;
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
    
    public void shoot(double timePassed, ArizonaAdventure game) {
        Player p = game.getPlayer();

        if(!onRight) {
            gunAng = new Vector2D(p.x - x - gunX, p.y - baseY - gunY).getAngle();
            slowAttack.updateTimer(timePassed);
            if(slowAttack.tryToFire()) {
                double barrelX = x + headX + gunX + gunR * Math.cos(gunAng - gunA);
                double barrelY = y + headY + gunY + gunR * Math.sin(gunAng - gunA);
                Vector2D dir = new Vector2D(p.x - barrelX, p.y - barrelY).getUnitVector();
                game.addNewProjectile(new BasicEnemyBullet(barrelX, barrelY, 55, 25, dir.scale(500), 40, slug));
            }
            return;
        }
        
        gunAng = (gunState == GunState.Bomb)? Math.PI * 1.0/4 : Math.PI + new Vector2D(p.x - x - gunX, p.y - baseY - gunY).getAngle();
        
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
                } while(newAttack == prevAttack);
                prevAttack = newAttack;
                gunState = newAttack;
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
                        game.addNewProjectile(new BasicEnemyBullet(barrelX, barrelY, 55, 25, dir.scale(830), 40, slug));
                        break;
                    case Shotgun:
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
                        break;
                    }
                
                shotCount--;
                if(shotCount <= 0) {
                    gunState = GunState.Reload;
                }
            }
        }
    }
    
    public void move(double timePassed, ArizonaAdventure game) {
        
        if(dissapearToLeft) {
            foreLegY = legRestY;
            rearLegY = legRestY;
            moveEntity(-walkSpeed * timePassed, 0, 0);
            if(x < spawnX/2.0) {
                flip();
                moveEntity(game.getGameWidth() - spawnX, 0, 0);
                dissapearToLeft = false;
                firstWalkOnRight = true;
            }
            return;
        }
        else if(firstWalkOnRight) {
            foreLegY = legRestY;
            rearLegY = legRestY;
            moveEntity(-walkSpeed * timePassed, 0, 0);
            if(x < targetXLeft) {
                firstWalkOnRight = false;
            }
            return;
        }
        
        double t = life % (stridePeriod * 2 + pausePeriod * 2);
        double effectiveT ;
        if(t > stridePeriod && t < stridePeriod + pausePeriod) {
            effectiveT = stridePeriod;
        }
        else if(t > stridePeriod + pausePeriod && t < stridePeriod * 2 + pausePeriod) {
            effectiveT = t - pausePeriod;
        }
        else if(t > stridePeriod * 2 + pausePeriod) {
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
        
        if(firstWalkOn && x < targetX) {
            moveEntity(xV * timePassed, 0, 0);
            if(x > targetX) {
                firstWalkOn = false;
            }
        }
        else {
            moveEntity((xV - walkSpeed) * timePassed, 0, 0);
        }

    }
    
    public boolean entityOffLeft() {
        return false;
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        life += timePassed;
        move(timePassed, game);
        if(!firstWalkOnRight) {
            shoot(timePassed, game);
        }
    }
    
    public void draw(Graphics2D g) {
        //super.draw(g);
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
