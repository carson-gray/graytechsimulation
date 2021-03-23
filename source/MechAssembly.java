import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MechAssembly extends GameObject {

    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    //mechanical and electronics assembly function identically

    //INPUTS AND OUTPUTS
    private int inputQueue = 0;                     //input queue is the queue going in
    private int outputQueue = 0;                    //output queue is mechanical component of final assembly queue (hosted by mech assembly)

    private int stationInput = 3;                   //required units from the 3d printer
    private int stationOutput = 2;                  //what is outputted to the output queue

    private int queueSizeVisual = 4;                //this is used for the visual queue bars, how tall each unit makes the bar
                                                    //a bigger number means the bar will visually fill up faster
    
    //CYCLE TIME
    private int cycleTime = 6;                      //initial cycle time
    private int cycleTimeUpgradeCost = 300;         //cost of first cycle time upgrade
    
    private int cycleTimeUpgradeIncrement = 1;      //how much cycle time improves each upgrade
    private int cycleTimeDeltaUpgradeCost = 200;     //the increased cost of each subsequent upgrade
    
    private int cycleTimesUpgraded = 0;             //times it has been upgraded (internal)
    private int availableCycleUpgrades = 5;         //times it can be upgraded
    
    //NUMBER OF STATIONS
    private int numStations = 1;                    //number of concurrently running stations
    private int numStationsUpgradeCost = 2500;       //cost to upgrade the number of stations

    private int numStationsDeltaUpgradeCost = 1500;  //increased cost per station upgrade
    
    private int stationTimesUpgraded = 0;           //number of times upgraded
    private int availableStationUpgrades = 3;       //available upgrades (DO NOT CHANGE THIS)
    
    //TIMING, PRODUCTION
    private int hour = 0;
    private int pacer = 0;

    private int startTime_1 = 0;                    //these are the start times for each station
    private int startTime_2 = 0;
    private int startTime_3 = 0;
    private int startTime_4 = 0;
    private boolean working_1 = false;              //these track whether or not each station is working
    private boolean working_2 = false;
    private boolean working_3 = false;
    private boolean working_4 = false;

    
    public MechAssembly(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler);
    }

//---------------------------------------------------------------------------------

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
                        outputQueue += stationOutput;
                        working_1 = false;
                    }
                }
            }

            //STATION 2 (all subsequent stations require an upgrade)
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
                        outputQueue += stationOutput;
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
                        outputQueue += stationOutput;
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
                        outputQueue += stationOutput;
                        working_4 = false;
                    }
                }
            }

        }

    }

