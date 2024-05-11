import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

// Base class of everything in the game world
class Actor {
    public String name;        // the short name of the item
    public String description; // the long description text print to console when the player looks at the item

    public ArrayList<Actor> inventory = new ArrayList<Actor>();
	
    public String getDescription() {
        return description + listInventory();
    }

    public void processCommand(Player player, String command, String[] args) {
        if ("l".equals(command) || "look".equals(command)) {
            System.out.println(getDescription());
        } else {
            System.out.println(String.format("%s does't not understand the command %s.", name, command));
        }
    }

    // Return a string of all items in the inventory.
    public String listInventory() {
        // TODO: compose a string of all items in the inventory, then return it.
        // example: " There are 3 items in the inventory: key, letter, and coin."
        // if the inventory is empty, return empty string "".
        return "";
    }

    public Actor findInventory(String name) {
        // Search the inventory for item with specific name.
        // If found, returns that item. Othewise, null.
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).name == name) {
                // return the first item with the name.
                return inventory.get(i);
            }
        }
        // there's no item named as "name". return null pointer.
        return null;
    }

    public void removeItemFromInventory(Actor item) {
        // remove the item from the inventory.
        // this command will do nothing, if the item is not in the inventory.
        inventory.remove(item);
    }

    public String findSubstringBeforeDot(String s) {
        // 1. search all letters within the string to locate the position of the first ".".
        //    example: for "abc.def", the search should return 3.
        int dotPosition = -1;
        for(int i = 0; i < s.length(); ++i) {
            if ('.' == s.charAt(i)) {
                dotPosition = i;
                break;
            }
        }
       
        // 2. if there's no dot in the string, then return the whole string:
        //    examole: for "abcd", return "abcd".
        if (-1 == dotPosition) return s;
        
        // 3. for string like ".abcd", should return empty string "".
        if (0 == dotPosition) return "";
        
        // 4. the return a substring from the first letter to the letter before the dot.
        //    example: for "abc.def", return substring [0, 2].
        return s.substring(0, dotPosition); // 2nd argument of substr() is the length of the substring.
    }

    public String findSubstringAfterDot(String s) {
        return "";
    }
}

// Represents the player in the game
class Player extends Actor {
    public Room currentRoom;
}

class Container extends Actor {
    public Container() {
    	name = "...";
	    description = "This is a container.";
    }

    public void processCommand(Player player, String command, String[] args) {
    	if ("t".equals(command) || "take".equals(command)) {
            // The take command has 1 arguments: item.
                // It takes the item from current actor's inventory list, move it into the player's inventory.
            // for example: "container.take key" will move the "key", if exist, from container to player.
            if (args.length < 1) {
             // print an error.
               System.out.println("What do you want to take?");
            } else {
               String itemName = args[0];
               Actor target = findInventory(itemName);
               if (null == target) {
                   // print error message.
                   System.out.println("....");
               } else {
                   removeItemFromInventory(target);
                   player.inventory.add(target);
               }
            }
        // TODO: consider add put command to put items back to a container or a room.
        } else {
            super.processCommand(player, command, args);
        }
    }
}

class Letter extends Actor {
    public void processCommand(Player player, String command, String[] args) {
       if ("r".equals(command) || "read".equals(command)) {
           // TODO: read the content of the letter. print it out.
       } else if ("h".equals(command) || "help".equals(command)) {
           // TODO: print available commands of the letter.
       } else {
            super.processCommand(player, command, args);
       } 
    }
}

// Represents a room in the game world
class Room extends Actor {
    boolean locked = false;
    
    Room upExit = null;
    Room downExit = null;
    Room northExit = null;
    Room southExit = null;
    Room eastExit = null;
    Room westExit = null;

    public void processCommand(Player player, String command, String[] args) {
        // for moving round the room
        if ("n".equals(command)) {
            go(player, northExit);
        } else if ("s".equals(command)) {
            go(player, southExit);
        } 
          else if ("u".equals(command)){
              go(player, upExit);
          }
          else if ("d". equals(command)){
              go(player, downExit);
          }
          else if ("e".equals(command)) {
            go(player, eastExit);
        } else if ("w".equals(command)) {
            go(player, westExit);
	    } else if ("search".equals(command)) {
            listInventory();
    	// handle command in form of "target.action"
        } else if ("h".equals(command) || "help".equals(command)) {
            // TODO: print available commands of the room.
	    } else if (command.contains(".")) {
    	    String targetName = findSubstringBeforeDot(command); // retrieve the target.
    	    String actionName = findSubstringAfterDot(command);  // retrieve the action. 
            Actor target = findInventory(targetName);
	        if (null != target) {
            // let the target to process the action.
            target.processCommand(player, actionName, args);
	        } else {
		       System.out.println("There's no " + targetName + " found in current room");
            } 
        } else {
            super.processCommand(player, command, args);
        }
    }

    private void go(Player player, Room room) {
        if (room == null) {
            System.out.println("You can't go that way.");
        } else if (!room.locked) {
            System.out.println("You entered the " + room.name);
            player.currentRoom = room;
            room.processCommand(player, "look", null);
	    } else if (room.hasKey(player)) {
	        room.locked = false; // mark the room as unlocked.
            System.out.println("You unlocked the " + room.name);
            player.currentRoom = room;
            room.processCommand(player, "look", null);
        } else {
	    System.out.println(room.name + "is locked. May be you need a key?");
	    }
    }

