/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
/**
 *
 * @author macle
 */
public class ArizonaAdventure extends JPanel implements Runnable {
    private Thread run;
    private boolean running;
    private int width, height;
    private BufferedImage buffer;
    private Sprite token;
    private int tokenSize = 50;
    private boolean w, a, s, d;
    private boolean click;
    private int mouseX, mouseY;
    private int maxLevel;
    private UpgradeList upgrades;
    private int upgradeTokens = 0;
    private int maxTokens = 0;
    
    private boolean playerDead;
    private Player player;
    private ArrayList<Audible> sounds;
    private ArrayList<KillableEntity> enemies;
    private ArrayList<KillableEntity> enemyQueue;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Projectile> projQueue;
    private ArrayList<ExplosionEffect> explosions;
    private ArrayList<Pickup> pickups;
    private ArrayList<Effect> effects;
    
    private ImagePanel currPanel;
    private Level currLevel;
    private boolean inMenu = true;
    private int currLevelNumber;
    //private SpawnPeriod testLevel;
    //private Boss testBoss;
    
    
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
        upgrades = new UpgradeList();
        token = new Sprite("token.png", tokenSize);
        
        enemies = new ArrayList();
        enemyQueue = new ArrayList();
        projectiles = new ArrayList();
        explosions = new ArrayList();
        effects = new ArrayList();
        pickups = new ArrayList();
        projQueue = new ArrayList();
        sounds = new ArrayList();
        
        mouseX = 0;
        mouseY = 0;
        maxLevel = 1;
        
        currPanel = new MainMenu(this);
        
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
        
        addMouseListener(new MouseAdapter() {
            
            public void mouseReleased(MouseEvent e) {
                click = true;
                mouseX = e.getX();
                mouseY = e.getY();
            }
   
        });
        
        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
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
        
