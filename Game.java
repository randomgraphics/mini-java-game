import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

// Base class of everything in the game world
class Actor {
    public String name; // the short name of the item
    public String description = null; // the long description text print to console when the player looks at the item
    public boolean moveable = false; // if the item is moveable (can be taken by player). Default is false.
    public ArrayList<Actor> inventory = new ArrayList<Actor>();

    public Actor() {
        name = "";
    }

    public Actor(String n) {
        name = n;
    }

    public Actor(String n, String d) {
        name = n;
        description = d;
    }

    public String getDescription() {
        if (null == description) {
            // If the description is not set, return the name of the item.
            return "This is a " + name + ".";
        }
        else {
            return description;
        }
    }

    public void processCommand(String command, String[] args) {
        if ("l".equals(command)) {
            if (args.length > 0) {
                // We got a command like "l <item>". This means the player wants to look at something in the inventory.
                Found f = findInventory(args[0]);
                Actor item = f.item;
                Actor owner = f.owner;
                if (null != item) {
                    System.out.println(item.getDescription());
                } else {
                    System.out.println("There's no item called " + f.unrecogizedName + " in the " + owner.name + ".");
                }
            } else {
                // print the description of the current actor.
                System.out.println(getDescription());
            }
        } else {
            System.out.println(String.format("The %s does't not understand the command %s.", name, command));
        }
    }

    // Return a string of all items in the inventory.
    public String listInventory() {
        String result = "";
        for (int i = 0; i < inventory.size(); i++) {
            if (0 == i) {
                // This is the first item in the list. No need to insert a comma.
                result = inventory.get(i).name;
            } else {
                // append the name to the result string with a comma in front.
                result = result + ", " + inventory.get(i).name;
            }
        }
        return result;
    }

    class Found {
        public Actor item;
        public Actor owner;
        public String unrecogizedName;
        public Found(Actor i, Actor o, String u) {
            item = i;
            owner = o;
            unrecogizedName = u;
        }
    }

    // Search the inventory for item with specific name.
    // The name could have dot in it, for example "a.b". This means search for item 'b' in the inventory of 'a'.
    // This process can be done recursively. So a name "a.b.c" means search for item 'c' in the inventory of 'b' in the inventory of 'a'.
    public Found findInventory(String fullName) {
        String name1 = findSubstringBeforeDot(fullName);
        String name2 = findSubstringAfterDot(fullName);

        // Search the inventory for name1.
        for (int i = 0; i < inventory.size(); i++) {
            // check if the name of the item is the same as the target name.
            Actor a = inventory.get(i);
            if (a.name.equals(name1)) {
                // Found item with name1. Now check if name2 is empty.
                if (name2.isEmpty()) {
                    // name2 is empty. This means we found the item.
                    return new Found(a, a, "");
                } else {
                    // name2 is not empty. This means we need to search the inventory of a for name2.
                    return a.findInventory(name2);
                }
            }
        }

        // name1 does not exist in the inventory. return null.
        return new Found(null, this, fullName);
    }

    // Take an item from the current actor's inventory list, move it into inventory of 'who'.
    // The rule of the name is the same as findInventory().
    public void take(Actor who, String itemName) {
        Found result = findInventory(itemName);
        Actor item = result.item;
        Actor owner = result.owner;
        if (null != item) {
            if (item.moveable) {
                owner.inventory.remove(item);
                who.inventory.add(item);
                System.out.println("You took the " + item.name + "from " + owner.name);
            } else {
                System.out.println("The " + itemName + " is not movable. You can't take it.");
            }
        }
        else {
            System.out.println("There's no item called " + result.unrecogizedName + " in the " + owner.name + ". If the item is in a container, type \"t <container name>.<item name>\" to take it'.");
        }
    }

    String findSubstringBeforeDot(String s) {
        // 1. search all letters within the string to locate the position of the first
        // ".".
        // example: for "abc.def", the search should return 3.
        //
        // position of string is 0-based, meaning the first letter is at position 0.
        // For example: string "abc" has 3 letters and 4 positions: 0, 1, 2, 3.
        // 0 is in front of the leter "a"
        // 3 is after the letter "c", marks the end of the string.

        int dotPosition = -1; // initialize the dot position to -1, meaning not found.
        for (int i = 0; i < s.length(); i++) { // loop through all letters in the string.
            if ('.' == s.charAt(i)) { // check if the letter is a dot.
                dotPosition = i; // if it's a dot, then remember the position.
                break; // stop the loop, no need to search further.
            }
        }

        // 2. if there's no dot in the string, then return the whole string:
        // example: for "abcd", return "abcd".
        if (-1 == dotPosition)
            return s;

        // 3. for string like ".abcd", should return empty string "".
        if (0 == dotPosition)
            return "";

        // 4. the return a substring from the first letter to the letter before the dot.
        // example: for "abc.def", return substring [0, 2].
        return s.substring(0, dotPosition); // 2nd argument of substr() is the length of the substring.
    }

