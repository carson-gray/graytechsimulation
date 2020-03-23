import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Printer3d extends GameObject {

    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    // this class encompasses the direct materials inventory, and the 3d printer itself
    //takes in one unit of input, outputs 4 units based on a player-set output ratio
    //it is located on the left side of the screen

    private int dmInv = 100;                            //STARTING DM INVENTORY
    
    final private String DAILY_STORAGE = "0.20";        //DM storage cost, 1/5
    final private int STORAGE_DENOM = 5;
    private int storageCharge = 0;

    private int cycleTime = 10;                                                     //cycle time in HALF HOURS
    private String cycleTimePrinted = (cycleTime / 2) + "." + (cycleTime % 2 * 5);  //printing cycle time in hour decimal format
    
    private int upgradeCost = 300;                                                  //cost of each upgrade
    final int CT_UPGRADE = 200;                                                      //increased cost for each successive upgrade
    
    private int availableCycleUpgrades = cycleTime - 1;                             //number of available upgrades
    private int cycleTimesUpgraded = 0;                                                  

    private int mRatio = 2;         //initializes the output ratio to be even
    private int mCount = 2;         //ratio is displayed, count is internal per production run
    private int eRatio = 2;
    private int eCount = 2;
    
    
    private int halfHour = 0;       //timing, uses half hours instead of hours
    private int pacer = 0;
    
    private int startTime = 0;          //internal production tracker
    private boolean working = false;
    
    public Printer3d(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler);
    }

//--------------------------------------------------------

    //OUTPUT RATIO controls
    //only 2 are needed, but used 4 for ease of reading

    public void mUp(){              //add to red
        if(mRatio + 1 <= 4){
            mRatio++;
            eRatio--;
        }
    }
    public void mDown(){            //take from red
        if(mRatio - 1 >= 0){
            mRatio--;
            eRatio++;
        }
    }
    public void eUp(){              //add to blue
        if(eRatio + 1 <= 4){
            eRatio++;
            mRatio--;
        }
    }
    public void eDown(){            //take from blue
        if(eRatio - 1 >= 0){
            eRatio--;
            mRatio++;
        }
    }

