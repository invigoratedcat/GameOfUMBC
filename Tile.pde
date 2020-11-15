class Tile {
  private color tileColor;
  private Event[] events;
  private Position tilePos;
  private Position direction;

  /**
  * Constructs the Tile object with a color, direction, and position.
  * Each tile is 35x35 pixels
  */
  public Tile(color mColor, Position pos, Position dir) {
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
  public color getColor() {return tileColor;}

  //return the tile's position on the board
  public Position getPos() {return tilePos;}

  //returns the tile's direction
  public Position getDirection() {return direction;}

  /**
  * @return - an array of Event objects
  */
  public Event[] getEvents() {return events;}
}
