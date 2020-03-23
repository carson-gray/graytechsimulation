import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//example object

public class FinalAssembly extends GameObject {

    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    //INPUTS AND OUTPUTS
    private int stationInputRed = 4;                //inputs from mech
    private int stationInputBlue = 3;               //inputs from elec
    private int stationOutput = 1;                  //output in fg
    
    //CYCLE TIME
    private int cycleTime = 16;                     //initial process time
    private int cycleTimeUpgradeCost = 1000;         //initial upgrade cost

    private int cycleTimeUpgradeIncrement = 2;      //how much cycle time improves each upgrade
    private int cycleTimeDeltaUpgradeCost = 750;    //the increased cost of each subsequent upgrade
    
    private int cycleTimesUpgraded = 0;             //times upgraded
    private int availableCycleUpgrades = 6;         //available upgrades

    //NUMBER OF STATIONS
    private int numStations = 1;                    //number of stations running
    private int numStationsUpgradeCost = 5000;       //cost of additional station

    private int numStationsDeltaUpgradeCost = 5000;  //cost of each successive upgrade
    
    private int stationTimesUpgraded = 0;           //for internal use
    private int availableStationUpgrades = 2;       //DON'T CHANGE
    
    //FINISHED GOODS STORAGE COST
    private int finishedGoodsInv = 0;               //results in a finished production process
    private int storageCost = 10;                   //charge per unit per day
    private int unitSales = 0;                      //tracks unit sales
    
    private int storageCharge = 0;                  //for storage charge calculations
    private int todaysRev = 0;                      //for sales calculations
    
    //SALES PRICE & DEMAND
    private int salesPrice = 180;                   //initial price
    private int dPrice = 20;                        //price change increments
    private int dailyDemand = 4;                    //demand for initial price
    private int dDemand = 2;                        //demand change increments
    
    //TIMING
    private int hour = 0;
    private int pacer = 0;

    //PRODUCTION
    private int startTime_1 = 0;        //start time for each station
    private int startTime_2 = 0;
    private int startTime_3 = 0;
    private boolean working_1 = false;  //tracks production status
    private boolean working_2 = false;
    private boolean working_3 = false;
    
    public FinalAssembly(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler);
    }

