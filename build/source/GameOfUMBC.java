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
int currentTile=0;//tracks how far along the player is

float playerGrade = 0; // the player doesn’t have a grade at the start
int playerHappiness=100; //assume the player is happy at the start
int playerWealth=10; //give player a $10 head start

boolean gameStart=false;//determines whether the game has started yet
boolean inputEvent=false;//used for when the player needs to give input
boolean examEvent=false;//used for when the player needs to take an exam
int changePath=0;//used for when the player needs to go left or right; 0 means they just go straight
PImage boardImage; //the image of the board
Tile[] gameTiles;//different tiles that are on the board
PImage[] dieFaces; //array that holds the six possible faces for a die to have

final int ORANGE=0xffff5722;
final int RED=0xffd50000;
final int WHITE=0xffe7fbf0;
final int GREEN=0xff4caf50;
final int BLUE=0xff03a9f4;
final int PINK=0xffda54ec;
final int DELTA_EXAM=25; //used to determine how much playerGrade changes when they take an exam

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
  
  //gameTiles = new Tile[] {new Tile(color(255, 165, 0),19), new Tile(color(255,0,0),2),
  //                new Tile(color(0,0,255),2), new Tile(color(0,255,0),1)};
  boardImage = loadImage("./Board.png");

}

public void draw() {
  image(boardImage,0,0);
}

/**
* @return the Tile the player is currently on
* if there isn't a tile found, return null
*/
private Tile getTile() {
  int toCheck = get((int)playerX,(int)playerY);
  Tile toReturn=null;
  for(int i=0;i<gameTiles.length;i++) {
    if(toCheck==gameTiles[i].getColor())
      toReturn=gameTiles[i];
  }
  return toReturn;
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
  private Position startDir, endDir;
  private Path nextPath;

  private HashMap<String, Integer> tileColors = new HashMap<String, Integer>(6);


  public Path(Tile[] tiles, Position start, Position end, Position startDir, Position endDir) {
    pointer = 0;
    this.tiles=tiles;
    this.startDir = startDir;
    this.endDir = endDir;
    for(int i=0;i<tiles.length;i++) {

    }
  }

  public Path(String path, Position start, Position end, Position startDir, Position endDir) {
    pointer = 0;
    tileColors.put("W",WHITE);
    tileColors.put("O",ORANGE);
    tileColors.put("R",RED);
    tileColors.put("B",BLUE);
    tileColors.put("G",GREEN);
    tileColors.put("P",PINK);
    this.startDir = startDir;
    this.endDir = endDir;

    for(int i=0;i<tiles.length;i++) {

    }
  }

 //String format: "x,y a b c d e f g x,y"
  private Tile[] parseString(String toParse)
  {
    String[] array = toParse.split("%s");
    String[] tempS = array[0].split(",");
    String[] tempE = array[array.length-1].split(",");
    Position start = new Position(Integer.parseInt(tempS[0]),Integer.parseInt(tempS[1]));
    Position end = new Position(Integer.parseInt(tempE[0]),Integer.parseInt(tempE[1]));

    Tile[] parsed = new Tile[array.length-2];

    for(int i=0;i<parsed.length;i++) {
      if(i==0) {
        parsed[i] = new Tile(toParse);
      } else if(i==parsed.length-1) {

      }

    }

    return parsed;
  }

  public Tile getCurrentTile() {
    return tiles[pointer];
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
  * Constructs the Tile object
  * @param mColor - the color of the tile
  * @param numEvents - the number of possible events that will occur
  * when the player steps on the tile
  */
  public Tile(int mColor, int numEvents, Position pos) {
    tileColor = mColor;
    events = new Event[numEvents];
    tilePos = pos;

    switch(tileColor) {
     case GREEN:

       break;
     case ORANGE:
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

       break;
     case BLUE:

       break;
     case RED:

       break;

    }
  }


  /**
  * @return - color tileColor
  */
  public int getColor() {return tileColor;}

  //return
  public Position getPos() {return tilePos;}
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
