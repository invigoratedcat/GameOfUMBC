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

final int ORANGE=#ff5722;
final int RED=#d50000;
final int WHITE=#e7fbf0;
final int GREEN=#4caf50;
final int BLUE=#03a9f4;
final int PINK=#da54ec;

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


void setup() {
  size(1536,900);

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


void draw() {
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
  if(((playerHappiness)/10) + (((playerGrade)/10)*roll) >= 60.0)
  {
    playerGrade+=DELTA_EXAM;
  }
  else
  {
    playerGrade-=DELTA_EXAM;
  }
}
