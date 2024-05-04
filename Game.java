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
        } else if ("e".equals(command)) {
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

class LivingRoom extends Room {
    public LivingRoom() {
        name = "Living Room";
        description = "You are in the living room. There is a TV, a table and a couch in the room.";
    }
}

class Bedroom extends Room {
    public Bedroom() {
        name = "Bedroom";
        description = "You are in the bedroom. There is a bed and a closet in the room.";
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
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String commandName = parts[0];
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

            // Process the user input
            if ("quit".equals(commandName)) {
                System.out.println("Game Over!");
                gameOver = true;
            } else {
                player.currentRoom.processCommand(player, commandName, commandArgs);
            }
        }
    }

    // Setup game level, return the first room that the player is in.
    static Room populateGameWorld() {
        // We'll setup a simple game level that contains 2 rooms, a living room and a bedroom.
        // the living room is on the east side, and the bedroom is on the west side.
        Room livingRoom = new LivingRoom();
        Room bedroom = new Bedroom();
        livingRoom.eastExit = bedroom;
        bedroom.westExit = livingRoom;

        // Returns the living room as the starting room for the player
        return livingRoom;
    }
}