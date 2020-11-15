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
