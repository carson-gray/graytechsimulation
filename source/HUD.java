import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HUD extends MouseAdapter{

    //has a handler to access game objects
    private Handler handler;
    //has a game so it can go to 'game over' game state
    private Game game;

    //constructor
    public HUD(Game game, Handler handler){
        this.handler = handler;
        this.game = game;
    }

    //----------------------------------------------------------------------
    
    //ADJUSTABLE GAME SETTINGS
    private int money = 1000;              //player money, SETTING STARTING MONEY
    private int bankruptcyThreshold = 14;   //number of consecutive days with negative cash to trigger bankruptcy
    private int gameLengthDays = 366;        //length of the game in days, set desired number + 1
    
    //----------------------------------------------------------------------

    private int day = 0;                    //days
    private int hour = 0;                   //hours
    private int pacer = 0;                  //used internally to time the game
    
    //income report
    private int salesRev = 0;               //total revenue
    private int totalExp = 0;               //total expenses
    private int todaysRev = 0;              //for internal calculations
    private int storageExp = 0;             //total storage costs paid
    private int todaysStorageExp = 0;       //for internal calculations
    private int upgradeCost = 0;            //total upgrade expenditures
    private int dmExp = 0;                  //total materials expense
    private int netIncome = 0;              //net income in the game

    //endgame presets
    private int bankruptcyCount = 0;        //used internally to track bankruptcy
    private String gameOver_0 = "BANKRUPT!";        //negative cash balance for 2 weeks
    private String gameOver_1 = "UNPROFITABLE";     //net income < 0
    private String gameOver_2 = "LOW PROFITS";      //0     <   net income < 30
    private String gameOver_3 = "PROFITABLE";       //30    <=  net income < 120
    private String gameOver_4 = "HIGH PROFITS!";    //120   <=  net income < 160
    private String gameOver_5 = "MONEYBAGS!";       //160   <=  net income < 200
    private String gameOver_6 = "MIDAS TOUCH!";     //200   <=  net income
    private String myGameOver = "";
    //---------------------------------------------------------------------
    
    //CODE EXECUTED BASED ON TIME PROGRESSION
    public void tick(){
        
        //LOCAL PACER
        //every hour, it recomputes net income
        //every day, it completes sales and charges
        //every day, it checks for endgame conditions
        pacer++;
        if(pacer == handler.getHourThreshold()){
            pacer = 0;
            hour++;

            //recompute net income
                netIncome = salesRev - totalExp;

            //END OF THE DAY ACTIVITIES
            if(hour==handler.getDayThreshold()){
                day++;
                hour = 0;

                //sales take place
                    todaysRev = handler.object.get(4).sell();
                    salesRev += todaysRev;
                    money += todaysRev;
                    todaysRev = 0;
                
                //storage charges take place
                    todaysStorageExp += handler.object.get(1).charge();
                    todaysStorageExp += handler.object.get(4).charge();
                    money -= todaysStorageExp;
                    storageExp += todaysStorageExp;
                    totalExp += todaysStorageExp;
                    todaysStorageExp = 0;

                //recompute net income
                    netIncome = salesRev - totalExp;
                
                //ENDGAME CONDITION: time runs out
                //sets game over message based on net income
                if(day == gameLengthDays){
                    if(netIncome <= 0){
                        myGameOver = gameOver_1;
                    }
                    else if(netIncome > 0 && netIncome < 30000){
                        myGameOver = gameOver_2;
                    }
                    else if(netIncome >= 30000 && netIncome < 120000){
                        myGameOver = gameOver_3;
                    }
                    else if(netIncome >= 120000 && netIncome < 160000){
                        myGameOver = gameOver_4;
                    }
                    else if(netIncome >= 160000 && netIncome < 200000){
                        myGameOver = gameOver_5;
                    }
                    else if(netIncome >= 200000){
                        myGameOver = gameOver_6;
                    }
                    game.gameState = Game.STATE.GameOver;
                }

                //ENDGAME CONDITION: bankruptcy
                if(money < 0){
                    bankruptcyCount++;
                }
                if(money >= 0){
                    bankruptcyCount = 0;
                }
                if(bankruptcyCount == bankruptcyThreshold){
                    myGameOver = gameOver_0;
                    game.gameState = Game.STATE.GameOver;
                }
                
            }
        }
    }

    //----------------------------------------------------------------------
    //VARIOUS METHODS

    //this is how Game Over gets the message
    public String gameOverMessage(){
        return myGameOver;
    }

    //checks to see if there is enough money to make a purchase
    public boolean afford(int cost){
        if(money >= cost){
            return true;
        }

        else{
            return false;
        }
    }

    //pays for a purchase
    public void purchase(int cost){
        money -= cost;
    }

    //receives money after a sale
    public void sale(int revenue){
        money += revenue;
    }

    //called by other classes to see bank account
    public int getMoney(){
        return money;
    }

    //called by Game Over class to get ending net income
    public int getNetIncome(){
        return netIncome;
    }

    //called by Game Over class to get ending unit sales
    public int finalUnitSales(){
        return handler.object.get(4).getUnitSales();
    }

    //-------------------------------------------------------------------
    //THIS HOLDS ALL OF THE GAME BUTTONS
    
    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales
    
    public void mousePressed(MouseEvent e){
        int mx = e.getX();
        int my = e.getY();
        
        //ORDER TERMINAL
            // plus button, adds to order
            if(mouseOver(mx, my, 342,111,30,30)){
                handler.object.get(0).addToOrder();
            }
            // minus button, takes from order
            if(mouseOver(mx, my, 380,111,30,30)){
                handler.object.get(0).takeFromOrder();
            }
            // submit button, actually places order
            if(mouseOver(mx, my, 424,108,113,37)){
                if(afford(handler.object.get(0).getCost())){
                    //record for the books
                        dmExp += handler.object.get(0).getCost();
                        totalExp += handler.object.get(0).getCost();
                    //execute the order
                        purchase(handler.object.get(0).getCost());
                        handler.object.get(0).shipOrder(handler.object.get(0).getOrderSize());
                }
            }
        
        //3D PRINTER
            // upgrade button
            if(mouseOver(mx, my, 27,603,111,37)){
                if(afford(handler.object.get(1).getCycleUpgradeCost())){
                    if(handler.object.get(1).cycleUpgradeCheck()){
                        //record for the books
                            upgradeCost += handler.object.get(1).getCycleUpgradeCost();
                            totalExp += handler.object.get(1).getCycleUpgradeCost();
                        //execute the upgrade
                            purchase(handler.object.get(1).getCycleUpgradeCost());
                            handler.object.get(1).cycleUpgrade();
                    }
                }
            }
            // left arrow RED
            if(mouseOver(mx, my, 164,538,17,17)){
                handler.object.get(1).mDown();
            }
            // right arrow RED
            if(mouseOver(mx, my, 201,538,17,17)){
                handler.object.get(1).mUp();
            }
            // left arrow BLUE
            if(mouseOver(mx, my, 164,625,17,17)){
                handler.object.get(1).eDown();
            }
            // right arrow BLUE
            if(mouseOver(mx, my, 201,625,17,17)){
                handler.object.get(1).eUp();
            }

        //MECHANICAL ASSEMBLY
            // cycle time upgrade
            if(mouseOver(mx, my, 408,309,111,37)){
                if(afford(handler.object.get(2).getCycleUpgradeCost())){
                    if(handler.object.get(2).cycleUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(2).getCycleUpgradeCost();
                        totalExp += handler.object.get(2).getCycleUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(2).getCycleUpgradeCost());
                        handler.object.get(2).cycleUpgrade();
                    }
                }
            }
            // station upgrade
            if(mouseOver(mx, my, 529,309,111,37)){
                if(afford(handler.object.get(2).getStationUpgradeCost())){
                    if(handler.object.get(2).stationUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(2).getStationUpgradeCost();
                        totalExp += handler.object.get(2).getStationUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(2).getStationUpgradeCost());
                        handler.object.get(2).stationUpgrade();
                    }
                }
            }

        //ELECTRONICS ASSEMBLY
            // cycle time upgrade
            if(mouseOver(mx, my, 408,559,111,37)){
                if(afford(handler.object.get(3).getCycleUpgradeCost())){
                    if(handler.object.get(3).cycleUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(3).getCycleUpgradeCost();
                        totalExp += handler.object.get(3).getCycleUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(3).getCycleUpgradeCost());
                        handler.object.get(3).cycleUpgrade();
                    }
                }
            }
            // station upgrade
            if(mouseOver(mx, my, 529,559,111,37)){
                if(afford(handler.object.get(3).getStationUpgradeCost())){
                    if(handler.object.get(3).stationUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(3).getStationUpgradeCost();
                        totalExp += handler.object.get(3).getStationUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(3).getStationUpgradeCost());
                        handler.object.get(3).stationUpgrade();
                    }
                }
            }

        //FINAL ASSEMBLY
            // cycle time upgrade
            if(mouseOver(mx, my, 986,309,111,37)){
                if(afford(handler.object.get(4).getCycleUpgradeCost())){
                    if(handler.object.get(4).cycleUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(4).getCycleUpgradeCost();
                        totalExp += handler.object.get(4).getCycleUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(4).getCycleUpgradeCost());
                        handler.object.get(4).cycleUpgrade();
                    }
                }
            }
            // station upgrade
            if(mouseOver(mx, my, 1109,309,111,37)){
                if(afford(handler.object.get(4).getStationUpgradeCost())){
                    if(handler.object.get(4).stationUpgradeCheck()){
                        //record for the books
                        upgradeCost += handler.object.get(4).getStationUpgradeCost();
                        totalExp += handler.object.get(4).getStationUpgradeCost();
                        //execute upgrade
                        purchase(handler.object.get(4).getStationUpgradeCost());
                        handler.object.get(4).stationUpgrade();
                    }
                }
            }

        //SALES TERMINAL
            // arrow left, moves price down
            if(mouseOver(mx, my, 1089,564,17,17)){
                handler.object.get(4).pDown();
            }
            // arrow right, moves price up
            if(mouseOver(mx, my, 1192,564,17,17)){
                handler.object.get(4).pUp();
            }
    }

    public void mouseReleased(MouseEvent e){

    }

    //gets mouse coordinates at time of click
    private boolean mouseOver(int mx, int my, int x, int y, int width, int height){
        if(mx > x && mx < x + width){
            if(my > y && my < y + height){
                return true;
            }
            else return false;
        }else return false;
    }

    //----------------------------------------------------------------------------------
    //GRAPHICS
    public void render(Graphics g){

        //instantiate fonts
        Font hudFont = new Font("arial", 1, 22);
        Font incomeFont = new Font("arial",1,14);
        Color nearWhite = new Color(235,235,235);
        Font creditsFont = new Font("arial", 1, 18);
        Font bannerTitle = new Font("arial", 1, 20);

        
        //TITLE BANNER, CREDITS
            g.setColor(Color.black);
            g.fillRect(0,0,1280,35);
            g.setFont(bannerTitle);
           
            g.setColor(Color.lightGray);
            g.drawString("GRAYTECH: OPERATIONS MANAGEMENT SIMULATION",20,25);

            g.setFont(creditsFont);
            g.setColor(Color.gray);
            g.drawString("Created by Carson Gray",1000,23);

        //BANK ACCOUNT FRAME
            g.setColor(Color.black);            //black outline
            g.fillRect(578, 56,658, 98);
            g.setColor(Color.white);            //white interior for bank & clock
            g.fillRect(582, 60, 650,90);
            g.setColor(Color.black);            //vertical black divider
            g.fillRect(860,60,4,90);
            g.setColor(nearWhite);              //light gray interior for income report
            g.fillRect(864,60,368,90);
            
        //BANK AND CLOCK
            g.setFont(hudFont);
            g.setColor(Color.black);
            g.drawString("BANK: $" + money, 590, 94);
            g.drawString("TIME: " + day + " Days, " + hour + " Hours", 590, 130);

        //INCOME REPORT
            g.setFont(creditsFont);
            g.setColor(Color.darkGray);
            g.drawString("INCOME REPORT", 871,82);
            
            g.setFont(incomeFont);
            g.drawString("Sales Revenue:  $" + salesRev, 871,102);
            g.drawString("Total Expenses: $" + totalExp, 871,120);
            g.drawString("Net Income:        $" + netIncome, 871,142);
            g.drawString("Direct Materials:  $" + dmExp, 1051,84);
            g.drawString("Storage Costs:    $" + storageExp, 1051,102);
            g.drawString("Upgrade Costs:   $" + upgradeCost, 1051,120);
            g.drawString("Total Expenses:  $" + totalExp, 1051,142);
            
            //the summation lines
            g.fillRect(871,125,158,2);
            g.fillRect(1051,125,160,2);
                
    }

}