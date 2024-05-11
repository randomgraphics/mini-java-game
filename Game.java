import java.util.Scanner;
import java.util.Arrays;

// Base class of everything in the game world
class Actor {
    public String name;        // the short name of the item
    public String description; // the long description text print to console when the player looks at the item

    public String getDescription() {
        return description;
    }

    public void processCommand(Player player, String command, String[] args) {
        if ("lookat".equals(command)) {
            System.out.println(getDescription());
        } else {
            System.out.println(String.format("%s does't not understand the command %s.", name, command));
        }
    }
}

// Represents the player in the game
class Player extends Actor {
    public Room currentRoom;
}

// Represents a room in the game world
class Room extends Actor {
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
        } else if ("l".equals(command) || "look".equals(command)) {
            System.out.println(getDescription());
        } else {
            super.processCommand(player, command, args);
        }
    }

    private void go(Player player, Room room) {
        if (room == null) {
            System.out.println("You can't go that way.");
        } else {
            System.out.println("You entered the " + room.name);
            player.currentRoom = room;
            room.processCommand(player, "look", null);
        }
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
name = "storageroom";
description = "You are at the storageroom, there is a box with lock on it, and a closet, bsement hallway is at west side ";
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


        // Returns the living room as the starting room for the player
        return livingRoom;
    }
}
