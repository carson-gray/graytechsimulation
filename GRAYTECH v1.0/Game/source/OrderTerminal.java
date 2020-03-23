import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class OrderTerminal extends GameObject {

    //accesses Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    //this is the player's way of ordering direct materials
    //on the top left of the screen

    //setting the constants for the game
    final private int UNIT_PRICE = 5;
    final private int LEAD_TIME = 7;
    final private int LEAD_TIME_HOURS = LEAD_TIME * handler.getDayThreshold();  //converts printed lead time into hours
    final private int ORDER_INCREMENT = 10;
    final private int SHIPPING_COST = 100;
    
    //runs on an hour basis, which is why lead time is converted
    private int hour = 0;
    private int pacer = 0;

    //used to track orders that have been placed
    ArrayList<Integer> orderList = new ArrayList<Integer>();
    private int ordersInTransit = 0;

    //orderSize is size of that specific order, orderPrice is the price of that specific order
    private int orderSize = ORDER_INCREMENT;                            //initialized to minimum order size
    private int orderPrice = orderSize * UNIT_PRICE + SHIPPING_COST;
    
    //constructor
    public OrderTerminal(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler);
    }

//---------------------------------------------------------------------------------------

    public int getCost(){
        return orderPrice;
    }

    public int getOrderSize(){
        return orderSize;
    }

    //FOR EDITING THE ORDER BEFORE IT IS PLACED
    public void addToOrder(){
        orderSize += ORDER_INCREMENT;
        orderPrice = ORDER_INCREMENT * UNIT_PRICE + orderPrice;
    }

    //FOR EDITING THE ORDER BEFORE IT IS PLACED
    public void takeFromOrder(){
        if(orderSize - ORDER_INCREMENT > 0){
            orderSize -= ORDER_INCREMENT;
            orderPrice = orderPrice - ORDER_INCREMENT * UNIT_PRICE;
        }
    }

    //resetting the values after order is placed, serves as a confirmation of sorts
    public void reset(){
        orderSize = ORDER_INCREMENT;
        orderPrice = orderSize * UNIT_PRICE + SHIPPING_COST;
    }

    //order is actually placed
    public void shipOrder(int myOrder){             //takes size of order
        ordersInTransit++;                          //indicates another order in transit
        orderList.add(Integer.valueOf(hour));       //adds the time of the order to list
        orderList.add(Integer.valueOf(myOrder));    //adds size of order as next item in list
        reset();                                    //resets the order terminal values
    }

    public void tick() {

        //Paces on the standard hour format
        pacer++;
        if(pacer == handler.getHourThreshold()){
            pacer = 0;
            hour++;

            //if there is an order in transit...
            if(ordersInTransit > 0){
                if(hour - orderList.get(0).intValue() >= LEAD_TIME_HOURS){              //checks when least recent order was placed, checks if it is time to deliver
                    handler.object.get(1).orderDelivery(orderList.get(1).intValue());   //delivers the order to the 3d printer class by accessing second item on order list (order size)
                    orderList.remove(0);                                                //removes the timing of the order, order size become first item on list
                    orderList.remove(0);                                                //removes new first item (order size)
                    ordersInTransit--;                                                  //indicates order is no longer in transit
                }                                                                       //this method can handle any number of orders
            }                                                                           //if order placed in same hour, there will be an hour delay on one order's delivery
        }

    }

//---------------------------------------------------------------

    public void render(Graphics g) {
        
        //initializing fonts and colors
            Font titleFnt = new Font("arial", 1, 18);
            Font bodyFnt = new Font("arial", 1, 14);
            Color darkRed = new Color(170, 0, 0);
            Color darkGreen = new Color(0, 155, 25);
            Color nearWhite = new Color(235,235,235);

        //Sets up the terminal window
            g.setColor(Color.black);
            g.fillRect(16, 56,532, 98);
            g.setColor(Color.white);
            g.fillRect(20, 60, 524, 90);
            g.setColor(Color.black);
            g.fillRect(20, 60, 524, 40);
            g.setColor(Color.black);
            g.fillRect(20, 100, 524, 2);
        
        //Sets up the terminal title
            g.setColor(Color.yellow);
            g.fillRect(20, 60, 192, 38);
            g.setFont(titleFnt);
            g.setColor(Color.black);
            g.drawString("ORDER TERMINAL", 29, 86);

        //Sets up the general order information
            g.setColor(Color.white);
            g.setFont(bodyFnt);
            g.drawString("Unit Price: $ " + UNIT_PRICE, 220, 78);
            g.drawString("Lead Time: " + LEAD_TIME + " days", 220, 93);
            g.drawString("Order Increment: " + ORDER_INCREMENT + " units", 355, 78);
            g.drawString("Cost of Shipping: $ " + SHIPPING_COST, 355, 93);

        //Sets up the Units and Price for the order
            g.setColor(Color.black);
            g.fillRect(111, 108, 48, 34);
            g.setColor(Color.black);
            g.fillRect(263, 108, 69, 34);
            g.setColor(nearWhite);
            g.fillRect(113, 110, 44, 30);
            g.fillRect(265, 110, 65, 30);
            g.setFont(titleFnt);
            g.setColor(Color.black);
            g.drawString("UNITS:      " + orderSize, 28, 132);
            g.drawString("PRICE:      $" + orderPrice, 180, 132);
        
        //Sets up the "+" button
            g.setColor(Color.black);
            g.fillRect(344, 113, 28, 28);
            g.setColor(darkGreen);
            g.fillRect(342, 111, 28, 28);
            g.setFont(titleFnt);
            g.setColor(Color.green);
            g.drawString("+", 351, 132);
        
        //Sets up the "-" button
            g.setColor(Color.black);
            g.fillRect(382, 113, 28, 28);
            g.setColor(darkRed);
            g.fillRect(380, 111, 28, 28);
            g.setFont(titleFnt);
            g.setColor(Color.red);
            g.drawString("-", 390, 131);
        
        //Sets up the "SUBMIT" button
            g.setColor(Color.black);
            g.fillRect(427, 111, 110, 34);
            g.setColor(Color.yellow);
            g.fillRect(424, 108, 110, 34);
            g.setFont(titleFnt);
            g.setColor(Color.darkGray);
            g.drawString("SUBMIT", 445, 132);

    }

    //GameObject methods that weren't used above (for compilation purposes)
    public int getCycleUpgradeCost(){return 0;}
    public int getStationUpgradeCost(){return 0;}
    public void cycleUpgrade(){}
    public void stationUpgrade(){}
    public boolean cycleUpgradeCheck(){return true;}
    public boolean stationUpgradeCheck(){return true;}
    public int charge(){return 0;}
    public void mUp(){}
    public void mDown(){}
    public void eUp(){}
    public void eDown(){}
    public void orderDelivery(int a){}
    public void sendInput(){}
    public int getOutputQueue(){return 0;}
    public void pullOutput(int a){}
    public void pUp(){}
    public void pDown(){}
    public int sell(){return 0;}
    public int getUnitSales(){return 0;}

}