//--------------------------------------------------------

    //upgrading the cycle time

    public int getCycleUpgradeCost(){                           //getting price of upgrade
        return upgradeCost;
    }

    public boolean cycleUpgradeCheck(){                         //checking availability of upgrade
        if (cycleTimesUpgraded < availableCycleUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    public void cycleUpgrade(){                                 //perform the upgrade
        cycleTime--;
        cycleTimesUpgraded++;
        upgradeCost += CT_UPGRADE;

        if(cycleTimesUpgraded == availableCycleUpgrades){       //turn value to 0 once all upgrades completed
            upgradeCost = 0;
        }

        cycleTimePrinted = (cycleTime / 2) + "." + (cycleTime % 2 * 5);         //reprint new cycle time
    }

    public void orderDelivery(int newDM){
        dmInv += newDM;
    }

//----------------------------------------------------------------------------------

    public void tick() {

        //3D PRINTER USES HALF HOURS, NOT FULL HOURS
        pacer++;
        if(pacer == handler.getHourThreshold()/2){
            pacer = 0;
            halfHour++;

            //starting a production cycle
            if(dmInv > 0 && working == false){
                //takes the output ratio at the time of production start
                mCount = mRatio;
                eCount = eRatio;
                
                startTime = halfHour;
                dmInv--;
                working = true;
            }

            //ending a production cycle
            if(working){
                if(halfHour - startTime >= cycleTime){
                    output();
                    working = false;
                }
            }
        }

    }

    private void output(){
        for(int i = 0; i < mCount; i++){
            handler.object.get(2).sendInput();      //sending to mechanical queue
        }
        for(int i = 0; i < eCount; i++){
            handler.object.get(3).sendInput();      //sending to electronics queue
        }
    }

    //this is the storage cost of the DM Inventory
    public int charge(){
        storageCharge = dmInv / STORAGE_DENOM;
        return storageCharge;
    }

//--------------------------------------------------------

    //rendering the graphics
    public void render(Graphics g) {

        Font titleFnt = new Font("arial", 1, 18);
        Font bodyFnt = new Font("arial", 1, 13);
        Font ptFont = new Font("arial", 1, 16);
        Color nearWhite = new Color(235,235,235);

        //DIRECT MATERIALS
            //DM INVENTORY FRAME
                g.setColor(Color.black);
                g.fillRect(16, 214, 220, 130);
                g.setColor(Color.white);
                g.fillRect(20, 218, 212, 122);
            
                g.setColor(Color.black);
                g.fillRect(50,255,152,44);
                g.setColor(nearWhite);
                g.fillRect(52,257,148,40);
        
            //DM INVENTORY BODY
                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.drawString("DIRECT MATERIALS", 38, 243);
                g.drawString(dmInv + " Units", 90, 283);
                g.setFont(bodyFnt);
                g.setColor(Color.darkGray);
                g.drawString("Storage: $" + DAILY_STORAGE + " / Unit / Day", 53, 322);
        
        //3D PRINTER
            //3D PRINTER FRAME
                g.setColor(Color.black);
                g.fillRect(16, 404, 220, 250);
                g.setColor(Color.white);
                g.fillRect(20, 408, 212, 242);
                g.setColor(Color.yellow);
                g.fillRect(20, 408, 212, 37);
                g.setColor(Color.black);
                g.fillRect(16, 445, 220, 4);
                g.setColor(Color.black);
                g.fillRect(146, 445, 4, 209);
                g.setColor(nearWhite);
                g.fillRect(150, 449,82, 201);

                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.drawString("3D PRINTER", 68, 434);

            //LEFT PORTION OF BODY

                g.setColor(Color.black);
                g.fillRect(38,548,88,40);
                g.setColor(nearWhite);
                g.fillRect(40,550,84,36);

                g.setColor(Color.darkGray);
                g.setFont(bodyFnt);
                g.drawString("IN: 1 Unit of DM", 24, 467);
                g.drawString("OUT: 4 Units of", 24, 485);
                g.drawString("Red & Blue based", 24, 499);
                g.drawString("on Output Ratio", 24, 513);

                g.setColor(Color.black);
                g.setFont(ptFont);
                g.drawString("Process Time", 32, 540);
                g.drawString(cycleTimePrinted + " hrs", 58, 575);

            //UPGRADE BUTTON
                g.setColor(Color.black);
                g.fillRect(30,606,108,34);
                g.setColor(Color.green);
                g.fillRect(27,603,108,34);

                g.setColor(Color.black);
                g.setFont(bodyFnt);
                g.drawString("Upgrade: $" + upgradeCost, 34, 624);

            //RED PRODUCTION
                g.setColor(Color.red);
                g.fillRect(160,494,61,40);
                g.setColor(Color.white);
                g.fillRect(162,496,57,36);
                g.setColor(Color.red);
                g.setFont(titleFnt);
                g.drawString(mRatio + " m", 175, 520);

                g.setColor(Color.black);
                g.fillPolygon(new int[] {165, 181, 181}, new int[] {547,539,555}, 3);
                g.fillPolygon(new int[] {217, 202, 202}, new int[] {547,539,555}, 3);

                g.setColor(Color.red);
                g.fillPolygon(new int[] {164, 180, 180}, new int[] {546,538,554}, 3);
                g.fillPolygon(new int[] {216, 201, 201}, new int[] {546,538,554}, 3);

            //BLUE PRODUCTION
                g.setColor(Color.blue);
                g.fillRect(160,579,61,40);
                g.setColor(Color.white);
                g.fillRect(162,581,57,36);
                g.setColor(Color.blue);
                g.setFont(titleFnt);
                g.drawString(eRatio + " e", 178, 605);

                g.setColor(Color.black);
                g.fillPolygon(new int[] {165, 181, 181}, new int[] {634,626,642}, 3);
                g.fillPolygon(new int[] {217, 202, 202}, new int[] {634,626,642}, 3);

                g.setColor(Color.blue);
                g.fillPolygon(new int[] {164, 180, 180}, new int[] {633,625,641}, 3);
                g.fillPolygon(new int[] {216, 201, 201}, new int[] {633,625,641}, 3);

            //PRODUCTION RATIO TITLE
                g.setColor(Color.darkGray);
                g.setFont(bodyFnt);
                g.drawString("Output", 160, 470);
                g.drawString("Ratio:", 160, 485);
                g.drawString("and", 161, 571);
        

        //ARROWS (the 4 on left side of window, right side arrows are in final assembly)
            //YELLOW ARROW ORDER TO DM
                g.setColor(Color.black);
                g.fillPolygon(new int[] {113, 128, 143}, new int[] {176,204,176}, 3);
                g.fillRect(125,165,6,11);
            
                g.setColor(Color.yellow);
                g.fillRect(123,163,6,11);
                g.fillPolygon(new int[] {111, 126, 141}, new int[] {174,202,174}, 3);

            //YELLOW ARROW DM TO 3D
                g.setColor(Color.black);
                g.fillPolygon(new int[] {113, 128, 143}, new int[] {366,394,366}, 3);
                g.fillRect(125,355,6,11);
            
                g.setColor(Color.yellow);
                g.fillRect(123,353,6,11);
                g.fillPolygon(new int[] {111, 126, 141}, new int[] {364,392,364}, 3);

            //RED ARROW 3D TO MECH
                g.setColor(Color.black);
                g.fillRect(246, 514, 18, 6);
                g.fillRect(258, 291, 6, 223);
                g.fillRect(264, 291, 12, 6);
                g.fillPolygon(new int[] {276, 304, 276}, new int[] {279, 294, 309}, 3);

                g.setColor(Color.red);
                g.fillRect(244, 512, 18, 6);
                g.fillRect(256, 289, 6, 223);
                g.fillRect(262, 289, 12, 6);
                g.fillPolygon(new int[] {274, 302, 274}, new int[] {277, 292, 307}, 3);

            //BLUE ARROW FROM 3D TO ELEC    
                g.setColor(Color.black);
                g.fillRect(246, 595, 30, 6);
                g.fillPolygon(new int[] {276, 304, 276}, new int[] {584, 599, 614}, 3);   

                g.setColor(Color.blue);
                g.fillRect(244, 593, 30, 6);
                g.fillPolygon(new int[] {274, 302, 274}, new int[] {582, 597, 612}, 3);
    
    }

    //GameObject methods that weren't used above (for compilation purposes)
    public int getCost(){return 0;}
    public int getStationUpgradeCost(){return 0;}
    public void stationUpgrade(){}
    public boolean stationUpgradeCheck(){return true;}
    public void addToOrder(){}
    public void takeFromOrder(){}
    public void reset(){}
    public void shipOrder(int a){}
    public int getOrderSize(){return 0;}
    public void sendInput(){}
    public int getOutputQueue(){return 0;}
    public void pullOutput(int a){}
    public void pUp(){}
    public void pDown(){}
    public int sell(){return 0;}
    public int getUnitSales(){return 0;}
}