    // Check if the player has key of the room
    boolean hasKey(Player player) {
        // the default implementation always return false,
        // meaning there's no common key for general room.
        return false;
    }
}
// set up a new room called living room 
class LivingRoom extends Room {
    public LivingRoom() {
        name = "Living Room";
        description = "You are in the living room. There is a TV, a table and a couch in the room. Hallway is at your east side ";
    }
}

class BasementHallway extends Room {
    public BasementHallway(){
        name = "Basement Hallway ";
        description = " you are in the basement. On your east side there is a storage room, and your west side, there is a locker room";
    }
}

class storageroom extends Room{
    public storageroom(){
        name = "Storage Room";
        description = "You are at the storageroom, there is a box with lock on it, and a closet, bsement hallway is at west side ";
    }

    public boolean hasKey(Player player) {
        // check if the player has the key of the storage room.
        Actor key = player.findInventory("Storage Room Key");
        if (null == key) return false; // no item named "Storage Room Key" in player's inventory, return false.

        // check if the key is an instance of StorageRoomKey.
        if (key instanceof StorageRoomKey) {
            // yes, this is indeed a StorageRoomKey. return true.
            return true;
        } else {
            // no, this is something else just happen to be called "Storage Room Key". return false.
            return false;
        }
    }
}

class StorageRoomKey extends Actor {
	public StorageRoomKey() {
		name = "Storage Room Key";
		description = "...";
	}
}

class staircase extends Room {
    public staircase(){
        name = "staircase";
        description = " You are at the staircase, going down is the basement, going up is the main floor";
    }
}
// set up a new room including the action things could do in the room 
class bedroom extends Room {
    public bedroom() {
        name = "bedroom";
        description = "You are in the bedroom. There is a bed and a closet in the room, at you south side, that's the guestbedroom. Bathroom is at your east";
    }
}

class kitchen extends Room{
    public kitchen(){
        name = "kitchen";
        description = " You are in the Kitchen. There is a toaster, a fridge in this room.";
    }
}

class hallway extends Room {
    public hallway(){
        name = "hallway";
        description = "You are at the hall way, livingroom is at your west side, bedroom in at you east side, kitchen in at your south side, the staircase is at your down side ";
    }
}

class guestbedroom extends Room {
    public guestbedroom(){
        name = "guestbedroom";
        description = " You are in the guestbedroom, there is a bed, and a closet";
    }
}

class bathroom extends Room {
    public bathroom(){
        name = "bathroom";
        description = " You are in the bathroom, there is a shower room, toilet. Bedroom is on your west side";
    }
}

public class Game {
    public static void main(String[] args) {
        // setup the game level.
        Player player = new Player();
        player.name = "Player";
        player.currentRoom = populateGameWorld();

        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

    	// Before game runs, print welcome screen to let player know how to play the game.
    	printWelcome();
	    
        // Run the game loop
        boolean gameOver = false;
        while (!gameOver) {
            // Read the user input
            String input = scanner.nextLine();// read user input 
            String[] parts = input.split(" "); // break up user input using " "
            String commandName = parts[0]; 
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
    
            // Process the user input
            if ("quit".equals(commandName)) {
                    System.out.println("Game Over!"); // if user enter "quit" game over= true 
                    gameOver = true;
            } else if ("welcome".equals(commandName)) {
                printWelcome();
            } else {
                player.currentRoom.processCommand(player, commandName, commandArgs); //need to use current room to process command 
            }
        }
    }

    // Setup game level, return the first room that the player is in.
    static Room populateGameWorld() {
        // We'll setup a simple game level that contains 2 rooms, a living room and a bedroom.
        // the living room is on the east side, and the bedroom is on the west side.
        Room livingRoom = new LivingRoom();
    	Room storageroom = new storageroom();
	    storageroom.locked = true; // locke the storage room. so player can't go in by default.
        Room bedroom = new bedroom();
        Room kitchen = new kitchen();
        Room hallway = new hallway();
        Room bathroom = new bathroom();
        Room staircase = new staircase();
        Room BasementHallway = new BasementHallway();
        Room guestbedroom = new guestbedroom();
        livingRoom.eastExit = hallway;
        hallway.westExit = livingRoom;
        hallway.eastExit = bedroom;
        hallway.downExit = staircase;
        staircase.upExit = hallway;
        staircase.downExit = BasementHallway;
        BasementHallway.upExit = staircase;
	    BasementHallway.eastExit = storageroom;
	    storageroom.westExit = BasementHallway;        
	    hallway.southExit = kitchen;
        kitchen.northExit = hallway;
        bedroom.westExit = hallway;
        bedroom.eastExit = bathroom;
        bathroom.westExit = bedroom;
        bedroom.southExit = guestbedroom;
        guestbedroom.northExit = bedroom;

    	// Create a container with the storage room key in it. Then put the container into the guest bedroom.
    	Container container = new Container();
    	StorageRoomKey key = new StorageRoomKey();
    	container.inventory.add(key);
    	guestbedroom.inventory.add(container);

        // Returns the living room as the starting room for the player
        return livingRoom;
    }

    static void printWelcome() {
	// print welcome screen and simple game tutorial.
	System.out.println("line 1");
	System.out.println("line 2");
	System.out.println("line 3");
	System.out.println("line 4");
	System.out.println("You can print this screen at any time during the game by typing \"help\" command");
    }
}