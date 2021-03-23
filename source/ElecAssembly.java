import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ElecAssembly extends GameObject {

    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    //mechanical and electronics assembly function identically
    //see mechanical for more rigorous comments

    //INPUTS AND OUTPUTS
    private int inputQueue = 0;                     //queue on the left
    private int outputQueue = 0;                    //queue on the right

    private int stationInput = 2;                   //input reqs
    private int stationOutput = 1;                  //output per production process

    private int queueSizeVisual = 4;                //match to mechanical's value

    //CYCLE TIME
    private int cycleTime = 12;                      //hours per process, initial
    private int cycleTimeUpgradeCost = 400;         //cost of upgrade

    private int cycleTimeUpgradeIncrement = 2;      //how much cycle time improves each upgrade
    private int cycleTimeDeltaUpgradeCost = 250;     //the increased cost of each subsequent upgrade
    
    private int cycleTimesUpgraded = 0;             //for internal upgrade tracking
    private int availableCycleUpgrades = 5;

    //NUMBER OF STATIONS
    private int numStations = 1;                    //number of concurrent stations
    private int numStationsUpgradeCost = 2500;       //cost per station purchase

    private int numStationsDeltaUpgradeCost = 1500;  //increased cost per station upgrade
    
    private int stationTimesUpgraded = 0;           //internal use
    private int availableStationUpgrades = 3;       //DO NOT CHANGE THIS
    
    //TIMING, PRODUCTION
    private int hour = 0;
    private int pacer = 0;

    private int startTime_1 = 0;            //start times for each station
    private int startTime_2 = 0;
    private int startTime_3 = 0;
    private int startTime_4 = 0;
    private boolean working_1 = false;      //tracks station production
    private boolean working_2 = false;
    private boolean working_3 = false;
    private boolean working_4 = false;
    
    public ElecAssembly(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler);
    }

