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
  * variable to the next Path.
  */
  public void traversePath(int toTravel) {
    pointer+=toTravel;
    if(pointer>=tiles.length-1) {
      try {
        currentPath = nextPath;
      } catch(NullPointerException e) {
        e.printStackTrace();
      }
    }
  }

  //returns the next Path object the player will "be on"
  public Path getNextPath() {return nextPath;}
}