    String findSubstringAfterDot(String s) {
        // 1. search dot position in the string.
        int dotPosition = -1; // initialize the dot position to -1, meaning not found.
        for (int i = 0; i < s.length(); i++) { // loop through all letters in the string.
            if ('.' == s.charAt(i)) { // check if the letter is a dot.
                dotPosition = i; // if it's a dot, then remember the position.
                break; // stop the loop, no need to search further.
            }
        }

        // 2. if there's no dot in the string, then return empty string:
        // example: for "abcd", return "".
        if (-1 == dotPosition)
            return "";

        // 3. if the dot is at the end of the string
        // For example "abcd.", return empty string "".
        // s.length() = 5.
        // dotPosition = 4.
        // So use (s.length() - 1) to determine if the dot is at the end of the string.
        if (s.length() - 1 == dotPosition)
            return "";

        // 3. the return a substring from the first letter after the dot to the end of
        // the string.
        // Example: for "abc.def", return string "def".
        // In this case, the dotPosition is 3
        // the first leter after the dot is at position 4, which is 3 + 1.
        int firstLetterAfterDot = dotPosition + 1;
        // the substring end is the end of the entire string.
        int subStringEnd = s.length();
        return s.substring(firstLetterAfterDot, subStringEnd);
    }
}

// Represents the player in the game
class Player extends Actor {
    public Room currentRoom;

    public void processCommand(String command, String[] args) {
        // for moving round the room
        if ("n".equals(command)) {
            currentRoom.enter(this, currentRoom.northExit);
        } else if ("s".equals(command)) {
            currentRoom.enter(this, currentRoom.southExit);
        } else if ("u".equals(command)) {
            currentRoom.enter(this, currentRoom.upExit);
        } else if ("d".equals(command)) {
            currentRoom.enter(this, currentRoom.downExit);
        } else if ("e".equals(command)) {
            currentRoom.enter(this, currentRoom.eastExit);
        } else if ("w".equals(command)) {
            currentRoom.enter(this, currentRoom.westExit);
        } else if ("i".equals(command)) {
            System.out.println("You have the following items in your inventory: " + listInventory());
        } else if ("t".equals(command)) {
            if (args.length < 1) {
                // print an error.
                System.out.println("What do you want to take?");
            } else {
                currentRoom.take(this, args[0]);
            }
        } else if ("l".equals(command)) {
            if (args.length > 0) {
                // We got a command like "l <item>". This means the player wants to look at something in the room.
                currentRoom.processCommand("l", args);
            } else {
                // print the description of the current room.
                System.out.println(currentRoom.getDescription());
            }
        } else if ("h".equals(command) || "help".equals(command)) {
            System.out.println(
                    "Your goal is to find what has happened in this house. You can use the following commands:\n" +
                    "   h or help - print this help message\n" +
                    "   q or quit - quit the game\n" +
                    "   n - go to the north\n" +
                    "   s - go to the south\n" +
                    "   e - go to the east\n" +
                    "   w - go to the west\n" +
                    "   u - go up\n" +
                    "   d - go down\n" +
                    "   l - look around.\n" +
                    "   l <item name> - look at something.\n" +
                    "   t <item name> - take an item in a room.\n" +
                    "   t <container name>.<item name> - take an item from a container.\n"
                    );
        } else {
            super.processCommand(command, args);
        }
    }
}

class Container extends Actor {
    public Container() {
        name = "box";
    }

    public String getDescription() {
        String s = super.getDescription();
        if (0 == inventory.size()) {
            s += " It is empty.";
        }
        else if (1 == inventory.size()) {
            // There is only one item in the room.
            s += " There is a " + inventory.get(0).name + " in it.";
        }
        else {
            s += " There are";
            for (int i = 0; i < inventory.size(); i++) {
                if (0 == i) {
                    // this is the first item in the list. No need to insert a comma.
                    s = s + " a " + inventory.get(i).name;
                } else if (i == inventory.size() - 1) {
                    // this is the last item in the list. Insert "and" before the item.
                    s = s + " and a " + inventory.get(i).name;
                } else {
                    // this is an item in the middle of the list. Insert a comma before the item.
                    s = s + ", a " + inventory.get(i).name;
                }
            }
            s += " in it.";
        }
        return s;
    }
}

// Represents a room in the game world
class Room extends Container {
    public boolean locked = false;
    public Room upExit = null;
    public Room downExit = null;
    public Room northExit = null;
    public Room southExit = null;
    public Room eastExit = null;
    public Room westExit = null;