        //testLevel = new SpawnPeriod(100, 2, new int[] {10, 30, 2, 3});
        //testBoss = new TrainBoss(this);
        while(running) {
            currentTime = System.nanoTime();
            double timePassed = (currentTime - previousTime)/ Math.pow(10, 9);
            previousTime = currentTime;
            
            if(inMenu) {
                menus(timePassed);
            }
            else {
                gameUpdate(timePassed);
                if(!inMenu) {
                    gameRender();
                }
            }
        }
    }
    
    public void loadMenu(MenuState newMenu) {
        switch(newMenu) {
            case Levels:
                currPanel = new LevelSelect(maxLevel);
                break;
            case Upgrades:
                currPanel = new UpgradeMenu(this);
        }
    }
    
    public void returnFromGame(boolean completed) {
        cutSounds();
        currLevel.unload();
        if(completed && currLevelNumber == maxLevel) {
            maxLevel++;
            switch(currLevelNumber) {
                case 1:
                    upgradeTokens += 10;
                    maxTokens += 10;
                    break;
                case 2:
                    upgradeTokens += 15;
                    maxTokens += 15;
                    break;
                case 3:
                    upgradeTokens += 5;
                    maxTokens += 5;
                    break;
            }
            save();
        }
        currPanel = new LevelSelect(maxLevel);
        click = false;
        inMenu = true;
    }
    
    public void loadLevel(int level) {
        switch(level) {
            case 1:
                currLevel = new Level1((int) getGameWidth(), (int) getGameHeight(), this);
                break;
            case 2:
                currLevel = new Level2((int) getGameWidth(), (int) getGameHeight(), this);
                break;
            case 3:
                currLevel = new Level3((int) getGameWidth(), (int) getGameHeight(), this);
                break;
            case 4:
                currLevel = new Level4((int) getGameWidth(), (int) getGameHeight(), this);
                break;
            default:
                System.out.println("No corresponding level found");
                return;
        }
        playerDead = false;
        currLevelNumber = level;
        player = new Player(-200, 300, upgrades);
        player.startZoomIn();
        enemies.clear();
        projectiles.clear();
        explosions.clear();
        effects.clear();
        pickups.clear();
        projQueue.clear();
        effects.add(new ColorFlash(0, 5, (int) width, (int) height, 0.0f, 0.0f, 0.0f));
        inMenu = false;
    }
    
    private void menus(double timePassed) {
        currPanel.update(timePassed, this);
        click = false;
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        currPanel.draw(g);
        if(!(currPanel instanceof MainMenu)) {
            drawTokens(g);
        }
        getGraphics().drawImage(buffer, 0, 0, null);  
    }
    
    private void gameUpdate(double timePassed) {
        if(!playerDead) {
            player.update(timePassed, this);
        }
        
        projectiles.addAll(projQueue);
        projQueue.clear();
        
        enemies.addAll(enemyQueue);
        enemyQueue.clear();
        
        for(KillableEntity enemy : enemies) {
            enemy.update(timePassed, this);
        }
        for(Projectile p : projectiles) {
            p.update(timePassed, this);
        }
        for(ExplosionEffect e : explosions) {
            e.update(timePassed, this);
        }  
        for(Pickup p : pickups) {
            p.update(timePassed, this);
        }
        for(Effect e : effects) {
            e.update(timePassed, this);
        }
        for(Audible a : sounds) {
            a.update(timePassed, this);
        }
        
        currLevel.update(timePassed, this);
        
        if(player.isDead() && !playerDead) {
            player.explode(this);
            currLevel.playerDead(this);
            playerDead = true;
        }
        
        for(int i = 0; i < enemies.size(); i++) {
            KillableEntity e = enemies.get(i);
            if(e.isDead() || e.entityOffLeft()) {
                enemies.remove(i);
                if(e.isDead()) {
                    e.explode(this);
                }
                i--;
            }
        }
        
        for(int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if(p.expired() || p.entityOutOfBounds(this)) {
                p.deleteActions();
                projectiles.remove(i);
                i--;
            }
        }
        
        for(int i = 0; i < explosions.size(); i++) {
           ExplosionEffect e = explosions.get(i);
            if(e.pastLife()) {
                explosions.remove(i);
                i--;
            }
        }
        
        for(int i = 0; i < pickups.size(); i++) {
            Pickup p = pickups.get(i);
            if(p.isConsumed() || p.entityOffLeft()) {
                pickups.remove(i);
                i--;
            }
        }
        
        for(int i = 0; i < effects.size(); i++) {
            Effect e = effects.get(i);
            if(e.done()) {
                effects.remove(i);
                i--;
            }
        }
    }
    
    private void drawTokens(Graphics2D g) {
        token.draw(g, width - tokenSize * 2, tokenSize/1.5, 0);
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.8f));
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        g.drawString("x" + upgradeTokens, (int) (width - tokenSize * 1.5), tokenSize);
    }
    
    private void gameRender() {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        //g.setColor(Color.WHITE);
        //g.fillRect(0, 0, width, height);
        currLevel.draw(g);
        if(!playerDead) {
            player.draw(g);
        }
        for(KillableEntity enemy : enemies) {
            enemy.draw(g);
        }
        for(Projectile p : projectiles) {
            p.draw(g);
        }
        for(Pickup p : pickups) {
            p.draw(g);
        }
        for(ExplosionEffect e : explosions) {
            e.draw(g);
        }
        for(Effect e : effects) {
            e.draw(g);
        }
        currLevel.drawForeMid(g);
        getGraphics().drawImage(buffer, 0, 0, null);    
    }
    
    public void loadSave() {
        try {
            Scanner scanner = new Scanner(new File("save.txt"));
            int level = Integer.parseInt(scanner.nextLine());
            int tokens = Integer.parseInt(scanner.nextLine());
            maxLevel = level;
            maxTokens = tokens;
            upgradeTokens = maxTokens;
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    
    private void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"));
            writer.write("" + maxLevel + "\n" + maxTokens);
            writer.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public boolean saveFileExists() {
        File save = new File("save.txt");
        return save.exists();
    }
    
    public void eraseSaveFile() {
        File save = new File("save.txt");
        save.delete();
    }
    
    public void cutSounds() {
        for(Audible a : sounds) {
            a.stopAudio();
        }
        SoundManager.stopAudio();
        sounds.clear();
    }
    
    public void addSound(Audible a) {
        sounds.add(a);
    }
    
    public void refundUpgrades() {
        upgrades.reset();
        upgradeTokens = maxTokens;
    }
    
    public void subTokens(int n) {
        upgradeTokens -= n;
    }
    
    public int getTokens() {
        return upgradeTokens;
    }
    
    public UpgradeList getUpgrades() {
        return upgrades;
    }
    
    public boolean getClick() {
        return click;
    }
    
    public int getMouseX() {
        return mouseX;
    }
    
    public int getMouseY() {
        return mouseY;
    }
    
    public void addPickup(Pickup p) {
        pickups.add(p);
    }
    
    public void addEffect(Effect e) {
        effects.add(e);
    }
    
    public void addExplosion(ExplosionEffect e) {
        explosions.add(e);
    }
    
    public void addNewProjectile(Projectile p) {
        projQueue.add(p);
    }
    
    public void addNewEnemy(KillableEntity e) {
        enemyQueue.add(e);
    }
    
    public ArrayList<KillableEntity> getEnemies() {
        return enemies;
    }
    
    public boolean allEnemiesDead() {
        return enemies.isEmpty();
    }
    
    public ArrayList<Pickup> getPickups() {
        return pickups;
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
