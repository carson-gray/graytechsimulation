import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.event.*;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 2L;
    
    //initialization
    private Thread thread;                  //everything runs on single thread
    private boolean running = false;        //tracks whether or not game is running
    private Handler handler;                //a list that holds the game objects
    private HUD hud;                        //the mouse listener when in-game
    private Menu menu;                      //the menu screen, also a mouse listener
    private HowTo howTo;                    //a tutorial screen, also a mouse listener
    private GameOver gameOver;              //game over screen, also a mouse listener

    //used to track mouse listener activation
    private boolean fixedListeners_game = false;
    private boolean fixedListeners_howTo = false;
    private boolean fixedListeners_gameOver = false;

    //set the aspect ratio and background color
    public static final int WIDTH = 1280, HEIGHT = 720;
    Color backgroundColor = new Color(150,200,200);

    //the variations of the game state
    public enum STATE{
        Menu, Game, HowTo, GameOver
    };

    //start the game in the menu
    public STATE gameState = STATE.Menu;

    //game constructor
    public Game() {

        //constructing the objects instantiated above
        //'this' is the game itself
        handler = new Handler();
        menu = new Menu(this, handler);
        howTo = new HowTo(this);
        hud = new HUD(this, handler);
        gameOver = new GameOver(hud);
        
        //listening to mouse and key inputs
        this.addKeyListener(new KeyInput());
        this.addMouseListener(menu);

        //creates a new window, which starts the game
        new Window (WIDTH, HEIGHT, "GRAYTECH", this);
        
    }

    //method to start the game, invoked by the Window class
    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    //method to end the game
    public synchronized void stop() {
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //standard game loop
    //causes game to execute code and render graphics
    //this is based on a delta time taken from system
    public void run() {
        //makes the game the active window
        this.requestFocus();
        
        //setting up a timer using system time
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        //iterates while game is running
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            //execute code
            while(delta >= 1){
                tick();
                delta--;
            }
            
            //render graphics
            if(running)
                render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        //once running is false, invokes stop method
        stop();
    }

    //this executes game code over small time increments
    private void tick(){
        
        //when in the game
        if(gameState == STATE.Game) {
            //handler ticks all game objects
            handler.tick();
            //hud ticks separately since it is a mouse listener
            hud.tick();
            
            //this figures out which mouse listeners should be active
            if(fixedListeners_game == false){
                this.removeMouseListener(menu);
                this.removeMouseListener(howTo);
                this.addMouseListener(hud);
                fixedListeners_game = true;
            }
        }

        //when in the menu
        else if(gameState == STATE.Menu){
            menu.tick();
            if(fixedListeners_howTo == true){
                this.addMouseListener(menu);
                this.removeMouseListener(howTo);
                fixedListeners_howTo = false;
            }
        }

        //when in the how to menu
        else if(gameState == STATE.HowTo){
            howTo.tick();
            if(fixedListeners_howTo == false){
                this.addMouseListener(howTo);
                this.removeMouseListener(menu);
                fixedListeners_howTo = true;
            }
        }

        //when in the game over screen
        else if(gameState == STATE.GameOver){
            gameOver.tick();
            if(fixedListeners_gameOver == false){
                this.removeMouseListener(hud);
                this.addMouseListener(gameOver);
                fixedListeners_gameOver = true;
            }
        }

    }

    //renders the graphics of the game
    private void render(){
        //buffer strategy lets you draw and show graphics
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        //based on graphics import
        Graphics g = bs.getDrawGraphics();

        //background, uses values inititated at beginning of class
        g.setColor(backgroundColor);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //rendering our objects based on game state
        if(gameState == STATE.Game){
            hud.render(g);
            handler.render(g);
        }
        else if(gameState == STATE.Menu){
            menu.render(g);
        }
        else if(gameState == STATE.HowTo){
            howTo.render(g);
        }
        else if(gameState == STATE.GameOver){
            gameOver.render(g);
        }
        
        //dump the graphics from the last tick to prevent stacking
        g.dispose();
        bs.show();
    }


    //sets acceptable boundaries for a number value
    public static int clamp(int var, int min, int max){
        if(var >= max)
            return var = max;
        else if(var <= min)
            return var = min;
        else
            return var;
    }

    //main method: this is what makes it go
    public static void main(String args[]) {
        new Game();
    }

}