    public String getDescription() {
        String s = super.getDescription();
        if (null != northExit) {
            s += "\n   Go north to " + northExit.name + ".";
        }
        if (null != southExit) {
            s += "\n   Go south to " + southExit.name + ".";
        }
        if (null != eastExit) {
            s += "\n   Go east to " + eastExit.name + ".";
        }
        if (null != westExit) {
            s += "\n   Go west to " + westExit.name + ".";
        }
        if (null != upExit) {
            s += "\n   Go up to " + upExit.name + ".";
        }
        if (null != downExit) {
            s += "\n   Go down to " + downExit.name + ".";
        }
        return s;
    }

    public void enter(Player player, Room room) {
        if (room == null) {
            System.out.println("You can't go that way.");
        } else if (!room.locked) {
            System.out.println("You entered the " + room.name + ".");
            player.currentRoom = room;
            room.processCommand("l", new String[0]);
        } else if (room.hasKey(player)) {
            room.locked = false; // mark the room as unlocked.
            System.out.println("You unlocked the " + room.name + " and entered.");
            player.currentRoom = room;
            room.processCommand("l", new String[0]);
        } else {
            System.out.println("The " + room.name + " is locked. May be you need a key?");
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
        name = "living room";
    }
}

class BasementHallway extends Room {
    public BasementHallway() {
        name = "basement hallway";
    }
}

class storageroom extends Room {
    public storageroom() {
        name = "storage room";
    }

    public boolean hasKey(Player player) {
        // check if the player has the key of the storage room.
        Found found = player.findInventory("StorageRoomKey");
        Actor key = found.item;
        if (null == key) {
            return false; // no item named "StorageRoomKey" in player's inventory, return false.
        }
        // check if the key is an instance of StorageRoomKey.
        else if (key instanceof StorageRoomKey) {
            // yes, this is indeed a StorageRoomKey. return true.
            return true;
        } else {
            // no, this is something else just happen to be called "StorageRoomKey".
            // return false.
            return false;
        }
    }
}

class StorageRoomKey extends Actor {
    public StorageRoomKey() {
        name = "StorageRoomKey";
        description = "This key might help you finish the game.";
    }
}

class staircase extends Room {
    public staircase() {
        name = "staircase";
    }
}

// set up a new room including the action things could do in the room
class bedroom extends Room {
    public bedroom() {
        name = "bedroom";
    }
}

class kitchen extends Room {
    public kitchen() {
        name = "kitchen";
    }
}

class hallway extends Room {
    public hallway() {
        name = "hall way";
    }
}

class guestbedroom extends Room {
    public guestbedroom() {
        name = "guest bedroom";
    }
}

class bathroom extends Room {
    public bathroom() {
        name = "bathroom";
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
        System.out.println("Welcome to this escape room. Type 'h' for how to play. Enjoy!");

        // Run the game loop
        boolean gameOver = false;
        while (!gameOver) {
            // Read the user input
            String input = scanner.nextLine();// read user input
            String[] parts = input.split(" "); // break up user input using " "
            String commandName = parts[0];
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
            if (null == commandArgs) {
                // Do not allow null commandArgs. If it's null, then set it to an empty array.
                commandArgs = new String[0];
            }

            // Process the user input
            if ("q".equals(commandName) || "quit".equals(commandName)) {
                System.out.println("Game Over!"); // if user enter "quit" game over= true
                gameOver = true;
            } else {
                player.processCommand(commandName, commandArgs); // need to use current room to
                                                                                     // process command
            }
        }
    }

    // Setup game level, return the first room that the player is in.
    static Room populateGameWorld() {
        // We'll setup a simple game level that contains 2 rooms, a living room and a
        // bedroom.
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

        // setup connections between rooms.
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

        // Put some funitures in the live room.
        livingRoom.inventory.add(new Actor("TV"));
        livingRoom.inventory.add(new Actor("table"));
        livingRoom.inventory.add(new Actor("sofa"));

        // Create a container with the storage room key in it. Then put the container
        // into the guest bedroom.
        Container container = new Container();
        StorageRoomKey key = new StorageRoomKey();
        key.moveable = true; // make the key moveable. So the player can take it.
        container.inventory.add(key);
        guestbedroom.inventory.add(container);

        // Put a letter into the storage room.
        storageroom.inventory.add(new Actor("letter",
            "This is a letter from the previous owner of the house. It says: \n\n" +
            "\"Hello dear player. You found this letter, which marks the end of the first chapter of this game. Congrats!\n\n" +
            "In the next chapter, you'll be asked to decipher the coordinate that I hide in this letter and go to the indicated location.\n\n" +
            "There's a treasure box waiting for you there.\n\n" +
            "See you soon in the next chapter!\""));

        // Returns the living room as the starting room for the player
        return livingRoom;
    }
}