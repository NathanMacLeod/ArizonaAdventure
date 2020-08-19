/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author macle
 */
public class MainMenu extends ImagePanel {
    
    public MainMenu(ArizonaAdventure game) {
        super();
        BufferedImage play = null;
        BufferedImage playHover = null;
        BufferedImage save = null;
        BufferedImage saveHover = null;
        BufferedImage noSave = null;
        try {
            play = ImageIO.read(new File("./sprites/playbutton.png"));
            playHover = ImageIO.read(new File("./sprites/playbuttonhover.png"));
            save = ImageIO.read(new File("./sprites/resetbutton.png"));
            saveHover = ImageIO.read(new File("./sprites/resetbuttonhover.png"));
            noSave = ImageIO.read(new File("./sprites/resetbuttonnosave.png"));
            this.image = ImageIO.read(new File("./sprites/menu.png"));
        } catch(IOException e) {
            System.out.println("Unable to load main menu sprites");
        }
        
        Button playB = new Button(play, playHover, 500, 335, 254, 97) {
            
            public void doAction(ArizonaAdventure game) {
                if(game.saveFileExists()) {
                    game.loadSave();
                }
                game.loadMenu(MenuState.Levels);
            }
            
        };
        Button saveB = new ResetSaveButton(save, saveHover, noSave, 500, 465, 254, 97, game.saveFileExists());
        
        buttons.add(playB);
        buttons.add(saveB);
    }
    
    private class ResetSaveButton extends Button {
        private BufferedImage noSaveImage;
        private boolean isSave;
        
        public ResetSaveButton(BufferedImage image, BufferedImage hoverImage, BufferedImage noFileImage, int x, int y, int w, int h, boolean isSave) {
            super(image, hoverImage, x, y, w, h);
            this.isSave = isSave;
            noSaveImage = noFileImage;
        }
        
        public void doAction(ArizonaAdventure game) {
            if(isSave) {
                game.eraseSaveFile();
                isSave = false;
            }
        }
        
        public void draw(Graphics2D g) {
            if(isSave) {
                super.draw(g);
            }
            else {
                g.drawImage(noSaveImage, x, y, width, height, null);
            }
        }
    }
}