//-------------------------------------------------------------------------

    public void tick() {

        //LOCAL PACER
        pacer++;
        if(pacer == handler.getHourThreshold()){
            pacer = 0;
            hour++;

            //STATION 1 (default)
            if(numStations >= 1){
                //starting a production cycle
                if(inputQueue >= stationInput && working_1 == false){
                    startTime_1 = hour;
                    inputQueue -= stationInput;
                    working_1 = true;
                }

                //ending a production cycle
                if(working_1){
                    if(hour - startTime_1 >= cycleTime){
                        outputQueue+= stationOutput;
                        working_1 = false;
                    }
                }
            }

            //STATION 2 (subsequent stations require an upgrade)
            if(numStations >= 2){
                //starting a production cycle
                if(inputQueue >= stationInput && working_2 == false){
                    startTime_2 = hour;
                    inputQueue -= stationInput;
                    working_2 = true;
                }

                //ending a production cycle
                if(working_2){
                    if(hour - startTime_2 >= cycleTime){
                        outputQueue+= stationOutput;
                        working_2 = false;
                    }
                }
            }

            //STATION 3
            if(numStations >= 3){
                //starting a production cycle
                if(inputQueue >= stationInput && working_3 == false){
                    startTime_3 = hour;
                    inputQueue -= stationInput;
                    working_3 = true;
                }

                //ending a production cycle
                if(working_3){
                    if(hour - startTime_3 >= cycleTime){
                        outputQueue+= stationOutput;
                        working_3 = false;
                    }
                }
            }

            //STATION 4
            if(numStations >= 4){
                //starting a production cycle
                if(inputQueue >= stationInput && working_4 == false){
                    startTime_4 = hour;
                    inputQueue -= stationInput;
                    working_4 = true;
                }

                //ending a production cycle
                if(working_4){
                    if(hour - startTime_4 >= cycleTime){
                        outputQueue+= stationOutput;
                        working_4 = false;
                    }
                }
            }
        }

    }

    //--------------------------------------------------------------------

    //QUEUE MANAGEMENT
    public int getOutputQueue(){            //used by final assembly
        return outputQueue;
    }

    public void pullOutput(int myInput){    //used by final assembly
        outputQueue -= myInput;
    }

    public void sendInput(){                //used by 3d printer
        inputQueue++;
    }

    //UPGRADE MANAGEMENT
    public int getCycleUpgradeCost(){       //used by hud
        return cycleTimeUpgradeCost;
    }

    public int getStationUpgradeCost(){     //used by hud
        return numStationsUpgradeCost;
    }

    public boolean cycleUpgradeCheck(){     //availability check
        if (cycleTimesUpgraded < availableCycleUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    public boolean stationUpgradeCheck(){   //availability check
        if (stationTimesUpgraded < availableStationUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    //perform the upgrades themselves
    public void cycleUpgrade(){
        cycleTime -= cycleTimeUpgradeIncrement;
        cycleTimeUpgradeCost += cycleTimeDeltaUpgradeCost;
        cycleTimesUpgraded++;

        if(cycleTimesUpgraded == availableCycleUpgrades){
            cycleTimeUpgradeCost = 0;
        }
    }

    public void stationUpgrade(){
        numStations++;
        numStationsUpgradeCost += numStationsDeltaUpgradeCost;
        stationTimesUpgraded++;

        if(stationTimesUpgraded == availableStationUpgrades){
            numStationsUpgradeCost = 0;         //0 indicates that all upgrades have taken place
        }
    }

    //------------------------------------------------------------------------

    public void render(Graphics g) {

        Font titleFnt = new Font("arial", 1, 18);
        Font ptFont = new Font("arial", 1, 16);
        Font bodyFnt = new Font("arial", 1, 13);
        Color nearWhite = new Color(235,235,235);
       
        //CREATE THE FRAME
        g.setColor(Color.black);
        g.fillRect(396, 434, 332, 220);
        g.setColor(Color.white);
        g.fillRect(400, 438, 324, 212);
        g.setColor(Color.black);
        g.fillRect(396,475,332,4);
        
        //BLUE BAR AND TITLE
        g.setColor(Color.blue);
        g.fillRect(400,438,324,37);
        g.setColor(Color.white);
        g.setFont(titleFnt);
        g.drawString("ELECTRONICS ASSEMBLY", 444,463);

        //------------------------------------------------------

        //INPUT QUEUE VISUAL BAR
        g.setColor(Color.blue);             //blue outline
        g.fillRect(322,624,60,30);
        g.setColor(Color.blue);
        g.fillRect(322,434,60,190);
        
        g.setColor(Color.black);            //background
        g.fillRect(324,436,56,188);
        g.setColor(nearWhite);              //color of base
        g.fillRect(324,626,56,26);
        g.setColor(Color.gray);             //color of bar

        g.fillRect(324, Game.clamp(624 - queueSizeVisual * inputQueue, 436, 624), 56, Game.clamp(queueSizeVisual * inputQueue, 0, 188));
            //creates the visual, moving bar, clamped into avaialable space, filled by queue size
        
        g.setColor(Color.darkGray);
        g.setFont(bodyFnt);
        g.drawString("Queue", 331,643);     //title at base of bar
        g.setColor(Color.white);
        g.setFont(ptFont);
        g.drawString(inputQueue + " e", 334,600);   //value printed in bar itself

        //OUTPUT QUEUE VISUAL BAR
        //see above for the comments
        g.setColor(Color.blue);
        g.fillRect(742,624,60,30);
        g.setColor(Color.blue);
        g.fillRect(742,434,60,190);
        
        g.setColor(Color.black);
        g.fillRect(744,436,56,188);
        g.setColor(nearWhite);
        g.fillRect(744,626,56,26);
        g.setColor(Color.gray);
        
        g.fillRect(744, Game.clamp(624 - queueSizeVisual * outputQueue, 436, 624), 56, Game.clamp(queueSizeVisual * outputQueue, 0, 188));
        
        g.setColor(Color.darkGray);
        g.setFont(bodyFnt);
        g.drawString("Queue", 751,643);
        g.setColor(Color.white);
        g.setFont(ptFont);
        g.drawString(outputQueue + " E", 752,600);

//-----------------------------------------------------------------------------------

        //INPUT AND OUTPUT BOXES
        g.setColor(Color.black);
        g.fillRect(396,607,332,4);
        g.setColor(nearWhite);
        g.fillRect(400,611,324,39);
        g.setColor(Color.black);
        g.setFont(titleFnt);
        g.drawString("INPUT:", 415, 636);
        g.drawString("OUTPUT:", 560, 636);
        
        g.setColor(Color.blue);     //input
        g.fillRect(484,615,60,30);
        g.setColor(Color.white);
        g.fillRect(486,617,56,26);
        g.setColor(Color.blue);
        g.drawString(stationInput + " e", 500, 636);

        g.setColor(Color.blue);     //output
        g.fillRect(649,615,60,30);
        g.setColor(Color.white);
        g.fillRect(651,617,56,26);
        g.setColor(Color.blue);
        g.drawString(stationOutput + " E", 663, 636);

        //----------------------------------------------------------

        //CYCLE TIME
                //TITLE, BOX
                g.setColor(Color.black);
                g.fillRect(419,509,88,40);
                g.setColor(nearWhite);
                g.fillRect(421,511,84,36);

                g.setColor(Color.black);
                g.setFont(ptFont);
                g.drawString("Process Time", 413, 501);
                g.setFont(bodyFnt);
                g.drawString(cycleTime + " hours", 437, 534);

            //UPGRADE BUTTON
                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.fillRect(411,562,108,34);
                g.setColor(Color.green);
                g.fillRect(408,559,108,34);

                g.setColor(Color.black);
                g.setFont(bodyFnt);
                g.drawString("Upgrade: $" + cycleTimeUpgradeCost, 415, 580);
        
        //NUMBER OF STATIONS
            //TITLE, BOX
                g.setColor(Color.black);
                g.fillRect(540,509,88,40);
                g.setColor(nearWhite);
                g.fillRect(542,511,84,36);

                g.setColor(Color.black);
                g.setFont(titleFnt);
                g.drawString("# of Stations", 528, 501);
                g.setFont(bodyFnt);
                g.drawString(numStations + " station(s)", 550, 534);

            //UPGRADE BUTTON
                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.fillRect(532,562,108,34);
                g.setColor(Color.green);
                g.fillRect(529,559,108,34);

                g.setColor(Color.black);
                g.setFont(bodyFnt);
                g.drawString("Upgrade: $" + numStationsUpgradeCost, 536, 580);

    //------------------------------------------------------------------------------

        //Production Tracker (4 gray bars on side)

            //default bar
            g.setColor(Color.lightGray);
            g.fillRect(650,488,12,108);
            if(working_1){
                g.setColor(Color.blue);         //turns blue when in production
                g.fillRect(650,488,12,108);
            }

            //initialize three lighter bars
            g.setColor(nearWhite);            
            g.fillRect(668,488,12,108);
            g.fillRect(686,488,12,108);
            g.fillRect(704,488,12,108);

            //graphics of second bar once purchased
            if(numStations >= 2){
                g.setColor(Color.lightGray);           
                g.fillRect(668,488,12,108);

                if(working_2){
                    g.setColor(Color.blue);
                    g.fillRect(668,488,12,108);
                }
            }

            //third bar
            if(numStations >= 3){
                g.setColor(Color.lightGray);
                g.fillRect(686,488,12,108);

                if(working_3){
                    g.setColor(Color.blue);
                    g.fillRect(686,488,12,108);
                }
            }

            //fourth bar
            if(numStations >= 4){
                g.setColor(Color.lightGray);
                g.fillRect(704,488,12,108);

                if(working_4){
                    g.setColor(Color.blue);
                    g.fillRect(704,488,12,108);
                }
            }
    }

    //GameObject methods that weren't used above (for compilation purposes)
    public int getCost(){return 0;}
    public int charge(){return 0;}
    public void addToOrder(){}
    public void takeFromOrder(){}
    public void reset(){}
    public void shipOrder(int a){}
    public int getOrderSize(){return 0;}
    public void mUp(){}
    public void mDown(){}
    public void eUp(){}
    public void eDown(){}
    public void orderDelivery(int a){}
    public void pUp(){}
    public void pDown(){}
    public int sell(){return 0;}
    public int getUnitSales(){return 0;}
}