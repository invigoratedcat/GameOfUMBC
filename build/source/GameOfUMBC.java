import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GameOfUMBC extends PApplet {

final float MAX_GRADE = 100; // max grade a player can get
final int MAX_TILES = 105; //the total tiles in the path the player can take.

float playerGrade = 0; // the player doesn’t have a grade at the start
int playerHappiness=100; //assume the player is happy at the start
int playerWealth=10; //give player a $10 head start

boolean gameStart=false;//determines whether the game has started yet
boolean inputEvent=false;//used for when the player needs to give input
boolean examEvent=false;//used for when the player needs to take an exam
int changePath=0;//used for when the player needs to go left or right; 0 means they just go straight
PImage boardImage; //the image of the board
PImage[] dieFaces; //array that holds the six possible faces for a die to have

final int ORANGE=0xffff5722;
final int RED=0xffd50000;
final int WHITE=0xffe7fbf0;
final int GREEN=0xff4caf50;
final int BLUE=0xff03a9f4;
final int PINK=0xffda54ec;

final int ORANGE_EVENTS=19;
final int WHITE_EVENTS=2;
final int GREEN_EVENTS=2;
final int BLUE_EVENTS=2;
final int PINK_EVENTS=2;
final int RED_EVENTS=2;
final int DELTA_EXAM=25; //used to determine how much playerGrade changes when they take an exam

Path[] path1;
Path[] path21;
Path[] path22;
Path[] path3;
Path[] path41;
Path[] path42;
Path[] path5;
Path[] path61;
Path[] path62;
Path[] path7;
//to be set to path1 once the game starts; it is used to track which path the player is currently on.
Path currentPath;

//each type of stat that can be affected by an event, hence EventType
public enum EventType {
  GRADE,
  HAPPY,
  WEALTH;
}


String playerName; //input by the user at the beginning of the game
float playerX; //the x position of the player token
float playerY; //the y pos of the player token
boolean playerTurn=false;//determines if the player can roll a die or not
String currentEvent="";//the current event that’s been registered by the game


public void setup() {
  

  boardImage = loadImage("./Board.png");
  createPaths();
}

/*
* Initializes each path the player can take in sequential order
* and then divided into two "sub paths" as denoted by the 1 in "path21" and 2 in "path22."
* Because each path connects to the next one, they are initialized in reverse order.
*/
private void createPaths()
{
  //Path 7 - has the last stop sign
  path7 = new Path[2];
  path7[1] = new Path("0,-1 o w o p o 0,0", new Position(51, 263), new Position(51, 103));
  path7[0] = new Path("-1,0 p o w o p g o p r w o p g o 0,-1", new Position(573, 263), new Position(51, 263), path7[1]);

  //Path 6 - path61 is the shorter one; path62 is the one to the right/above of path61
  path61 = new Path[2];
  path61[1] = new Path("-1,0 w o p g o w o p o w g o p o w o w g p -1,0", new Position(1302, 263), new Position(573, 263),path7[0]);
  path61[0] = new Path("0,-1 b o g o p o w -1,0", new Position(1302, 505), new Position(1302, 263),path61[1]);

  path62 = new Path[4];
  path62[3] = new Path("0,1 g o p -1,0", new Position(573, 183), new Position(573, 263),path7[0]);
  path62[2] = new Path("-1,0 w o p o w o g p o w o p o g w o p o w o g 0,1", new Position(1383, 183), new Position(573, 183),path62[3]);
  path62[1] = new Path("0,-1 g p o w o p o g w -1,0", new Position(1383, 505), new Position(1383, 183),path62[2]);
  path62[0] = new Path("1,0 b o g 0,-1", new Position(1302, 505), new Position(1383, 505),path62[1]);

  //Path 5 - has the first stop sign(red tile)
  path5 = new Path[6];
  path5[5] = new Path("0,-1 w g p o w o 0,-1", new Position(1302,745), new Position(1302, 545));
  path5[4] = new Path("1,0 p g o w o p o w 0,-1", new Position(1021,745), new Position(1302, 745),path5[5]);
  path5[3] = new Path("0,1 o p o w g o p o w o p 1,0", new Position(1021,343), new Position(1021,745),path5[4]);
  path5[2] = new Path("1,0 p o w o p g o w o 0,1", new Position(700,343), new Position(1021,343), path5[3]);
  path5[1] = new Path("0,-1 g o p 1,0", new Position(700, 424), new Position(700,343), path5[2]);
  path5[0] = new Path("1,0 o p r w g 0,-1", new Position(533, 424), new Position(700,424), path5[1]);

  //Path 4 - path41 is the higher one; path42 is the lower one
  path41 = new Path[3];
  path41[2] = new Path("0,1 o w o 1,0", new Position(533,344), new Position(533,424),path5[0]);
  path41[1] = new Path("1,0 w o p o g w o p o w o g o 0,1", new Position(51,344), new Position(533,344),path5[2]);
  path41[0] = new Path("0,-1 b o g p o w 1,0", new Position(51, 545), new Position(51,344),path5[1]);

  path42 = new Path[2];
  path42[1] = new Path("0,-1 p o w o 1,0",new Position(533,545), new Position(533,424),path5[0]);
  path42[0] = new Path("1,0 b o g p o w o p o g w o p 0,-1",new Position(51, 545), new Position(533,545),path42[1]);

  //Path 3
  path3 = new Path[2];
  path3[1] = new Path("0,-1 o w g o p o w 0,-1", new Position(51,826), new Position(51,585));
  path3[0] = new Path("-1,0 w p o w o p g o w o p o 0,-1", new Position(493,826), new Position(51, 826),path3[1]);

  //Path 2 - path21 is the longer one with many turns, path22 is the short one
  path21 = new Path[7];
  path21[6] = new Path("0,1 o p o g w -1,0", new Position(494,664), new Position(493,785),path3[0]);
  path21[5] = new Path("-1,0 g p o w o 0,1", new Position(655,664), new Position(494,664),path21[6]);
  path21[4] = new Path("0,1 p o w o g -1,0", new Position(653,503), new Position(655,664),path21[5]);
  path21[3] = new Path("-1,0 g w o p 0,1", new Position(774, 504), new Position(653, 503),path21[4]);
  path21[2] = new Path("0,1 o p g -1,0", new Position(774, 424), new Position(774, 504),path21[3]);
  path21[1] = new Path("-1,0 o p o w o 0,1", new Position(936,424), new Position(774, 424),path21[2]);
  path21[0] = new Path("0,-1 b g o w o p o w g o -1,0", new Position(936, 786), new Position(936,424),path21[1]);

  path22 = new Path[1];
  path22[0] = new Path("-1,0 b g w o p o g w o p o g 0,1", new Position(936, 786), new Position(493,785),path3[0]);

  //Path 1
  path1 = new Path[1];
  path1[0] = new Path("-1,0 w o p o w o g p o w o p 0,-1", new Position(1393, 841), new Position(935, 826));
}


public void draw() {
  image(boardImage,0,0);
}

/**
* Returns the Tile the player is currently on
*/
private Tile getTile() {
  return currentPath.getCurrentTile();
}

/**
* picks one of the possible Tile events at pseudo-random
* and sets currentEvent to it. Then it applies the relevant changes
* to the player's stats.
*/
private void processTile()
{
  Tile toProcess = getTile();
  Event eventToProcess = toProcess.getEvents()[(int)random(0, toProcess.getEvents().length-1)];
  currentEvent = eventToProcess.getText();

  if(toProcess.getColor()==RED)
  {
    examEvent=true;
    inputEvent=true;
  }
  else if(toProcess.getColor() != BLUE)
  {
    examEvent=false;
    for(int i=0; i<eventToProcess.getType().length; i++)
    {
      switch(eventToProcess.getType()[i])
      {
        case GRADE:
          playerGrade+=eventToProcess.getStatChange()[i];
          break;
        case HAPPY:
          playerHappiness+=eventToProcess.getStatChange()[i];
          break;
        case WEALTH:
          playerWealth+=eventToProcess.getStatChange()[i];
          break;
      }
    }
  }
  else
  {
    inputEvent=true;
  }
  playerTurn=true;
}

/**
* calculates whether the player passes an exam
* and changes playerGrade based on that
* @param the value of the rolled die/dice
*/
private void processExam(int roll)
{
  if(((playerHappiness)/10) + (((playerGrade)/10)*roll) >= 60.0f)
  {
    playerGrade+=DELTA_EXAM;
  }
  else
  {
    playerGrade-=DELTA_EXAM;
  }
}
class Event {
 private String text;
 private EventType[] types;
 private int[] statChanges;
 
 /**
 * Event constructor: takes a String, either an array of EventType or just one EventType,
 * and either an array of integers or just one integer; stores each of these in variables
 */
 public Event(String text, EventType[] types, int[] statChanges) {
   this.text=text;
   this.types=types;
   this.statChanges=statChanges;
 }
 
 public Event(String text, EventType type, int statChange) {
   this.text=text;
   this.types = new EventType[1];
   types[0]=type;
   this.statChanges = new int[1];
   statChanges[0]=statChange;
 }
 
 /**
 * returns the array of EventType associated with this Event
 */
 public EventType[] getType() {return types;}
 
  /**
 * returns the String representing what happens to the player's character
 * associated with this Event
 */
 public String getText() {return text;}
 
  /**
 * returns the array of integers representing changes to the player's stats
 * associated with this Event
 */
 public int[] getStatChange() {return statChanges;}
}
class Path {
  private Tile[] tiles;
  private int pointer;
  private Position startPos, endPos;
  private Path nextPath;

  //this is used as reference to get the color value from each letter in the middle part of the string
  private HashMap<String, Integer> tileColors = new HashMap<String, Integer>(6);

 /**
 * A path consists of an array of tiles, a starting position and direction, and an end position and direction.
 * All but the positions are taken from the given string.
 *
 */
  public Path(String path, Position start, Position end) {
    pointer = 0;
    tileColors.put("w",WHITE);
    tileColors.put("o",ORANGE);
    tileColors.put("r",RED);
    tileColors.put("b",BLUE);
    tileColors.put("g",GREEN);
    tileColors.put("p",PINK);

    startPos=start;
    endPos=end;
    this.tiles = parseString(path);

  }

  public Path(String path, Position start, Position end, Path nextPath) {
    pointer = 0;
    tileColors.put("w",WHITE);
    tileColors.put("o",ORANGE);
    tileColors.put("r",RED);
    tileColors.put("b",BLUE);
    tileColors.put("g",GREEN);
    tileColors.put("p",PINK);

    startPos=start;
    endPos=end;
    this.tiles = parseString(path);

    this.nextPath = nextPath;
  }

 //String format: "x,y a b c d e f g x,y" where "a b c d e f g" is substituted with tile letters(e.g., o,w,r,g,b,p)
 //given a String, parses it and returns an array of appropriately initialized Tiles
  private Tile[] parseString(String toParse)
  {
    //splits the string toParse into an array based on whitespace
    String[] parseable = toParse.toLowerCase().split(" ");

    //further splits the parseable array for the purpose of getting the start and end positions
    String[] tempS = parseable[0].split(",");
    String[] tempE = parseable[parseable.length-1].split(",");

    println(tempE[0]);
    //parses the start and end strings as "x,y"
    Position start = new Position(Integer.parseInt(tempS[0]),Integer.parseInt(tempS[1]));
    Position end = new Position(Integer.parseInt(tempE[0]),Integer.parseInt(tempE[1]));

    //removes the first and last element in the parseable array, as they are no longer needed
    String[] temp = new String[parseable.length-2];
    for(int a=0;a<temp.length;a++) {
      temp[a] = parseable[a+1];
    }
    parseable=temp;

    //instantiates the Tile array to be returned by this method
    Tile[] parsed = new Tile[parseable.length];

    for(int i=0;i<parsed.length;i++) {
      if(i==parsed.length-1) {
        parsed[i] = new Tile(tileColors.get(parseable[i]),endPos, end);
      } else if(i==0) {
        parsed[i] = new Tile(tileColors.get(parseable[i]), startPos, start);
      } else {
        //offset each Tile's position based on the starting direction, position and the current index
        parsed[i] = new Tile(tileColors.get(parseable[i]), new Position(startPos.getX()+i*35*start.getX(), startPos.getY()+i*35*start.getY()), start);
      }

    }

    return parsed;
  }

  public Tile getCurrentTile() {
    return tiles[pointer];
  }

  /**
  * Sets the next path after this one.
  * Use when the player decides which path to take before moving them.
  */
  public void setNextPath(Path nextPath) {
    this.nextPath=nextPath;
  }

  /**
  * Tracks where the player is
  *
  */
  public void traversePath(int toTravel) {
    pointer+=toTravel;
    if(pointer>=tiles.length-1) {
      currentPath = nextPath;
    }
  }


}
class Position {
  private int x;
  private int y;

  /**
  * Represents a position in 2D space using X and Y
  */
  public Position(int x, int y) {
    this.x=x;
    this.y=y;
  }

  //returns the x value
  public int getX() {return x;}

  //returns the y value
  public int getY() {return y;}
}
class Tile {
  private int tileColor;
  private Event[] events;
  private Position tilePos;
  private Position direction;

  /**
  * Constructs the Tile object with a color, direction, and position.
  * Each tile is 35x35 pixels
  */
  public Tile(int mColor, Position pos, Position dir) {
    tileColor = mColor;
    direction=dir;
    tilePos = pos;

    switch(tileColor) {
     case GREEN:
       events = new Event[GREEN_EVENTS];
       break;
     case ORANGE:
     events = new Event[ORANGE_EVENTS];
       events[0] = new Event("Slept in and missed class", EventType.GRADE, -5);
       events[1] = new Event("Went out with a friends to a party", new EventType[]{EventType.HAPPY,EventType.WEALTH}, new int[]{3,-2});
       events[2] = new Event("Grandma sent a care package filled with cookies", EventType.HAPPY, 10);
       events[3] = new Event("Went to the library to study", EventType.GRADE, 2);
       events[4] = new Event("Spent your summer working at Sunglasses Shack", EventType.WEALTH, 50);
       events[5] = new Event("Asked out your crush, got rejected", EventType.HAPPY, -40);
       events[6] = new Event("Saw a flier on campus for free food", EventType.HAPPY, 2);
       events[7] = new Event("Spent precious money for food on books and supplies", new EventType[]{EventType.WEALTH, EventType.GRADE}, new int[]{-5,3});
       events[8] = new Event("Got an extension on your essay", EventType.GRADE, 2);
       events[9] = new Event("Purchased one banana", EventType.WEALTH, -3);
       events[10] = new Event("Got a scholarship!", EventType.WEALTH, 50);
       events[11] = new Event("Your \"buddies\" crashed your car, Pay for repairs", EventType.WEALTH, -50);
       events[12] = new Event("Went on an Amazon shopping spree", EventType.WEALTH, -50);
       events[13] = new Event("Internet connection dropped while presenting on Blackboard", EventType.GRADE, -10);
       events[14] = new Event("Dog ate your COMP 101 homework, it was only a couple of bytes", EventType.GRADE, -3);
       events[15] = new Event("Went to Office Hours", EventType.GRADE, 5);
       events[16] = new Event("Didn't check RateMyProfessor but still managed to get Prof B", EventType.HAPPY, 80);
       events[17] = new Event("Your roommate got into feng shui, Why is my bed over there?!", EventType.HAPPY, -4);
       events[18] = new Event( "Pulled an all nighter to take notes and study", new EventType[]{EventType.HAPPY,EventType.GRADE},new int[]{-10,5});
       break;
    case PINK:
       events = new Event[PINK_EVENTS];
       break;
     case BLUE:
       events = new Event[BLUE_EVENTS];
       break;
     case RED:
       events = new Event[RED_EVENTS];
       break;

    }
  }


  /**
  * @return - color tileColor
  */
  public int getColor() {return tileColor;}

  //return the tile's position on the board
  public Position getPos() {return tilePos;}

  //returns the tile's direction
  public Position getDirection() {return direction;}

  /**
  * @return - an array of Event objects
  */
  public Event[] getEvents() {return events;}
}
  public void settings() {  size(1536,900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GameOfUMBC" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
