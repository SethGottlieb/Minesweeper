import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.bezier.guido.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper extends PApplet {


int NUM_ROWS = 20;
int NUM_COLS = 20;
private MSButton[][] buttons; //2d array of minesweeper buttons
private ArrayList <MSButton> bombs; //ArrayList of just the minesweeper buttons that are mined
private boolean gameOver = false;

public void setup ()
{
    size(400, 400);
    textAlign(CENTER,CENTER);
    
    // make the manager
    Interactive.make( this );
    
    bombs = new ArrayList <MSButton>();
    buttons = new MSButton[NUM_ROWS][NUM_COLS];
    for (int i = 0; i < NUM_ROWS; i++) 
    {
        for (int j = 0; j < NUM_COLS; j++) 
        {
            buttons[i][j] = new MSButton(i,j);
        }
    }
    setBombs();
}
public void setBombs()
{
    while(bombs.size() < 30)
    {
        int r = (int)(Math.random() * NUM_ROWS);
        int c = (int)(Math.random() * NUM_COLS);
        if(!bombs.contains(buttons[r][c]))
        {
            bombs.add(buttons[r][c]);
        }
    }
}

public void draw ()
{
    background(0);
    if(isWon())
        displayWinningMessage();
}
public boolean isWon()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(!buttons[r][c].isMarked() && !buttons[r][c].isClicked())
                return false;
    return true;
}
public void displayLosingMessage()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(bombs.contains(buttons[r][c]))
                buttons[r][c].setLabel("M");

    String message = new String("GAME  OVER");
    for(int i = 0; i < message.length(); i++ )
    {
        buttons[9][i+5].clicked = true;
        if(!bombs.contains(buttons[9][i+5]))
            bombs.add(buttons[9][i+5]);
        buttons[9][i+5].setLabel(message.substring(i,i+1));
    }
}
public void displayWinningMessage()
{
    String message = new String("YOU  WON");
    for(int i = 0; i < message.length(); i++ )
    {
        buttons[9][i+6].clicked = true;
        if(!bombs.contains(buttons[9][i+6]))
            bombs.add(buttons[9][i+6]);
        buttons[9][i+6].setLabel(message.substring(i,i+1));
    }
}
public class MSButton
{
    private int r, c;
    private float x,y, width, height;
    private boolean clicked, marked;
    private String label;
    
    public MSButton ( int rr, int cc )
    {
        width = 400/NUM_COLS;
        height = 400/NUM_ROWS;
        r = rr;
        c = cc; 
        x = c*width;
        y = r*height;
        label = "";
        marked = clicked = false;
        Interactive.add( this ); // register it with the manager
    }
    public boolean isMarked()
    {
        return marked;
    }
    public boolean isClicked()
    {
        return clicked;
    }
    // called by manager
    
    public void mousePressed () 
    {
        if(gameOver) return;
        clicked = true;
        if(keyPressed)
            marked = !marked;
        else if(bombs.contains(this))
        {
            displayLosingMessage();
            gameOver = true;
        }
        else if(countBombs(r,c) > 0)
            label = "" + countBombs(r,c);
        else 
        {
            if(isValid(r-1,c) && !buttons[r-1][c].clicked)
                buttons[r-1][c].mousePressed();
            if(isValid(r+1,c) && !buttons[r+1][c].clicked)
                buttons[r+1][c].mousePressed();
            if(isValid(r,c-1) && !buttons[r][c-1].clicked)
                buttons[r][c-1].mousePressed();
            if(isValid(r,c+1) && !buttons[r][c+1].clicked)
                buttons[r][c+1].mousePressed();
            if(isValid(r-1,c+1) && !buttons[r-1][c+1].clicked)
                buttons[r-1][c+1].mousePressed();
            if(isValid(r+1,c+1) && !buttons[r+1][c+1].clicked)
                buttons[r+1][c+1].mousePressed();
            if(isValid(r-1,c-1) && !buttons[r-1][c-1].clicked)
                buttons[r-1][c-1].mousePressed();
            if(isValid(r+1,c-1) && !buttons[r+1][c-1].clicked)
                buttons[r+1][c-1].mousePressed();
        }
    }

    public void draw () 
    {    
        if (marked)
        {
            buttons[r][c].setLabel("F");
            fill(81, 15, 173);
        }
        else if(clicked && bombs.contains(this)) 
        {
            fill(255, 205, 0 );
        }
        else if(clicked)
        {
            fill(5, 125, 159);
        }
        else 
        {
            fill(23, 41, 176);
        }

        rect(x, y, width, height);
        fill(81, 15, 173);
        text(label,x+width/2,y+height/2);
    }
    public void setLabel(String newLabel)
    {
        label = newLabel;
    }
    public boolean isValid(int r, int c)
    {
        return r >=0 && r < NUM_ROWS && c >= 0 && c < NUM_COLS;
    }
    public int countBombs(int row, int col)
    {
        int numBombs = 0;
        if(isValid(row-1,col) && bombs.contains(buttons[row-1][col]))
            numBombs++;
        if(isValid(row+1,col) && bombs.contains(buttons[row+1][col]))
            numBombs++;
        if(isValid(row-1,col-1) && bombs.contains(buttons[row-1][col-1]))
            numBombs++;
        if(isValid(row+1,col-1) && bombs.contains(buttons[row+1][col-1]))
            numBombs++;
        if(isValid(row,col-1) && bombs.contains(buttons[row][col-1]))
            numBombs++;
        if(isValid(row-1,col+1) && bombs.contains(buttons[row-1][col+1]))
            numBombs++;
        if(isValid(row+1,col+1) && bombs.contains(buttons[row+1][col+1]))
            numBombs++;
        if(isValid(row,col+1) && bombs.contains(buttons[row][col+1]))
            numBombs++;           
        return numBombs;
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
