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
final int MAX_WEALTH = 100; //max money a player can have
final int MAX_HAPPY = 100;//max happiness a player can have
final int MAX_TILES = 157; //the most tiles in the paths the player can take.

//constants for drawing player status bars
final float WEALTH_X = 200;
final float WEALTH_Y = 250;
final float GRADE_X = 200;
final float GRADE_Y = 250;
final float HAPPY_X = 200;
final float HAPPY_Y = 250;
final float STATUSBAR_SIZE = 145;

//constants for drawing the dice
final int DICE_X=1400;
final int DICE_Y=63;
final int DICE_HEIGHT=100;
final int DICE_WIDTH=150;

//constants for drawing the Event box
final int EVENT_X=800;
final int EVENT_Y=10;
final int EVENT_WIDTH=600;
final int EVENT_HEIGHT=150;

//the hex value of each Tile's color
final int ORANGE=0xffff5722;
final int RED=0xffd50000;
final int WHITE=0xffe7fbf0;
final int GREEN=0xff4caf50;
final int BLUE=0xff03a9f4;
final int PINK=0xffda54ec;


//each type of stat that can be affected by an event, hence EventType
public enum EventType {
  GRADE,
  HAPPY,
  WEALTH;
}

//used to determine how much playerGrade changes when they take an exam
final int DELTA_EXAM=25;

final int TEXT_SIZE=20;
//X and Y position of the "Start" button
final int START_BUTTON_X=600;
final int START_BUTTON_Y=800;
final int START_BUTTON_SIZE=200;

//x,y and width, height of the choice buttons
final int CHOICES_LEFT_X=900;
final int CHOICES_RIGHT_X=1050;
final int CHOICES_Y=120;
final int CHOICES_WIDTH=120;
final int CHOICES_HEIGHT=25;

//every path the player can take.
Path[] path1, path21, path22, path3, path41, path42, path5, path61, path62, path7;

//to be set to path1 once the game starts; it is used to track which path the player is currently on.
Path currentPath;
//set to 1 when the game starts. Should be used for determining the next path the player chooses.
int pathNumber;

//images and fonts
PImage boardImage;
PImage dice;
PFont mana;
PImage playerToken;

//game loop booleans
boolean gameStart=false;//determines whether the game has started yet
boolean inputEvent=false;//used for when the player needs to give input
boolean examEvent=false;//used for when the player needs to take an exam
boolean rolled = false;
boolean playerTurn=false;//determines if the player can roll a die or not

//input by the user at the beginning of the game
String playerName;
//tracks where the player is using their X and Y
float playerX=1394;
float playerY=842;

//player stats
float playerGrade = 100; // the player has a 100/100 grade at the start
int playerHappiness=100; //assume the player is happy at the start
int playerWealth=10; //give player a $10 head start

//the current event that’s been registered by the game
String currentEvent="";
//used to store what the player rolled across draw loops
int roll;


public void setup() {
  
  frameRate(60);

  //load every image, then create the paths
  boardImage = loadImage("Board.png");
  playerToken = loadImage("africanamerican_girl.png");

  dice = loadImage("pixel_dice.png");
  mana = createFont("manaspc.ttf", 32);
  createPaths();
}


/**
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

  //Path 6 - path61 is the shorter one; path62 is the one to the right/above of path61. 25 and 33 tiles respectively
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

  //Path 4 - path41 is the higher one; path42 is the lower one. 20 and 16 respectively
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

  //if the game hasn't started, draw the start menu
  if(!gameStart) {
    drawStartMenu();
  } else {
    //if the game has ended, draw the end screen
    if(gameEnded()) {
      displayEndScreen();
    } else {
      //if the game hasn't ended, draw everything to the sketch.
      //the order is: board > roll button > status bars > event box > player
      image(boardImage,0,0);
      drawRollButton();
      drawPlayerStatus();
      drawEventBox();
      drawPlayerToken();
      //if the player needs to roll the die and isn't on a red tile, draw the choices buttons
      if(inputEvent && !examEvent)
        drawChoices();
    }
  }
}

/**
* Returns if the game has ended.
* If there is a NullPointerException, there is no next Path yet,
* so it will return if currentPath is the last path(path7[1]).
*/
public boolean gameEnded() {
  boolean toReturn=false;
  try {
    toReturn = (currentPath.getNextPath()==null && currentPath==path7[1]);
  }
  catch(NullPointerException e)
  {
    toReturn=currentPath==path7[1];
  }
  return toReturn;
}

//draws the player token
public void drawPlayerToken(){
   image(playerToken, playerX-40, playerY-40, 70, 70);
}