//--------------------------------------------------------------------------

    //QUEUE MANAGEMENT
    public int getOutputQueue(){                //used by final assembly
        return outputQueue;
    }

    public void pullOutput(int myInput){        //used by final assembly
        outputQueue -= myInput;
    }

    public void sendInput(){                    //used by 3d printer
        inputQueue++;
    }

    //UPGRADE MANAGEMENT
    public int getCycleUpgradeCost(){           //used by hud
        return cycleTimeUpgradeCost;
    }

    public int getStationUpgradeCost(){         //used by hud
        return numStationsUpgradeCost;
    }

    public boolean stationUpgradeCheck(){
        if (stationTimesUpgraded < availableStationUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    //upgrade the cycle time
    public void cycleUpgrade(){
        cycleTime -= cycleTimeUpgradeIncrement;
        cycleTimeUpgradeCost += cycleTimeDeltaUpgradeCost;
        cycleTimesUpgraded++;

        if(cycleTimesUpgraded == availableCycleUpgrades){
            cycleTimeUpgradeCost = 0;       //0 indicates all upgrades have taken place
        }
    }

    public boolean cycleUpgradeCheck(){
        if (cycleTimesUpgraded < availableCycleUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    //upgrade the number of stations
    public void stationUpgrade(){
        numStations++;
        numStationsUpgradeCost += numStationsDeltaUpgradeCost;
        stationTimesUpgraded++;

        if(stationTimesUpgraded == availableStationUpgrades){
            numStationsUpgradeCost = 0;
        }
    }

//------------------------------------------------------------------

    public void render(Graphics g) {

        Font titleFnt = new Font("arial", 1, 18);
        Font ptFont = new Font("arial", 1, 16);
        Font bodyFnt = new Font("arial", 1, 13);
        Color nearWhite = new Color(235,235,235);
       
        //CREATE THE FRAME
        g.setColor(Color.black);
        g.fillRect(396, 184, 332, 220);
        g.setColor(Color.white);
        g.fillRect(400, 188, 324, 212);
        g.setColor(Color.black);
        g.fillRect(396,225,332,4);

        //CREATE THE RED BAR FOR THE TITLE
        g.setColor(Color.red);
        g.fillRect(400,188,324,37);
        g.setColor(Color.white);
        g.setFont(titleFnt);
        g.drawString("MECHANICAL ASSEMBLY", 445,213);

        //---------------------------------------------------------------------

        //INPUT QUEUE VISUAL BAR
        g.setColor(Color.red);              //outline
        g.fillRect(322,374,60,30);
        g.setColor(Color.red);
        g.fillRect(322,184,60,190);
        
        g.setColor(Color.black);            //background of bar
        g.fillRect(324,186,56,188);
        g.setColor(nearWhite);              //the color of the base
        g.fillRect(324,376,56,26);
        g.setColor(Color.gray);             //color of bar itself
        
        g.fillRect(324, Game.clamp(374 - queueSizeVisual * inputQueue, 186, 374), 56, Game.clamp(queueSizeVisual * inputQueue, 0, 188));
            //this creates the moving bar visual, clamped within the available space, with y values determined by the number of items in the queue
        
        g.setColor(Color.darkGray);
        g.setFont(bodyFnt);
        g.drawString("Queue", 331,393);     //title at base of bar
        g.setColor(Color.white);
        g.setFont(ptFont);
        g.drawString(inputQueue + " m", 333,350);   //the value printed over the bar

        //OUTPUT QUEUE VISUAL BAR
        //code is identical, see above comments
        g.setColor(Color.red);
        g.fillRect(742,374,60,30);
        g.setColor(Color.red);
        g.fillRect(742,184,60,190);
        
        g.setColor(Color.black);
        g.fillRect(744,186,56,188);
        g.setColor(nearWhite);
        g.fillRect(744,376,56,26);
        g.setColor(Color.gray);

        g.fillRect(744, Game.clamp(374 - queueSizeVisual * outputQueue, 186, 374), 56, Game.clamp(queueSizeVisual * outputQueue, 0, 188));
        
        g.setColor(Color.darkGray);
        g.setFont(bodyFnt);
        g.drawString("Queue", 751,393);
        g.setColor(Color.white);
        g.setFont(ptFont);
        g.drawString(outputQueue + " M", 751,350);

        //------------------------------------------------------------------------

        //INPUT AND OUTPUT BOXES
        g.setColor(Color.black);
        g.fillRect(396,357,332,4);
        g.setColor(nearWhite);
        g.fillRect(400,361,324,39);
        g.setColor(Color.black);
        g.setFont(titleFnt);
        g.drawString("INPUT:", 415, 386);
        g.drawString("OUTPUT:", 560, 386);
        
        g.setColor(Color.red);          //input
        g.fillRect(484,365,60,30);
        g.setColor(Color.white);
        g.fillRect(486,367,56,26);
        g.setColor(Color.red);
        g.drawString(stationInput + " m", 497, 386);

        g.setColor(Color.red);          //output
        g.fillRect(649,365,60,30);
        g.setColor(Color.white);
        g.fillRect(651,367,56,26);
        g.setColor(Color.red);
        g.drawString(stationOutput + " M", 663, 386);

        //-----------------------------------------------------------------------

        //CYCLE TIME
                //TITLE, BOX
                g.setColor(Color.black);
                g.fillRect(419,259,88,40);
                g.setColor(nearWhite);
                g.fillRect(421,261,84,36);

                g.setColor(Color.black);
                g.setFont(ptFont);
                g.drawString("Process Time", 413, 251);
                g.setFont(bodyFnt);
                g.drawString(cycleTime + " hours", 437, 284);

            //UPGRADE BUTTON
                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.fillRect(411,312,108,34);
                g.setColor(Color.green);
                g.fillRect(408,309,108,34);

                g.setColor(Color.black);
                g.setFont(bodyFnt);
                g.drawString("Upgrade: $" + cycleTimeUpgradeCost, 415, 330);
        
        //NUMBER OF STATIONS
            //TITLE, BOX
                g.setColor(Color.black);
                g.fillRect(540,259,88,40);
                g.setColor(nearWhite);
                g.fillRect(542,261,84,36);

                g.setColor(Color.black);
                g.setFont(titleFnt);
                g.drawString("# of Stations", 528, 251);
                g.setFont(bodyFnt);
                g.drawString(numStations + " station(s)", 550, 284);

            //UPGRADE BUTTON
                g.setFont(titleFnt);
                g.setColor(Color.black);
                g.fillRect(532,312,108,34);
                g.setColor(Color.green);
                g.fillRect(529,309,108,34);

                g.setColor(Color.black);
                g.setFont(bodyFnt);
                g.drawString("Upgrade: $" + numStationsUpgradeCost, 536, 330);

        //-------------------------------------------------------------------------

        //Production Tracker (4 gray bars on the right side)
            
            //default bar
            g.setColor(Color.lightGray);        //light gray means owned
            g.fillRect(650,238,12,108);
            if(working_1){
                g.setColor(Color.red);          //red means working
                g.fillRect(650,238,12,108);
            }

            //three extra bars start out as an even lighter gray to indicate availability of purchase
            g.setColor(nearWhite);
            g.fillRect(668,238,12,108);
            g.fillRect(686,238,12,108);
            g.fillRect(704,238,12,108);

            //graphics of second bar once purchased
            if(numStations >= 2){
                g.setColor(Color.lightGray);           
                g.fillRect(668,238,12,108);

                if(working_2){
                    g.setColor(Color.red);
                    g.fillRect(668,238,12,108);
                }
            }

            //graphics of third bar once purchased
            if(numStations >= 3){
                g.setColor(Color.lightGray);
                g.fillRect(686,238,12,108);

                if(working_3){
                    g.setColor(Color.red);
                    g.fillRect(686,238,12,108);
                }
            }

            //graphics of fourth bar once purchased
            if(numStations >= 4){
                g.setColor(Color.lightGray);
                g.fillRect(704,238,12,108);

                if(working_4){
                    g.setColor(Color.red);
                    g.fillRect(704,238,12,108);
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