import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class GameOver extends MouseAdapter {

    //has an hud so statistics can be pulled
    private HUD hud;

    //performance statistics from the game
    private int endMoney = 0;                   //cash balance at game end
    private int endNetIncome = 0;               //net income at game end
    private int endUnitSales = 0;               //unit sales at game end
    private String gameOverMessage = "";        //game over message, filled in later

    //constructor class, getting the hud
    public GameOver(HUD hud){
        this.hud = hud;
    }

    //this is called when the mouse is clicked
    public void mousePressed(MouseEvent e){
        //gets mouse coordinates
        int mx = e.getX();
        int my = e.getY();

        //game over button coordinates
        if(mouseOver(mx, my, 627, 397, 356, 46)){
            //game is exited once button is pressed
            System.exit(0);  
        }
    }

    public void mouseReleased(MouseEvent e){

    }

    //this checks where the mouse is on the screen when it is clicked
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height){
        if(mx > x && mx < x + width){
            if(my > y && my < y + height){
                return true;
            }
            else return false;
        }else return false;
    }

    //code is executed every loop
    public void tick(){

        //fills in stat values based on hud values
        endMoney = hud.getMoney();
        endNetIncome = hud.getNetIncome();
        endUnitSales = hud.finalUnitSales();

        //gets the unique game over message based on performance
        gameOverMessage = hud.gameOverMessage();

    }

    //renders graphics
    public void render(Graphics g){
        //creating fonts
        Font titleFont = new Font("arial", 2|3, 110);
        Font statsFont = new Font("arial", 1, 30);
        Font creditsFont = new Font("arial", 1, 20);
        Font thanksFont = new Font("arial", 2, 40);

        //creates the white/blue offset GAME OVER message
        g.setFont(titleFont);
        g.setColor(Color.blue);
        g.drawString("GAME OVER", 295, 275);
        g.setColor(Color.white);
        g.drawString("GAME OVER", 300, 275);

        //creates the unique game over message
        g.setFont(statsFont);
        g.setColor(Color.blue);
        g.drawString(gameOverMessage, 277, 340);
        
        //types out the various game stats
        g.setColor(Color.black);
        g.drawString("Cash Balance: $" + endMoney, 277, 375);
        g.drawString("Net Income: $" + endNetIncome, 277, 410);
        g.drawString("Unit Sales: " + endUnitSales + " units", 277, 445);

        //types out my game credits
        g.setFont(creditsFont);
        g.setColor(Color.darkGray);
        g.drawString("Created by Carson Gray",946,630);
        g.drawString("Version 1.0",1070,654);

        //types out the thank you message
        g.setFont(thanksFont);
        g.setColor(Color.white);
        g.drawString("Thanks for Playing!", 627, 360);

        //exit game button
        g.setColor(Color.blue);             //blue border
        g.fillRect(627, 385, 356, 46);
        g.setColor(Color.gray);             //gray interior
        g.fillRect(630, 388, 350, 40);
        g.setFont(statsFont);
        g.setColor(Color.white);
        g.drawString("EXIT GAME", 712, 418);
    }
}