//handles player input
public void mousePressed() {

  //check if the game has started
  if(!gameStart) {
    //check if the Start button was pressed. if so, start the game
    if( (mouseX > START_BUTTON_X) && (mouseX < START_BUTTON_X+START_BUTTON_SIZE) && (mouseY > START_BUTTON_Y) && (mouseY < START_BUTTON_Y+50)) {
      gameStart=true;
      playerTurn=true;
      currentPath=path1[0];
      currentEvent="Click the dice to roll!";
    }
  } else {
    //if it's the player's turn, handle player input
    if(playerTurn) {
      textFont(mana);
      fill(255);
      textSize(16);

      //if the player isn't on a blue or red tile, their roll just moves them
      if(inputEvent==false){
        if ((mouseX > DICE_X+50) && (mouseX < DICE_X+DICE_WIDTH-25) && (mouseY > DICE_Y) && (mouseY < DICE_Y+DICE_HEIGHT-25)) {
          roll = rollDie();
          playerTurn=false;
          currentEvent="You rolled a " + roll;
          movePlayer(roll);
        }
      //inputEvent is true; the player is supposed to roll the die
      } else {
        //if the player is on a red tile, they roll to pass an exam
        if(examEvent) {
          currentEvent= "Click the dice to roll to see if you pass your exam!";
          //if the player clicked the button, calculate a roll and determine whether they pass the exam
          if ((mouseX > DICE_X+50) && (mouseX < DICE_X+DICE_WIDTH-25) && (mouseY > DICE_Y) && (mouseY < DICE_Y+DICE_HEIGHT-25)) {
            roll = rollDie();
            processExam(roll);
          }
        } else //the player is on a blue tile, check if they click one of the two choices buttons
        {
          //if the player clicks the left button
          if((mouseX > CHOICES_LEFT_X) && (mouseX < CHOICES_LEFT_X + CHOICES_WIDTH) && ((mouseY> CHOICES_Y) && (mouseY<CHOICES_Y+CHOICES_HEIGHT)) ) {
            //based on what the path currently is, decide what the next one will be
            switch(pathNumber)
            {
              case 1:
                currentPath.setNextPath(path22[0]);
                break;
              case 3:
                currentPath.setNextPath(path42[0]);
                break;
              case 5:
                currentPath.setNextPath(path61[0]);
                break;
            }
            inputEvent=false;
            pathNumber++;
            movePlayer(roll);
          }
          //if the player clicks the right button
          else if((mouseX > CHOICES_RIGHT_X) && (mouseX < CHOICES_RIGHT_X + CHOICES_WIDTH) && ((mouseY> CHOICES_Y) && (mouseY<CHOICES_Y+CHOICES_HEIGHT)) ) {
            //based on what the path currently is, decide what the next one will be
            switch(pathNumber)
            {
              case 1:
                currentPath.setNextPath(path21[0]);
                break;
              case 3:
                currentPath.setNextPath(path41[0]);
                break;
              case 5:
                currentPath.setNextPath(path62[0]);
                break;
            }
            pathNumber++;
            inputEvent=false;
            movePlayer(roll);
          }
        }
      }

    }
  }
}

/**
* Moves the player given the number of spaces to move.
* Calls traversePath on currentPath so that the player's location is kept track of,
* and then calls processTile() to handle the event that will come from the tile the player is on.
* If there's a NullPointerException or ArrayIndexOutOfBoundsException,
* the player is either on a blue/red tile or at the end of the board, so they are given a turn to give input.
*/
public void movePlayer(int spaces) {
  try {
    currentPath.traversePath(spaces);
    playerX = currentPath.getCurrentTile().getPos().getX();
    playerY = currentPath.getCurrentTile().getPos().getY();
    currentEvent="You moved " + spaces + " spaces.";
    processTile();
  } catch(NullPointerException e)
  {
    inputEvent=true;
    playerTurn=true;
  } catch(ArrayIndexOutOfBoundsException e)
  {
    inputEvent=true;
    playerTurn=true;
  }
}

/**
* Draws the roll button (a die)
*/
public void drawRollButton() {
  image(dice, DICE_X, DICE_Y, DICE_WIDTH, DICE_HEIGHT);
}

