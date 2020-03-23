import java.awt.Graphics;

//every object will be a GameObject, this standardizes how they run
public abstract class GameObject {

    protected int x, y;
    protected ID id;
    Handler handler;
    
    public GameObject (int x, int y, ID id, Handler handler) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.handler = handler;
    }
    
    //-----------------------------------------------------------------

    //THESE ARE THE EXTERNALLY-INVOKABLE PUBLIC METHODS FOR GAME OBJECTS
    //when making a method, if you want another class to be able to call them, you have to put them here

    //TO ACTUALLY CALL THE METHODS ON A SPECIFIC OBJECT:
    //access Handler objects and their methods using handler.object.get(#).methodInvoked()
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sale

    //----------------------------------------------------------------
    
    public abstract void tick();
    public abstract void render(Graphics g);

    //METHODS USED BY MULTIPLE CLASSES
    public abstract int getCost();                  //gets the cost of an action
    public abstract int getCycleUpgradeCost();
    public abstract int getStationUpgradeCost();
    
    public abstract void cycleUpgrade();            //executes the upgrade
    public abstract void stationUpgrade();
    
    public abstract boolean cycleUpgradeCheck();    //checks to see if another upgrade is available
    public abstract boolean stationUpgradeCheck();
    
    public abstract int charge();                   //charges the bank account

    //CLASS SPECIFIC METHODS, will be explained in respective class
    //order terminal abstract methods
    public abstract void addToOrder();
    public abstract void takeFromOrder();
    public abstract void reset();
    public abstract void shipOrder(int a);
    public abstract int getOrderSize();

    //3d printer abstract methods
    public abstract void mUp();
    public abstract void mDown();
    public abstract void eUp();
    public abstract void eDown();
    public abstract void orderDelivery(int a);

    //mech and elec abstracts (called by 3d printer)
    public abstract void sendInput();
    public abstract int getOutputQueue();
    public abstract void pullOutput(int a);

    //sales terminal (final assembly) abstract methods
    public abstract void pUp();
    public abstract void pDown();
    public abstract int sell();
    public abstract int getUnitSales();

    //---------------------------------------------------------
    //basic getters and setters for xy coordinates and id
    public void setX(int x){
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getY(){
        return y;
    }

    public void setID(ID id){
        this.id = id;
    }

    public ID getID(){
        return id;
    }


}