import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class Menu extends MouseAdapter {

    private Game game;              //game to access game state
    private Handler handler;        //handler to initizalize the objects

    public Menu(Game game, Handler handler){
        this.game = game;
        this.handler = handler;
    }


    public void mousePressed(MouseEvent e){
        int mx = e.getX();
        int my = e.getY();

        //Play button, adds the objects to the handler
        if(mouseOver(mx, my, 427, 397, 356, 46)){
            game.gameState = Game.STATE.Game;
            
            handler.addObject(new OrderTerminal(100, 100, ID.OrderTerminal, handler));
            handler.addObject(new Printer3d(100, 100, ID.Printer3d, handler));
            handler.addObject(new MechAssembly(100, 100, ID.MechAssembly, handler));
            handler.addObject(new ElecAssembly(100, 100, ID.ElecAssembly, handler));
            handler.addObject(new FinalAssembly(100, 100, ID.FinalAssembly, handler));
        }

        //how to button
        if(mouseOver(mx, my, 427, 457, 356, 46)){
            game.gameState = Game.STATE.HowTo;
            
        }
    }

    public void mouseReleased(MouseEvent e){

    }

    //checks mouse location, used during a click
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height){
        if(mx > x && mx < x + width){
            if(my > y && my < y + height){
                return true;
            }
            else return false;
        }else return false;
    }

    public void tick(){

    }


    public void render(Graphics g){
        Font grayOpsFont = new Font("arial", 2|3, 110);
        Font opManFont = new Font("arial", 1, 40);
        Font fnt2 = new Font("arial", 1, 30);
        Font creditsFont = new Font("arial", 1, 20);
        
        //white/blue offset title
        g.setFont(grayOpsFont);
        g.setColor(Color.blue);
        g.drawString("GRAYTECH", 295, 275);
        g.setColor(Color.white);
        g.drawString("GRAYTECH", 300, 275);

        //subtitle
        g.setFont(opManFont);
        g.setColor(Color.blue);
        g.drawString("Operations Management Simulation", 277, 335);

        //start button
        g.setColor(Color.blue);             //blue outline
        g.fillRect(427, 397, 356, 46);
        g.setColor(Color.gray);             //gray interior
        g.fillRect(430, 400, 350, 40);
        g.setFont(fnt2);
        g.setColor(Color.white);
        g.drawString("START THE GAME", 470, 430);

        //how to button
        g.setColor(Color.blue);             //blue outline
        g.fillRect(427, 457, 356, 46);
        g.setColor(Color.gray);             //gray interior
        g.fillRect(430, 460, 350, 40);
        g.setFont(fnt2);
        g.setColor(Color.white);
        g.drawString("HOW TO PLAY", 498, 490);

        //credits
        g.setFont(creditsFont);
        g.setColor(Color.darkGray);
        g.drawString("Created by Carson Gray",946,630);
        g.drawString("Version 1.0",1070,654);
    }
}