import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.awt.Graphics;

public class Handler {

    LinkedList<GameObject> object = new LinkedList<GameObject>();

    //access Handler objects using 'handler.object.get(#)'
        //0:    Order Terminal
        //1:    3D Printer/DM Inventory
        //2:    Mechanical Assembly/Queues
        //3:    Electronics Assembly/Queues
        //4:    Final Assembly/Sales

    //THIS IS HOW YOU CONTROL GAME PACING
    //-----------------------------------
    //-----------------------------------
    
    private int hourThreshold = 11;         //number of ticks in an hour (+/- the value to +/- the pacing)
    private int dayThreshold = 24;          //number of hours in a day (currently at 24 for simplicity, could do 16)

    //the other objects get this pacing using these methods
    public int getHourThreshold(){
        return hourThreshold;
    }

    public int getDayThreshold(){
        return dayThreshold;
    }
    
    //-----------------------------------
    //-----------------------------------

    //when handler ticks & renders, every object in the list ticks & renders
    public void tick(){
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.tick();
        }
    }

    public void render(Graphics g){
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }

    //used to initialize my objects, done by Menu
    public void addObject(GameObject object){
        this.object.add(object);
    }

    public void removeObject(GameObject object){
        this.object.remove(object);
    }

}