//--------------------------------------------------------------------
    
    public void tick() {

        //LOCAL PACER
        pacer++;
        if(pacer == handler.getHourThreshold()){
            pacer = 0;
            hour++;

            //STATION 1 (default)
            if(numStations >= 1){
                //starting a production cycle
                if(working_1 == false){
                    if(handler.object.get(2).getOutputQueue() >= stationInputRed && handler.object.get(3).getOutputQueue() >= stationInputBlue){
                        handler.object.get(2).pullOutput(stationInputRed);
                        handler.object.get(3).pullOutput(stationInputBlue);
                        startTime_1 = hour;
                        working_1 = true;
                    }
                }

                //ending a production cycle
                if(working_1){
                    if(hour - startTime_1 >= cycleTime){
                        finishedGoodsInv++;
                        working_1 = false;
                    }
                }
            }

            //STATION 2 (needs an upgrade to function)
            if(numStations >= 2){
                //starting a production cycle
                if(working_2 == false){
                    if(handler.object.get(2).getOutputQueue() >= stationInputRed && handler.object.get(3).getOutputQueue() >= stationInputBlue){
                        handler.object.get(2).pullOutput(stationInputRed);
                        handler.object.get(3).pullOutput(stationInputBlue);
                        startTime_2 = hour;
                        working_2 = true;
                    }
                }

                //ending a production cycle
                if(working_2){
                    if(hour - startTime_2 >= cycleTime){
                        finishedGoodsInv++;
                        working_2 = false;
                    }
                }
            }

            //STATION 3
            if(numStations >= 3){
                //starting a production cycle
                if(working_3 == false){
                    if(handler.object.get(2).getOutputQueue() >= stationInputRed && handler.object.get(3).getOutputQueue() >= stationInputBlue){
                        handler.object.get(2).pullOutput(stationInputRed);
                        handler.object.get(3).pullOutput(stationInputBlue);
                        startTime_3 = hour;
                        working_3 = true;
                    }
                }

                //ending a production cycle
                if(working_3){
                    if(hour - startTime_3 >= cycleTime){
                        finishedGoodsInv++;
                        working_3 = false;
                    }
                }
            }

        }

    }

    //-----------------------------------------------------------

    //UPGRADE MANAGEMENT
    
    public int getCycleUpgradeCost(){
        return cycleTimeUpgradeCost;
    }

    public int getStationUpgradeCost(){
        return numStationsUpgradeCost;
    }

    public void cycleUpgrade(){
        cycleTime -= cycleTimeUpgradeIncrement;
        cycleTimeUpgradeCost += cycleTimeDeltaUpgradeCost;
        cycleTimesUpgraded++;

        if(cycleTimesUpgraded == availableCycleUpgrades){
            cycleTimeUpgradeCost = 0;
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

    public void stationUpgrade(){
        numStations++;
        numStationsUpgradeCost += numStationsDeltaUpgradeCost;
        stationTimesUpgraded++;

        if(stationTimesUpgraded == availableStationUpgrades){
            numStationsUpgradeCost = 0;
        }
    }
    
    public boolean stationUpgradeCheck(){
        if (stationTimesUpgraded < availableStationUpgrades){
            return true;
        }

        else{
            return false;
        }
    }

    //------------------------------------------------------------------

    //SALES TERMINAL
    
    public void pUp(){                      //increase price, used by arrows
        if(dailyDemand - dDemand >= 0){
            salesPrice += dPrice;
            dailyDemand -= dDemand;
        }
    }

    public void pDown(){                    //decrease price, used by arrows
        if(salesPrice - dPrice > 0){
            salesPrice -= dPrice;
            dailyDemand += dDemand;
        }
    }

    public int charge(){                    //storage charge, used by hud
        storageCharge = finishedGoodsInv * storageCost;
        return storageCharge;
    }

    public int sell(){                      //sale takes place, used by hud
        todaysRev = salesPrice * Math.min(dailyDemand, finishedGoodsInv);   //sells min of fg or daily demand
        unitSales += Math.min(dailyDemand, finishedGoodsInv);
        finishedGoodsInv -= Math.min(dailyDemand, finishedGoodsInv);
        return todaysRev;
    }

    public int getUnitSales(){
        return unitSales;
    }

//------------------------------------------------------------------------------------

    public void render(Graphics g) {

    //FINAL ASSEMBLY
        //FINAL ASSEMBLY FRAME
            Font titleFnt = new Font("arial", 1, 18);
            Font bodyFnt = new Font("arial", 1, 13);
            Font salesFnt = new Font("arial", 1, 16);
            Color myPurple = new Color (221, 0, 255);
            Color nearWhite = new Color(235,235,235);
    
            g.setColor(Color.black);
            g.fillRect(888, 184, 348, 250);
            g.setColor(Color.white);
            g.fillRect(892, 188, 340, 242);
            g.setColor(Color.black);
            g.fillRect(888, 225, 348, 4);
            g.setColor(Color.black);
            g.fillRect(978, 360, 258, 4);
            g.setColor(myPurple);
            g.fillRect(892, 188, 340, 37);

            g.setFont(titleFnt);
            g.setColor(Color.white);
            g.drawString("FINAL ASSEMBLY", 984, 213);

            g.setColor(Color.black);
            g.fillRect(974, 225, 4, 209);
            g.setColor(nearWhite);
            g.fillRect(892, 229, 82, 201);

        //FINAL ASSEMBLY LEFTHAND INPUT/OUTPUT LIST
            //INPUT RATIO TITLE
                g.setColor(Color.darkGray);
                g.setFont(bodyFnt);
                g.drawString("Inputs:", 902, 250);
                g.drawString("Output:", 902, 371);

            //RED INPUT BOX
                g.setColor(Color.red);
                g.fillRect(902,259,61,40);
                g.setColor(Color.white);
                g.fillRect(904,261,57,36);
                g.setColor(Color.red);
                g.setFont(titleFnt);
                g.drawString(stationInputRed + " M", 918, 285);

            //BLUE INPUT BOX
                g.setColor(Color.blue);
                g.fillRect(902,309,61,40);
                g.setColor(Color.white);
                g.fillRect(904,311,57,36);
                g.setColor(Color.blue);
                g.setFont(titleFnt);
                g.drawString(stationInputBlue + " E", 918, 334);

            //PURPLE OUTPUT BOX
                g.setColor(myPurple);
                g.fillRect(902,380,61,40);
                g.setColor(Color.white);
                g.fillRect(904,382,57,36);
                g.setColor(Color.darkGray);
                g.setFont(titleFnt);
                g.drawString(stationOutput + " FG", 913, 405);

        //RIGHTHAND BODY
            //CYCLE TIME
                //TITLE, BOX
                    g.setColor(Color.black);
                    g.fillRect(997,259,88,40);
                    g.setColor(nearWhite);
                    g.fillRect(999,261,84,36);

                    g.setColor(Color.black);
                    g.setFont(salesFnt);
                    g.drawString("Process Time", 991, 251);
                    g.setFont(bodyFnt);
                    g.drawString(cycleTime + " hours", 1016, 284);

                //UPGRADE BUTTON
                    g.setFont(titleFnt);
                    g.setColor(Color.black);
                    g.fillRect(989,312,108,34);
                    g.setColor(Color.green);
                    g.fillRect(986,309,108,34);

                    g.setColor(Color.black);
                    g.setFont(bodyFnt);
                    g.drawString("Upgrade: $" + cycleTimeUpgradeCost, 993, 330);
            
            //NUMBER OF STATIONS
                //TITLE, BOX
                    g.setColor(Color.black);
                    g.fillRect(1120,259,88,40);
                    g.setColor(nearWhite);
                    g.fillRect(1122,261,84,36);

                    g.setColor(Color.black);
                    g.setFont(titleFnt);
                    g.drawString("# of Stations", 1108, 251);
                    g.setFont(bodyFnt);
                    g.drawString(numStations + " station(s)", 1130, 284);

                //UPGRADE BUTTON
                    g.setFont(titleFnt);
                    g.setColor(Color.black);
                    g.fillRect(1112,312,113,34);
                    g.setColor(Color.green);
                    g.fillRect(1109,309,113,34);

                    g.setColor(Color.black);
                    g.setFont(bodyFnt);
                    g.drawString("Upgrade: $" + numStationsUpgradeCost, 1114, 330);

//----------------------------------------------------------------------

        //PRODUCTION TRACKER

            //station 1
            g.setColor(Color.lightGray);
            g.fillRect(990, 372, 230, 13);
            if(working_1){
                g.setColor(myPurple);           //purple indicates production in process
                g.fillRect(990, 372, 230, 13);
            }

            //initialize 2 available stations
            g.setColor(nearWhite);
            g.fillRect(990, 391, 230, 13);
            g.fillRect(990, 410, 230, 13);
            
            //station 2 once purchased
            if(numStations >= 2){
                g.setColor(Color.lightGray);
                g.fillRect(990, 391, 230, 13);

                if(working_2){
                    g.setColor(myPurple);
                    g.fillRect(990, 391, 230, 13);
                }
            }

            //station 3 once purchased
            if(numStations >= 3){
                g.setColor(Color.lightGray);
                g.fillRect(990, 410, 230, 13);

                if(working_3){
                    g.setColor(myPurple);
                    g.fillRect(990, 410, 230, 13);
                }
            }
    
    //----------------------------------------------------------------

    //SALES TRACKER
        //SALES FRAME
            g.setColor(Color.black);            //overall frame
            g.fillRect(888, 494, 348, 160);
            g.setColor(Color.white);
            g.fillRect(892, 498, 340, 152);

            g.setColor(Color.black);            //vertical dividing line
            g.fillRect(1062, 494, 4, 160);
            g.setColor(nearWhite);
            g.fillRect(1066, 498, 164,152);

            g.setColor(Color.black);            //finished goods box
            g.fillRect(915,526,120,32);
            g.setColor(nearWhite);
            g.fillRect(917,528,116,28);

            g.setColor(Color.black);            //unit sales box
            g.fillRect(915,610,120,32);
            g.setColor(nearWhite);
            g.fillRect(917,612,116,28);

            g.setColor(myPurple);               //sales price box
            g.fillRect(1089,526,120,32);
            g.setColor(Color.white);
            g.fillRect(1091,528,116,28);

            g.setColor(myPurple);               //daily demand box
            g.fillRect(1089,610,120,32);
            g.setColor(Color.white);
            g.fillRect(1091,612,116,28);

            g.setFont(salesFnt);
            g.setColor(Color.black);
            g.drawString("FINISHED GOODS",907,518);
            g.drawString("GOODS SOLD",920,603);
            g.drawString("SALES PRICE",1094,518);
            g.drawString("DAILY DEMAND",1088,603);
            
            g.setFont(bodyFnt);
            g.setColor(Color.darkGray);
            g.drawString("Storage: $" + storageCost + " / Unit / Day", 902, 577);
            g.drawString("PRICE (+/-)", 1114, 577);
            
            g.setFont(salesFnt);
            g.drawString("$ " + salesPrice, 1123, 548);
            g.drawString(dailyDemand + " Units", 1123, 632);
            g.drawString(finishedGoodsInv + " Units", 945, 548);
            g.drawString(unitSales + " Units", 942, 632);

            //arrows to control price
            g.setColor(Color.black);
            g.fillPolygon(new int[] {1090, 1106, 1106}, new int[] {573,565,581}, 3);
            g.fillPolygon(new int[] {1209, 1193, 1193}, new int[] {573,565,581}, 3);

            g.setColor(myPurple);
            g.fillPolygon(new int[] {1089, 1105, 1105}, new int[] {572,564,580}, 3);
            g.fillPolygon(new int[] {1208, 1192, 1192}, new int[] {572,564,580}, 3);


    //ARROWS FROM MID TO FINAL
        //RED ARROW FROM MECH TO FINAL   
            g.setColor(Color.black);
            g.fillRect(812, 280, 30, 6);
            g.fillPolygon(new int[] {842, 870, 842}, new int[] {268, 283, 298}, 3);

            g.setColor(Color.red);
            g.fillRect(810, 278, 30, 6);
            g.fillPolygon(new int[] {840, 868, 840}, new int[] {266, 281, 296}, 3);

        //BLUE ARROW FROM ELEC TO FINAL
            g.setColor(Color.black);
            g.fillRect(812, 554, 18, 6);
            g.fillRect(824, 331, 6, 223);
            g.fillRect(830, 331, 12, 6);
            g.fillPolygon(new int[] {842, 870, 842}, new int[] {319, 334, 349}, 3);

            g.setColor(Color.blue);
            g.fillRect(810, 552, 18, 6);
            g.fillRect(822, 329, 6, 223);
            g.fillRect(828, 329, 12, 6);
            g.fillPolygon(new int[] {840, 868, 840}, new int[] {317, 332, 347}, 3);
    
        //PURPLE ARROW FROM FINAL TO SALES
            g.setColor(Color.black);
            g.fillPolygon(new int[] {1049, 1064, 1079}, new int[] {456,486,456}, 3);
            g.fillRect(1061,446,6,11);
            g.setColor(myPurple);
            g.fillRect(1059,444,6,11);
            g.fillPolygon(new int[] {1047, 1062, 1077}, new int[] {454,484,454}, 3);
    }

    //GameObject methods that weren't used above (for compilation purposes)
    public int getCost(){return 0;}
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
    public void sendInput(){}
    public int getOutputQueue(){return 0;}
    public void pullOutput(int a){}
}