// draws wealth, grade, and happiness status bars on the top right of the screen
public void drawPlayerStatus() {
  textSize(TEXT_SIZE);

  fill(255);
  rect(WEALTH_X+30, WEALTH_Y-225, STATUSBAR_SIZE, 12, 20);

  fill(0, 255, 0);
  text("$", WEALTH_X+5, WEALTH_Y-215);
  rect(WEALTH_X+30,WEALTH_Y-225,STATUSBAR_SIZE*playerWealth/MAX_WEALTH, 12, 20);

  fill(255);
  rect(GRADE_X+215, GRADE_Y-225, STATUSBAR_SIZE, 12, 20);

  fill(255, 0, 0);
  rect(GRADE_X+215, GRADE_Y-225, STATUSBAR_SIZE*playerGrade/MAX_GRADE, 12, 20);
  text("A+", GRADE_X+187, GRADE_Y-215);

  fill(255);
  rect(HAPPY_X+400, HAPPY_Y-225, STATUSBAR_SIZE, 12, 20);

  fill(255, 255, 0);
  rect(HAPPY_X+400, HAPPY_Y-225, STATUSBAR_SIZE*playerHappiness/MAX_HAPPY, 12, 20);
  text(":)", HAPPY_X+375, HAPPY_Y-215);
}

/**
* returns the result of the rolled die
*/
public int rollDie() {
return PApplet.parseInt(random(1, 7));
}

/**
* draws the Start menu and the start "button"
*/
public void drawStartMenu(){
  fill(200);
  rect(0,0,width,height);

  fill(0xffffc0cb);
  textSize(50);
  text("Game Of UMBC!", width/2 - 200,height*0.1f);

  displayInstructions();

  fill(50);
  rect(START_BUTTON_X,START_BUTTON_Y,START_BUTTON_SIZE,50);
  fill(255);
  textSize(20);
  text("Click to start", START_BUTTON_X+10,START_BUTTON_Y+25);

}

/**
* draws the choices buttons that show when the player is on a blue tile and needs to pick a path.
*/
public void drawChoices(){

  textSize(20);

  fill(255);
  rect(CHOICES_LEFT_X,CHOICES_Y,CHOICES_WIDTH,CHOICES_HEIGHT);
  fill(0);
  text("Go Ahead", CHOICES_LEFT_X + 5, CHOICES_Y+20);

  fill(255);
  rect(CHOICES_RIGHT_X,CHOICES_Y,CHOICES_WIDTH,CHOICES_HEIGHT);
  fill(0);
  text("Go Around", CHOICES_RIGHT_X + 5,CHOICES_Y+20);

}

/**
* Draws text to the screen that explains what the player should do
*/
public void displayInstructions() {

  //text
  String g = "Goal: Reach the end of the board. \nGet good grades, be financially stable, \npass your exams, and have fun!";
  fill(0);
  text(g, 365, 150, 1500, 250);

  String i = "Instructions: You start each turn by rolling a die. Move to the # of spaces and an event may pop up. Keep rolling and playing until you reach the end of the board!";
  fill(0);
  text(i, 365, 400, 875, 400);
}

/**
* draws the end screen, which shows the player's stats
*/
public void displayEndScreen() {

  fill(50);
  rect(0, 0, width,height);

  fill(255);
  textSize(48);
  text("The Semester is Over! Here are your results:", GRADE_X, GRADE_Y-100);

  fill(255);
  rect(WEALTH_X+200, WEALTH_Y-50, STATUSBAR_SIZE-100, STATUSBAR_SIZE+150, STATUSBAR_SIZE-10);
  textSize(70);
  fill(0, 255, 0);
  rect(WEALTH_X+200, WEALTH_Y-50, STATUSBAR_SIZE-100, (STATUSBAR_SIZE+150)*playerWealth/MAX_WEALTH, STATUSBAR_SIZE-10);
  text("$", WEALTH_X+257, WEALTH_Y+250);

  fill(255);
  rect(GRADE_X+350, GRADE_Y-50, STATUSBAR_SIZE-100, STATUSBAR_SIZE+150, STATUSBAR_SIZE-10);
  textSize(65);
  fill(255, 0, 0);
  rect(GRADE_X+350, GRADE_Y-50, STATUSBAR_SIZE-100, (STATUSBAR_SIZE+150)*playerGrade/MAX_GRADE, STATUSBAR_SIZE-10);
  text("A°", GRADE_X+400, GRADE_Y+250);

  fill(255);
  rect(HAPPY_X+500, HAPPY_Y-50, STATUSBAR_SIZE-100, STATUSBAR_SIZE+150, STATUSBAR_SIZE-10);
  noStroke();
  fill(255, 255, 0);
  rect(HAPPY_X+500, HAPPY_Y-50, STATUSBAR_SIZE-100, STATUSBAR_SIZE+150*playerHappiness/MAX_HAPPY, STATUSBAR_SIZE-10);
  text(":)", HAPPY_X+550, HAPPY_Y+250);

  textSize(TEXT_SIZE);
}

