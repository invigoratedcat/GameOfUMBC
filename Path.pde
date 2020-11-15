class Path {
  private Tile[] tiles;
  private int pointer;
  private Position startDir, endDir;
  private Position startPos, endPos;
  private Path nextPath;

  private HashMap<String, Integer> tileColors = new HashMap<String, Integer>(6);

 //35x35
  public Path(String path, Position start, Position end) {
    pointer = 0;
    tileColors.put("W",WHITE);
    tileColors.put("O",ORANGE);
    tileColors.put("R",RED);
    tileColors.put("B",BLUE);
    tileColors.put("G",GREEN);
    tileColors.put("P",PINK);

    this.tiles = parseString(path);
    startPos=start;
    endPos=end;
  }

 //String format: "x,y a b c d e f g x,y" where "a b c d e f g" is substituted with tile letters(e.g., o,w,r,g,b,p)
 //given a String, parses it and returns an array of appropriately instantiated Tiles
  private Tile[] parseString(String toParse)
  {
    String[] parseable = toParse.toLowerCase().split("%s");
    String[] tempS = parseable[0].split(",");
    String[] tempE = parseable[parseable.length-1].split(",");
    Position start = new Position(Integer.parseInt(tempS[0]),Integer.parseInt(tempS[1]));
    Position end = new Position(Integer.parseInt(tempE[0]),Integer.parseInt(tempE[1]));

    //removes the first and last element in the array parseable
    String[] temp = new String[parseable.length-2];
    for(int a=0;a<temp.length;a++) {
      temp[a] = parseable[a+1];
    }
    parseable=temp;

    Tile[] parsed = new Tile[parseable.length-2];

    for(int i=0;i<parsed.length;i++) {
      if(i==parsed.length-1) {
        parsed[i] = new Tile(tileColors.get(parseable[i]),endPos, end);
      } else if(i==0) {
        parsed[i] = new Tile(tileColors.get(parseable[i]), startPos, start);
      } else {
        //offset each Tile's position based on the starting direction and index
        parsed[i] = new Tile(tileColors.get(parseable[i]), new Position(startPos.getX()+i*35*start.getX(), startPos.getY()+i*35*start.getY()), start);
      }

    }

    return parsed;
  }

  public Tile getCurrentTile() {
    return tiles[pointer];
  }


}
