import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.net.URI;
import java.awt.Desktop;

public class HowTo extends MouseAdapter {

    //takes the game in order to navigate game states
    private Game game;

    public HowTo(Game game){
        this.game = game;
    }

    //mouse press
    public void mousePressed(MouseEvent e){
        int mx = e.getX();
        int my = e.getY();

        //coordinates of back button
        if(mouseOver(mx, my, 330, 450, 206, 46)){
            game.gameState = Game.STATE.Menu;
        }

        //coordinates of tutorial link
        if(mouseOver(mx, my, 335, 350, 580, 40)){
            try{
                //url to tutorial video
                URI tutorialURL = new URI("https://www.youtube.com/channel/UCjuDFH4cJThu6wTA8K4lR3A/featured?view_as=subscriber");
                Desktop desktop = java.awt.Desktop.getDesktop();
                
                //follows the url
                desktop.browse(tutorialURL);
            }catch(Exception e2){
                //just in case it doesn't work
                e2.printStackTrace();
            }
        }
    }

    public void mouseReleased(MouseEvent e){

    }

    //tracks mouse location when clicked
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height){
        if(mx > x && mx < x + width){
            if(my > y && my < y + height){
                return true;
            }
            else return false;
        }else return false;
    }

    //nothing ticks, since its code relies on mouse listening
    public void tick(){

    }

    //renders the graphics
    public void render(Graphics g){

        //creating the fonts
        Font titleFont = new Font("arial", 1, 50);
        Font buttonFont = new Font("arial", 1, 30);
        Font bodyFont = new Font("arial", 1, 20);
        Font urlFont = new Font("arial", 1, 12);
        Font creditsFont = new Font("arial", 1, 20);

        //back button
        g.setColor(Color.blue);             //blue outline
        g.fillRect(330, 450, 206, 46);
        g.setColor(Color.gray);             //gray interior
        g.fillRect(333, 453, 200, 40);
        g.setColor(Color.white);            //white text
        g.setFont(buttonFont);
        g.drawString("BACK", 387, 483);

        //how to play title, white/blue offset
        g.setFont(titleFont);
        g.setColor(Color.blue);
        g.drawString("HOW TO PLAY", 454, 175);
        g.setColor(Color.white);
        g.drawString("HOW TO PLAY", 457, 175);

        //this is the box the text is iin
        g.setColor(Color.blue);             //blue outline
        g.fillRect(330, 220, 600, 200);
        g.setColor(Color.white);            //white interior
        g.fillRect(333, 223, 594, 194);

        //text that goes in the box
        g.setColor(Color.black);
        g.setFont(bodyFont);
        g.drawString("Welcome to the GRAYTECH simulation!", 345, 260);
        g.drawString("Follow the clickable link below to watch a short series", 345, 307);
        g.drawString("that walks through the game mechanics. Enjoy!", 345, 329);

        //url text
        g.setFont(urlFont);
        g.setColor(Color.white);
        g.drawRect(335, 350, 580, 40);  //this is an invisible border that has the button itself
        g.setColor(Color.blue);
        //this text is just a string, the link is internal
        g.drawString("https://www.youtube.com/channel/UCjuDFH4cJThu6wTA8K4lR3A/featured?view_as=subscriber", 345, 376);

        //types out the credits
        g.setFont(creditsFont);
        g.setColor(Color.darkGray);
        g.drawString("Created by Carson Gray",946,630);
        g.drawString("Version 1.0",1070,654);
    }
}