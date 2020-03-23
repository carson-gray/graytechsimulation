import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{

    //this class exists for the escape key

    public KeyInput(){
    }

    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();


        //the purpose of the class
        if(key == KeyEvent.VK_ESCAPE)
            System.exit(1);
    }

}