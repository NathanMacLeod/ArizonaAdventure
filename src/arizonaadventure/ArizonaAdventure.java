/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class ArizonaAdventure extends JPanel implements Runnable {
    private Thread run;
    private boolean running;
    private int width, height;
    private BufferedImage buffer;
    private boolean w, a, s, d;
    
    private Player player;
    private ArrayList<KillableEntity> enemies;
    private ArrayList<Projectile> projectiles;
    
    private SpawnPeriod testLevel;
    
    /**
     * @param args the command line arguments
     */
    public ArizonaAdventure() {
        JFrame frame = new JFrame();
        frame.setContentPane(this);
        frame.setResizable(false);
        width = 1000;
        height = 600;
        frame.resize(width, height + 30);
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        player = new Player(100, 300);
        enemies = new ArrayList();
        projectiles = new ArrayList();
        
        frame.addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        w = true;
                        break;
                    case KeyEvent.VK_S:
                        s = true;
                        break;
                    case KeyEvent.VK_A:
                        a = true;
                        break;
                    case KeyEvent.VK_D:
                        d = true;
                        break;
                    
                }
            }
            
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        w = false;
                        break;
                    case KeyEvent.VK_S:
                        s = false;
                        break;
                    case KeyEvent.VK_A:
                        a = false;
                        break;
                    case KeyEvent.VK_D:
                        d = false;
                        break;
                }
            }
        });
        
        start();
    }   
 
    private void start() {
        running = true;
        run = new Thread(this);
        run.start();
    }
    
    public void run() {
        long currentTime = System.nanoTime();
        long previousTime = currentTime;
        
        testLevel = new SpawnPeriod(100, 2, new int[] {10, 30, 2, 3});
        
        while(running) {
            currentTime = System.nanoTime();
            double timePassed = (currentTime - previousTime)/ Math.pow(10, 9);
            previousTime = currentTime;
            
            double hVel = 250 * timePassed;
            double vVel = 200 * timePassed;
            
            double xVel = 0, yVel = 0;
            if(w) {
                yVel = -vVel;
            }
            else if(s) {
                yVel = vVel;
            }
            if(a) {
                xVel = -hVel;
            }
            else if(d) {
                xVel = hVel;
            }
            
            gameUpdate(timePassed);
            gameRender();
        }
    }
    
    private void gameUpdate(double timePassed) {
        player.update(timePassed, this);
        for(KillableEntity enemy : enemies) {
            enemy.update(timePassed, this);
        }
        for(Projectile p : projectiles) {
            p.update(timePassed, this);
        }
        
        testLevel.update(timePassed, this);
        
        for(int i = 0; i < enemies.size(); i++) {
            KillableEntity e = enemies.get(i);
            if(e.isDead() || e.entityOffLeft()) {
                enemies.remove(i);
                i--;
            }
        }
        
        for(int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if(p.hasCollided() || p.entityOutOfBounds(this)) {
                projectiles.remove(i);
                i--;
            }
        }
    }
    
    private void gameRender() {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        player.draw(g);
        for(KillableEntity enemy : enemies) {
            enemy.draw(g);
        }
        for(Projectile p : projectiles) {
            p.draw(g);
        }
        getGraphics().drawImage(buffer, 0, 0, null);    
    }
    
    public void addNewProjectile(Projectile p) {
        projectiles.add(p);
    }
    
    public void addNewEnemy(KillableEntity e) {
        enemies.add(e);
    }
    
    public ArrayList<KillableEntity> getEnemies() {
        return enemies;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public double getGameWidth() {
        return width;
    }
    
    public double getGameHeight() {
        return height;
    }
    
    public boolean getW() {
        return w;
    }
    
    public boolean getA() {
        return a;
    }
    
    public boolean getS() {
        return s;
    }
    
    public boolean getD() {
        return d;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        ArizonaAdventure game = new ArizonaAdventure();
    }
}