/**
* draws a textbox that is populated with currentEvent.
*/
public void drawEventBox() {
  //background
  noStroke();
  fill(255,255,255,192);
  rect(EVENT_X,EVENT_Y,EVENT_WIDTH,EVENT_HEIGHT);

  //sides
  noStroke();
  fill (233, 35, 148);
  rect(EVENT_X, EVENT_Y, EVENT_WIDTH, 5);

  noStroke();
  fill (73, 222, 248);
  rect(EVENT_X+EVENT_WIDTH, EVENT_Y, 5, EVENT_HEIGHT);

  noStroke();
  fill (147, 196, 125);
  rect(EVENT_X, EVENT_Y+EVENT_HEIGHT, EVENT_WIDTH, 5);

  noStroke();
  fill (255, 217, 102);
  rect(EVENT_X, EVENT_Y, 5, EVENT_HEIGHT);

  //text
  textSize(26);
  fill(0);
  text("Event: " + parseEvent(), EVENT_X+10, 50);
}

//takes currentEvent and returns a text-wrapped version so that it fits in the Event box.
private String parseEvent()
{
  String[] toParse = currentEvent.split(" ");
  String parsed = "";

  int word=0;
  ArrayList<Integer> breaks = new ArrayList<Integer>(1);

  for(int i=0;i<currentEvent.length();i++)
  {
    if(currentEvent.charAt(i)==' ' && i!=0)
    {
      word++;
    }
    if(i % 27==0 && i!=0) {
      breaks.add(currentEvent.indexOf(toParse[word]));
    }
  }

  for(int a=0;a<currentEvent.length();a++)
  {
    for(int b=0;b<breaks.size();b++)
    {
      if(breaks.get(b)==a)
      {
        parsed+="\n";
      }
    }
    parsed+=currentEvent.charAt(a);
  }

  return parsed;
}

/**
* picks one of the possible Tile events at pseudo-random
* and sets currentEvent to it. Then it applies the relevant changes
* to the player's stats, after which the player can take a turn.
*/
private void processTile()
{
  Tile toProcess = currentPath.getCurrentTile();
  Event eventToProcess = toProcess.getEvents()[(int)random(0, toProcess.getEvents().length-1)];
  currentEvent = eventToProcess.getText();

  //tile is red
  if(toProcess.getColor()==RED)
  {
    examEvent=true;
    inputEvent=true;
  }
  //tile is either orange, white, pink, or green
  else if(toProcess.getColor() != BLUE)
  {
    examEvent=false;
    for(int i=0; i<eventToProcess.getType().length; i++)
    {
      switch(eventToProcess.getType()[i])
      {
        case GRADE:
          if(playerGrade+eventToProcess.getStatChange()[i]<MAX_GRADE && playerGrade+eventToProcess.getStatChange()[i]>0)
            playerGrade+=eventToProcess.getStatChange()[i];
          break;
        case HAPPY:
          if(playerHappiness+eventToProcess.getStatChange()[i]<MAX_HAPPY && playerHappiness+eventToProcess.getStatChange()[i]>0)
            playerHappiness+=eventToProcess.getStatChange()[i];
          break;
        case WEALTH:
          if(playerWealth+eventToProcess.getStatChange()[i]<MAX_WEALTH && playerWealth+eventToProcess.getStatChange()[i]>0)
            playerWealth+=eventToProcess.getStatChange()[i];
          break;
      }
    }
  }
  else
  {
    //the tile is blue
    inputEvent=true;
    examEvent=false;
  }
  playerTurn=true;
}

/**
* Calculates whether the player passes an exam
* and changes playerGrade based on that.
* Parameter: value of the rolled die/dice
*/
private void processExam(int roll)
{
  if(((playerHappiness)/10) + (((playerGrade)/10)*roll) >= 60.0f)
  {
    if(playerGrade+DELTA_EXAM < MAX_GRADE)
      playerGrade+=DELTA_EXAM;
    else playerGrade=MAX_GRADE;
    currentEvent="Congrats! You passed the exam.";
  }
  else
  {
    if(playerGrade-DELTA_EXAM>=0)
      playerGrade-=DELTA_EXAM;
    else playerGrade=0;
    currentEvent="You failed the exam...";
  }

  inputEvent=false;
  examEvent=false;
  playerTurn=true;
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

  /**
  * returns the current tile - throws ArrayIndexOutOfBoundsException when the next path needs to get set.
  */
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
  * Updates the Path object's pointer variable so that it tracks where the player is.
  * If the player is supposed to move to the next path, set the currentPath global
  * variable to the next Path. If nextPath is null, don't set currentPath to it.
  */
  public void traversePath(int toTravel) {
    pointer+=toTravel;
    if(pointer>=tiles.length-1) {
      try {
        if(nextPath!=null)
          currentPath = nextPath;
      } catch(NullPointerException e) {
        e.printStackTrace();
      }
    }
  }

  //returns the next Path object the player will "be on"
  public Path getNextPath() {return nextPath;}
}
class Position {
  private float x;
  private float y;

