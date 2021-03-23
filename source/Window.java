import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends Canvas {

    private static final long serialVersionUID = 1L;

    //window constructor
    //THIS CLASS STARTS THE GAME ONCE CONSTRUCTED
    
    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame(title);

        //sets parameters for size of game window
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        //make it exit out, not resizable, start in middle of screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        //this adds the game to the window and makes it visible
        frame.add(game);
        frame.setVisible(true);

        //THIS IS HOW THE GAME STARTS
        game.start();

    }

}