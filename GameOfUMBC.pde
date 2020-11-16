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
final int ORANGE=#ff5722;
final int RED=#d50000;
final int WHITE=#e7fbf0;
final int GREEN=#4caf50;
final int BLUE=#03a9f4;
final int PINK=#da54ec;

//each type of stat that can be affected by an event, hence EventType
public enum EventType {
  GRADE,
  HAPPY,
  WEALTH;
}

//used to determine how much playerGrade changes when they take an exam
final int DELTA_EXAM=25;

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

//game loop booleans
boolean gameStart=false;//determines whether the game has started yet
boolean inputEvent=false;//used for when the player needs to give input
boolean examEvent=false;//used for when the player needs to take an exam
boolean rolled = false;
boolean playerTurn=false;//determines if the player can roll a die or not

//input by the user at the beginning of the game
String playerName;
//tracks where the player is using their X and Y
float playerX;
float playerY;

//player stats
float playerGrade = 100; // the player has a 100/100 grade at the start
int playerHappiness=100; //assume the player is happy at the start
int playerWealth=10; //give player a $10 head start

//the current event that’s been registered by the game
String currentEvent="";

void setup() {
  size(1536,900);
  frameRate(60);

  boardImage = loadImage("./Board.png");
  createPaths();
  dice = loadImage("pixel_dice.png");
  mana = createFont("manaspc.ttf", 32);
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

void draw() {
  image(boardImage,0,0);
  drawRollButton();
  drawPlayerStatus();
  drawEventBox();
  // displayInstructions();
}

//
int roll;
void mousePressed() {
  if(playerTurn) {
    if ((mouseX > DICE_X+50) && (mouseX < DICE_X+DICE_WIDTH-25) && (mouseY > DICE_Y) && (mouseY < DICE_Y+DICE_HEIGHT-25)) {
      rolled=true;
      roll = int(random(1, 7));
      playerTurn=false;
      if(inputEvent==false)
      {
        //movePlayer(roll);
      } else if(examEvent) {
        processExam(roll);
      }
    }
  }
}

/**
* Draws the roll button(a die) and calls movePlayer when playerTurn==true.
*
*/
void drawRollButton() {
  image(dice, DICE_X, DICE_Y, DICE_WIDTH, DICE_HEIGHT);
  // if(playerTurn)
  // {
    textFont(mana);
    fill(255);
    textSize(16);

    if (rolled == false) {
      // currentEvent= "Click the dice to roll!";//, DICE_X-150, DICE_Y-25);
    } else {
        // currentEvent="You rolled a " + roll;//, DICE_X-150, DICE_Y-25);
        if(frameCount%120==0) {
          rolled=false;
        }
      }
  // }
}

// draws wealth, grade, and happiness status bars on the top right of the screen
void drawPlayerStatus() {
  textSize(20);

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
* Draws text to the screen that explains what the player should do
*/
void displayInstructions() {

  //background
  noStroke();
  fill(255);
  rect(350,200,500,400);

  //sides
  noStroke();
  fill (233, 35, 148);
  rect(350, 200, 500, 5);

  noStroke();
  fill (73, 222, 248);
  rect(850, 200, 5, 400);

  noStroke();
  fill (147, 196, 125);
  rect(350, 595, 500, 5);

  noStroke();
  fill (255, 217, 102);
  rect(350, 200, 5, 400);

  //text
  String g = "Goal: Reach the end of the board. Get good grades, be financially stable, pass your exams, and have fun!";
  fill(0);
  text(g, 365, 210, 475, 150);

  String i = "Instructions: You start each turn by rolling a die. Move to the # of spaces and an event may pop up. Keep rolling and playing until you reach the end of the board!";
  fill(0);
  text(i, 365, 400, 475, 400);
}

/**
* draws a textbox that is populated with currentEvent.
*/
void drawEventBox() {
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
* to the player's stats.
*/
private void processTile()
{
  Tile toProcess = currentPath.getCurrentTile();
  Event eventToProcess = toProcess.getEvents()[(int)random(0, toProcess.getEvents().length-1)];
  currentEvent = eventToProcess.getText();

  if(toProcess.getColor()==RED)
  {
    examEvent=true;
    inputEvent=false;
  }
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
    inputEvent=true;
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
  if(((playerHappiness)/10) + (((playerGrade)/10)*roll) >= 60.0)
  {
    if(playerGrade+DELTA_EXAM < MAX_GRADE)
      playerGrade+=DELTA_EXAM;
    else playerGrade=MAX_GRADE;
  }
  else
  {
    if(playerGrade-DELTA_EXAM>=0)
      playerGrade-=DELTA_EXAM;
    else playerGrade=0;
  }
}