  /**
  * Represents a position in 2D space using X and Y
  */
  public Position(float x, float y) {
    this.x=x;
    this.y=y;
  }

  //returns the x value
  public float getX() {return x;}

  //returns the y value
  public float getY() {return y;}
}
class Tile {
  private int tileColor;
  private Event[] events;
  private Position tilePos;
  private Position direction;

  //constants used for initialization of events
  private final int ORANGE_EVENTS=14;
  private final int WHITE_EVENTS=7;
  private final int GREEN_EVENTS=1;
  private final int BLUE_EVENTS=2;
  private final int PINK_EVENTS=1;
  private final int RED_EVENTS=1;

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
       events[0] = new Event("You got your paycheck!", EventType.GRADE, 25);
       break;
     case ORANGE:
     events = new Event[ORANGE_EVENTS];
       events[0] = new Event("Slept in and missed class", EventType.GRADE, -5);
       events[1] = new Event("Went out with a friends to a party", new EventType[]{EventType.HAPPY,EventType.WEALTH}, new int[]{3,-2});
       events[2] = new Event("Went to the library to study", EventType.GRADE, 2);
       events[3] = new Event("Spent your summer working at Sunglasses Shack", EventType.WEALTH, 50);
       events[4] = new Event("Spent precious money for food on books and supplies", new EventType[]{EventType.WEALTH, EventType.GRADE}, new int[]{-5,3});
       events[5] = new Event("Got an extension on your essay", EventType.GRADE, 2);
       events[6] = new Event("Purchased one banana", EventType.WEALTH, -3);
       events[7] = new Event("Got a scholarship!", EventType.WEALTH, 50);
       events[8] = new Event("Your \"buddies\" crashed your car, Pay for repairs", EventType.WEALTH, -50);
       events[9] = new Event("Went on an Amazon shopping spree", EventType.WEALTH, -50);
       events[10] = new Event("Internet connection dropped while presenting on Blackboard", EventType.GRADE, -10);
       events[11] = new Event("Dog ate your COMP 101 homework, it was only a couple of bytes", EventType.GRADE, -3);
       events[12] = new Event("Went to Office Hours", EventType.GRADE, 5);
       events[13] = new Event("Your new next gen console arrived.", new EventType[]{EventType.HAPPY, EventType.GRADE, EventType.WEALTH}, new int[]{80,-40,-65});
       break;
    case PINK:
       events = new Event[PINK_EVENTS];
       events[0] = new Event("You feel sick", new EventType[]{EventType.HAPPY, EventType.GRADE}, new int[]{-10,-5});
       break;
    case BLUE:
       events = new Event[BLUE_EVENTS];
       events[0] = new Event("Do you study?", EventType.GRADE, 0);
       events[1] = new Event("Do you change your major?", EventType.GRADE, 0);
       break;
    case RED:
       events = new Event[RED_EVENTS];
       events[0] = new Event("You have to take an exam.", EventType.GRADE, 0);
       break;
    case WHITE:
       events = new Event[WHITE_EVENTS];
       events[0] = new Event("Grandma sent a care package filled with cookies.", EventType.HAPPY, 10);
       events[1] = new Event("Asked out your crush, got rejected.", EventType.HAPPY, -40);
       events[2] = new Event("Saw a flier on campus for free food.", EventType.HAPPY, 2);
       events[3] = new Event("Didn't check RateMyProfessor but still managed to get Prof B.", EventType.HAPPY, 80);
       events[4] = new Event("Your roommate got into feng shui, Why is my bed over there?!", EventType.HAPPY, -4);
       events[5] = new Event("Pulled an all nighter to take notes and study.", new EventType[]{EventType.HAPPY,EventType.GRADE},new int[]{-10,5});
       events[6] = new Event("Your roommate tested positive for COVID-19.", new EventType[]{EventType.HAPPY, EventType.GRADE}, new int[]{-50,5});
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
