/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arizonaadventure;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
/**
 *
 * @author macle
 */
public class Credits extends ImagePanel {
    
    private Music music;
    private Font creditsFont;
    private double fontGap = 50;
    private BufferedImage background;
    private final double panSpeed = 60;
    private double time;
    private double creditsY = 600;
    private double y;
    private double x;
    
    private static final String[] credits = new String[] {
        "ARIZONA ADVENTURE",
        "",
        "",
        "Source images for sprites found on Google Images",
        "",
        "Devon:",
        "Redbull laser satellite",
        "Teapot turret",
        "Sierra mist turret",
        "Wii-remote turret",
        "Train engine car",
        "",
        "",
        "Sound effects and music from freesound.org",
        "",
        "Sirkoto51:",
        "Success Loop #1.wav",
        "Level Loop - Unleash The Universe",
        "Boss Battle Loop #1",
        "Boss Battle Loop #2",
        "Stress Music/Ambient Loop",
        "Boss Battle Loop #3",
        "",
        "EFlexMusic:",
        "Artillery Explosion (Close)(Mixed)",
        "",
        "lulyc:   Retro game heal sound",
        "Raclure:   Damage sound effect",
        "HenryRichard:   SFX-Clear!.wav",
        "joshuaempyre:   Arcade Music Loop.wav",
        "edwardszakal:   Game Music",
        "JoelAudio:   ELECTRIC_ZAP_001.wav",
        "deleted_user_96:   Cha Ching.wav",
        "CGEffex:   Submarie dive horn_better.wav",
        "InspectorJ:   Boiling Water, Large, A.wav",
        "0ne_one111yt:   pew.ogg",
        "BeneGe:   nasal-spray-single-stero.flac",
        "kernschall:   thin metal sliding door close slam",
        "debsound:   Train Loop 07.wav",
        "Kneeling:   cannon.mp3",
        "SamsterBirdies:   laser",
        "MikeE63:   blaster shot single 5.wav",
        "Mozfoo:   Rocket Loop",
        "qubodup:   Rocket Launch.flac",
        "Jarusca:   Rocket Launch",
        "estlacksensory:   Estlack_doorslam_rvrb_3shrt.aif",
        "britishpirate93:   FW Kick.wav",
        "LiamG_SFX:   Explosion 1",
        "tommccann:   Explosion_01.wav",
        "jobro:   Laser9.wav",
        "Kodack:   Unrelenting Shotgun",
        "",
        "",
        "",
        "",
        "Thanks for playing :)"
    };
    
    public Credits() {  
        super();
        y = 0;
        time = 0;
        BufferedImage returnB = null;
        BufferedImage returnBHover = null;
        try {
            returnB = ImageIO.read(new File("./sprites/returnButton.png"));
            returnBHover = ImageIO.read(new File("./sprites/returnButtonHighlighted.png"));
            background = ImageIO.read(new File("./sprites/creditsbg.png"));
        } catch(IOException e) {
            System.out.println("Unable to load level select sprites");
        }
        
        Button returnButton = new Button(returnB, returnBHover, 0, 0, 190, 70) {
            public void doAction(ArizonaAdventure game) {
                music.stop();
                game.loadMenu(MenuState.Levels);
            }
        };

        music = new Music("credittheme.wav");
        music.play();
        creditsFont = new Font("SansSerif", Font.BOLD, 30);
        buttons.add(returnButton);
    }
    
    private void writeCenteredText(Graphics2D g, String message, int x, int y) {
        g.setFont(creditsFont);
        int Y = (y);
        int X = (x) - g.getFontMetrics(creditsFont).stringWidth(message)/2;
        g.drawString(message, X, Y);
    }
    
    private void drawText(Graphics2D g) {
        for(int i = 0; i < credits.length; i++) {
            int y = (int) (creditsY + i * fontGap);
            g.setColor(Color.black);
            writeCenteredText(g, credits[i], 500 + 4, y + 4);
            g.setColor(new Color(236, 176, 204));
            writeCenteredText(g, credits[i], 500 + 2, y + 2);
            g.setColor(Color.white);
            writeCenteredText(g, credits[i], 500, y);
        }
    }
    
    public void draw(Graphics2D g) {
        int y1 = -(int) y;
        int y2 = background.getHeight() -(int) y;
        int x1 = -(int) x;
        int x2 = background.getWidth() - (int) x;
        
        g.drawImage(background, x1, y1, null);
        if(y2 < 600) {
            g.drawImage(background, x1, background.getHeight() -(int) y, null);
        }
        if(x2 < 1000) {
            g.drawImage(background, x2, y1, null);
            if(y2 < 600) {
                g.drawImage(background, x2, background.getHeight() -(int) y, null);
            }
        }
        
        drawText(g);
        
        for(Button b : buttons) {
            b.draw(g);
        }
        
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        time += timePassed;
        super.update(timePassed, game);
        creditsY -= panSpeed * timePassed * 2.0;
        y += panSpeed * timePassed;
        x += panSpeed * timePassed;
        if(y >= background.getHeight()) {
            y -= background.getHeight();
        }
        if(x >= background.getWidth()) {
            x -= background.getWidth();
        }
    }
    
}
