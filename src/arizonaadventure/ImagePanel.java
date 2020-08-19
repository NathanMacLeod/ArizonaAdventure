/*
 * File added by Nathan MacLeod 2020
 */
package arizonaadventure;

/**
 *
 * @author macle
 */
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics2D;

public class ImagePanel implements Drawable, Updatable {
    
    protected BufferedImage image;
    protected ArrayList<Button> buttons;
    
    public ImagePanel() {
        buttons = new ArrayList();
    }
    
    public void click(int x, int y, ArizonaAdventure game) {
        for(Button b : buttons) {
            if(b.isHovered(x, y)) {
                b.doAction(game);
            }
        }
    }
    
    public void update(double timePassed, ArizonaAdventure game) {
        int mX = game.getMouseX();
        int mY = game.getMouseY();
        for(Button b : buttons) {
            if(b.isHovered(mX, mY)) {
                b.setHovered(true);
                if(game.getClick()) {
                    b.doAction(game);
                }
            }
            else {
                b.setHovered(false);
            }
        }
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(image, 0, 0, null);
        for(Button b : buttons) {
            b.draw(g);
        }
    }
}
