import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.util.FileUtil;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.*;

/**
 * Welcome to the Game class!
 * created by
 * @author Sam Coates, sam@ryancoates.com, coatess@purdue.edu
 * on
 * @version 12/8/2017 - 12/12/2017
 * 
 * This class contains the main method for my roguelike game that I started working on on the 7th.
 * It serves to create the map, start the game, accept input, save things, display stuff, etc.
 * This uses the libjcsi library, which displays program output in a console-like window and is covered under the GNU LGPL license.
 * Licensing information for the libjcsi library can be found in the Documents Required by License directory.
 * 
 * GAME stands for GAMEs Are My Everything
 */
public class Game{
	private static int width, height, windowX, windowY, level;
	private static ConsoleSystemInterface csi;//viewable coordinates range from (0, 0) to (79, 24)
	private static boolean update;
	private static Random rand;
	private static boolean[] saveExists;
	private static boolean atLeastOneSave;
	private static CreatureFactory cf;
	private static Creature player;
	private static World world;
	private static FieldOfView fov;
	private static PlayerAI pai;

	//Initializes variables and then proceeds to the main menu
	public static void main(String[] args){
		rand = new Random();

		//choose our properties for the console window
		Properties windowProperties = new Properties();
		windowProperties.setProperty("fontSize","32");//20
		windowProperties.setProperty("font", "Consolas");//Lucida Console

		//create the window itself
		csi = null;
		try{
			csi = new WSwingConsoleInterface("Cave Roguelike Thingy", windowProperties);
		}catch(ExceptionInInitializerError e){
			System.out.println("\nFailed to initialize swing console box :(\n");
			e.printStackTrace();
			System.exit(-1);
		}

		//go to the main menu
		mainMenu();
	}//main()

	//Loads information about the save files available
	private static void loadSaveInfo(){
		saveExists = new boolean[4];
		saveExists[0] = FileUtil.fileExists("File 0.data");
		saveExists[1] = FileUtil.fileExists("File 1.data");
		saveExists[2] = FileUtil.fileExists("File 2.data");
		saveExists[3] = FileUtil.fileExists("File 3.data");
		atLeastOneSave = false;
		for(int i = 0;i < saveExists.length;i++){
			if(saveExists[i])
				atLeastOneSave = true;
		}
	}

	//Displays a menu with several typical main menu options
	private static void mainMenu(){
		int cursorY = 6;
		loadSaveInfo();

		update = true;

		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "Sam's Roguelike Cave Game Thingy", CSIColor.CYAN);	//title

				csi.print(11, 6, "New Game", CSIColor.LAVENDER);							//new game option
				if(atLeastOneSave)
					csi.print(11, 8, "Continue Game", CSIColor.LAVENDER);					//continue option
				else
					csi.print(11, 8, "Continue Game", CSIColor.GRAY);						//gray out the option if there are no saves	
				csi.print(11, 10, "Instructions", CSIColor.LAVENDER);						//instructions option
				csi.print(11, 12, "Credits", CSIColor.LAVENDER);							//credits option
				csi.print(11, 14, "Quit", CSIColor.LAVENDER);							//quit option

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				csi.print(10, cursorY, ">", CSIColor.BRIGHT_GREEN);							//cursors on either side of the user's selection
				csi.print(24, cursorY, "<", CSIColor.BRIGHT_GREEN);
				csi.refresh();
				update = false;
			}

			//Wait for player input
			CharKey dir = csi.inkey();
			if(dir.isUpArrow()){
				switch(cursorY){
					case 6:
						cursorY = 14;
						update = true;
						continue;
					case 8:
						cursorY = 6;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					case 12:
						cursorY = 10;
						update = true;
						continue;
					case 14:
						cursorY = 12;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isDownArrow()){
				switch(cursorY){
					case 6:
						cursorY = 8;
						update = true;
						continue;
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 12;
						update = true;
						continue;
					case 12:
						cursorY = 14;
						update = true;
						continue;
					case 14:
						cursorY = 6;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorY){
					case 6:
						newGame();
						continue;
					case 8:
						if(atLeastOneSave)
							continueGame();
						continue;
					case 10:
						instructions();
						continue;
					case 12:
						credits();
						continue;
					case 14:
						userQuit();
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method displays instructions
	private static void instructions(){
		csi.cls();
		csi.print(5, 3, "INSTRUCTIONS:", CSIColor.CYAN);

		csi.print(11, 6, "Use the WASD keys to move.", CSIColor.LAVENDER);
		csi.print(11, 7, "Use the P key to pause.", CSIColor.LAVENDER);
		csi.print(11, 8, "Use the ESC key to quit.", CSIColor.LAVENDER);
		csi.print(11, 9, "Use the arrow keys to attack.", CSIColor.LAVENDER);

		csi.print(11, 11, "You're using the developer version,", CSIColor.LAVENDER);
		csi.print(11, 12, "so you can also use the R key to", CSIColor.LAVENDER);
		csi.print(11, 13, "generate a new level immediately.", CSIColor.LAVENDER);

		csi.print(19, 20, "Press any key to return to the menu.", CSIColor.LIGHT_GRAY);
		csi.refresh();

		csi.inkey();
		update = true;
	}

	//This method displays credits
	private static void credits(){
		csi.cls();
		csi.print(5, 3, "CREDITS:", CSIColor.CYAN);

		csi.print(8, 6, "This game was coded by Sam Coates from 12/7/2017 to 12/9/2017.", CSIColor.LAVENDER);

		csi.print(8, 8, "It uses libjcsi (the Java Console System Interface Library),", CSIColor.LAVENDER);
		csi.print(8, 9, "which is covered by the GNU Lesser General Public License.", CSIColor.LAVENDER);
		csi.print(8, 10, "For more information on this license, check the Documents", CSIColor.LAVENDER);
		csi.print(8, 11, "Required by License directory.", CSIColor.LAVENDER);

		csi.print(8, 13, "Sam can be contacted at sam@ryancoates.com or coatess@purdue.edu.", CSIColor.LAVENDER);
		csi.print(8, 14, "Have fun playing the game!", CSIColor.LAVENDER);

		csi.print(19, 20, "Press any key to return to the main menu.", CSIColor.LIGHT_GRAY);
		csi.refresh();

		csi.inkey();
		mainMenu();
	}

	//This method creates a new map and starts the game
	private static void newGame(){
		//temporarily hide the screen
		csi.cls();
		csi.print(1, 1, "Generating world...", CSIColor.WHITE);
		csi.print(1, 3, "Building terrain...", CSIColor.WHITE);
		csi.refresh();

		world = new WorldBuilder(30, 30)
					.makeCaves()
					.build();

		cf = new CreatureFactory(world);

		//the player's field of view
		fov = new FieldOfView(world);
		//the player

		PlayerClass pc = newCharacter();//choose a class
		player = cf.newPlayer(fov, pc);	//make the player
		pai = (PlayerAI) player.getAI();//get a reference to the player's AI

		for(int i = 0;i < 30;i++){
			cf.newTrap(level);
		}
		for(int i = 0;i < 6;i++){
			cf.newZombie(level, player);
		}
		for(int i = 0;i < 12;i++){
			cf.newBat(level);
		}

		//track the number of levels completed so far
		level = 0;

		playGame();
	}

	//This method allows the user to choose a class
	private static PlayerClass newCharacter(){
		update = true;
		int cursorX = 31;

		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "Choose a class:", CSIColor.CYAN);

				csi.print(cursorX - 1, 8, "> <", CSIColor.BRIGHT_GREEN);	//the cursor

				csi.print(31, 8, '@', PlayerClass.WIZARD.getColor());
				csi.print(35, 8, '@', PlayerClass.PALADIN.getColor());
				csi.print(39, 8, '@', PlayerClass.GUNSLINGER.getColor());
				csi.print(43, 8, '@', PlayerClass.GRENADIER.getColor());
				csi.print(47, 8, '@', PlayerClass.SUMMONER.getColor());

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				switch(cursorX){
					case 31:
						csi.print(8, 10, "WIZARD", PlayerClass.WIZARD.getColor());
						csi.print(10, 12, "The wizard is magical and shoots magic.", CSIColor.LAVENDER);
						break;
					case 35:
						csi.print(8, 10, "PALADIN", PlayerClass.PALADIN.getColor());
						csi.print(10, 12, "The paladin is a knight type person with a sword.", CSIColor.LAVENDER);
						break;
					case 39:
						csi.print(8, 10, "GUNSLINGER", PlayerClass.GUNSLINGER.getColor());
						csi.print(10, 12, "The gunslinger is an old westerny dude and shoots bullets.", CSIColor.LAVENDER);
						break;
					case 43:
						csi.print(8, 10, "GRENADIER", PlayerClass.GRENADIER.getColor());
						csi.print(10, 12, "The grenadier is explodey and shoots explosives.", CSIColor.LAVENDER);
						break;
					case 47:
						csi.print(8, 10, "SUMMONER", PlayerClass.SUMMONER.getColor());
						csi.print(10, 12, "The summoner is summony and summons.", CSIColor.LAVENDER);
						break;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}

				csi.refresh();
			}

			CharKey dir = csi.inkey();
			if(dir.isLeftArrow()){
				switch(cursorX){
					case 31:
						cursorX = 47;
						update = true;
						continue;
					case 35:
						cursorX = 31;
						update = true;
						continue;
					case 39:
						cursorX = 35;
						update = true;
						continue;
					case 43:
						cursorX = 39;
						update = true;
						continue;
					case 47:
						cursorX = 43;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isRightArrow()){
				switch(cursorX){
					case 31:
						cursorX = 35;
						update = true;
						continue;
					case 35:
						cursorX = 39;
						update = true;
						continue;
					case 39:
						cursorX = 43;
						update = true;
						continue;
					case 43:
						cursorX = 47;
						update = true;
						continue;
					case 47:
						cursorX = 31;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorX){
					case 31:
						return PlayerClass.WIZARD;
					case 35:
						return PlayerClass.PALADIN;
					case 39:
						return PlayerClass.GUNSLINGER;
					case 43:
						return PlayerClass.GRENADIER;
					case 47:
						return PlayerClass.SUMMONER;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method brings up a load menu and allows the user to choose a file to load the game from, starting the game if they select one, or returning otherwise
	private static void continueGame(){
		int cursorY = 6;
		update = true;

		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "Select a file to load:", CSIColor.CYAN);	//title

				//print the available options in lavender, and the other ones in gray
				if(saveExists[0])
					csi.print(11, 6, "File 0", CSIColor.LAVENDER);
				else
					csi.print(11, 6, "File 0", CSIColor.GRAY);
				if(saveExists[1])
					csi.print(11, 8, "File 1", CSIColor.LAVENDER);
				else
					csi.print(11, 8, "File 1", CSIColor.GRAY);
				if(saveExists[2])
					csi.print(11, 10, "File 2", CSIColor.LAVENDER);
				else
					csi.print(11, 10, "File 2", CSIColor.GRAY);
				if(saveExists[3])
					csi.print(11, 12, "File 3", CSIColor.LAVENDER);
				else
					csi.print(11, 12, "File 3", CSIColor.GRAY);

				csi.print(11, 15, "Cancel", CSIColor.LAVENDER);

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				csi.print(10, cursorY, ">", CSIColor.BRIGHT_GREEN);							//cursors on either side of the user's selection
				csi.print(17, cursorY, "<", CSIColor.BRIGHT_GREEN);

				csi.refresh();
				update = false;
			}

			//Wait for player input
			CharKey dir = csi.inkey();
			if(dir.isUpArrow()){
				switch(cursorY){
					case 6:
						cursorY = 15;
						update = true;
						continue;
					case 8:
						cursorY = 6;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					case 12:
						cursorY = 10;
						update = true;
						continue;
					case 15:
						cursorY = 12;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isDownArrow()){
				switch(cursorY){
					case 6:
						cursorY = 8;
						update = true;
						continue;
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 12;
						update = true;
						continue;
					case 12:
						cursorY = 15;
						update = true;
						continue;
					case 15:
						cursorY = 6;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorY){
					case 6:
						if(!saveExists[0])
							continue;
						loadFromFile("File 0");
						playGame();
					case 8:
						if(!saveExists[1])
							continue;
						loadFromFile("File 1");
						playGame();
					case 10:
						if(!saveExists[2])
							continue;
						loadFromFile("File 2");
						playGame();
					case 12:
						if(!saveExists[3])
							continue;
						loadFromFile("File 3");
						playGame();
					case 15:
						update = true;
						return;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method loads the data from the specified file into the current game
	private static void loadFromFile(String fileName){
		try(
			FileInputStream fis = new FileInputStream(fileName + ".data");
			ObjectInputStream ois = new ObjectInputStream(fis);
		){//width, height, level, cf, player, world, fov
			width = ois.readInt();
			height = ois.readInt();
			level = ois.readInt();
			cf = (CreatureFactory) ois.readObject();
			player = (Creature) ois.readObject();

			pai = (PlayerAI) player.getAI();

			world = (World) ois.readObject();
			fov = (FieldOfView) ois.readObject();

			ois.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Failed to load game!!!");
		}
	}

	//Brings up a menu with several typical pause menu options
	private static void pauseMenu(){
		int cursorY = 6;
		loadSaveInfo();
		boolean saved = false;

		update = true;
		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "PAUSE MENU", CSIColor.CYAN);	//title

				csi.print(11, 6, "Continue", CSIColor.LAVENDER);							//continue option
				csi.print(11, 8, "Save Game", CSIColor.LAVENDER);							//save option
				csi.print(11, 10, "Exit to Title", CSIColor.LAVENDER);						//exit to title option
				csi.print(11, 12, "Exit to Desktop", CSIColor.LAVENDER);					//exit to desktop option
				csi.print(11, 14, "Instructions", CSIColor.LAVENDER);						//instructions option

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				csi.print(10, cursorY, ">", CSIColor.BRIGHT_GREEN);							//cursors on either side of the user's selection
				csi.print(26, cursorY, "<", CSIColor.BRIGHT_GREEN);

				if(saved)
					csi.print(1, 24, "Game saved.", CSIColor.BRIGHT_GREEN);

				csi.refresh();
				update = false;
			}

			//Wait for player input
			CharKey dir = csi.inkey();
			if(dir.isUpArrow()){
				switch(cursorY){
					case 6:
						cursorY = 14;
						update = true;
						continue;
					case 8:
						cursorY = 6;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					case 12:
						cursorY = 10;
						update = true;
						continue;
					case 14:
						cursorY = 12;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isDownArrow()){
				switch(cursorY){
					case 6:
						cursorY = 8;
						update = true;
						continue;
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 12;
						update = true;
						continue;
					case 12:
						cursorY = 14;
						update = true;
						continue;
					case 14:
						cursorY = 6;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorY){
					case 6:
						playGame();
						continue;
					case 8:
						saved = saveGame();
						update = true;
						continue;
					case 10:
						if(!saved)
							noSaveWarning();
						mainMenu();
						continue;
					case 12:
						if(!saved)
							noSaveWarning();
						userQuit();
						continue;
					case 14:
						instructions();
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method brings up a save menu and allows the user to choose a save file for the game, returning true if they save, and false otherwise
	private static boolean saveGame(){
		int cursorY = 6;
		update = true;

		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "Select a save file:", CSIColor.CYAN);	//title

				csi.print(11, 6, "File 0", CSIColor.LAVENDER);
				csi.print(11, 8, "File 1", CSIColor.LAVENDER);
				csi.print(11, 10, "File 2", CSIColor.LAVENDER);
				csi.print(11, 12, "File 3", CSIColor.LAVENDER);
				csi.print(11, 15, "Cancel", CSIColor.LAVENDER);

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				csi.print(10, cursorY, ">", CSIColor.BRIGHT_GREEN);							//cursors on either side of the user's selection
				csi.print(17, cursorY, "<", CSIColor.BRIGHT_GREEN);

				csi.refresh();
				update = false;
			}

			//Wait for player input
			CharKey dir = csi.inkey();
			if(dir.isUpArrow()){
				switch(cursorY){
					case 6:
						cursorY = 15;
						update = true;
						continue;
					case 8:
						cursorY = 6;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					case 12:
						cursorY = 10;
						update = true;
						continue;
					case 15:
						cursorY = 12;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isDownArrow()){
				switch(cursorY){
					case 6:
						cursorY = 8;
						update = true;
						continue;
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 12;
						update = true;
						continue;
					case 12:
						cursorY = 15;
						update = true;
						continue;
					case 15:
						cursorY = 6;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorY){
					case 6:
						return saveToFile("File 0");
					case 8:
						return saveToFile("File 1");
					case 10:
						return saveToFile("File 2");
					case 12:
						return saveToFile("File 3");
					case 15:
						return false;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method saves the data for the current game to the selected file, returning true if it succeeds, and false otherwise
	private static boolean saveToFile(String fileName){
		try(
			FileOutputStream fos = new FileOutputStream(fileName + ".data");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		){//width, height, level, cf, player, world, fov
			//Remember to read the fields in the same order that we write the fields to the file
			oos.writeInt(width);
			oos.writeInt(height);
			oos.writeInt(level);
			oos.writeObject(cf);
			oos.writeObject(player);
			oos.writeObject(world);
			oos.writeObject(fov);

			oos.flush();
			oos.close();
			fos.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//This method warns the user that they should save their game, and allows them to select OK or CANCEL. CANCEL calls the pauseMenu() method
	private static void noSaveWarning(){
		int cursorY = 8;
		update = true;

		while(true){
			if(update){
				csi.cls();
				csi.print(5, 3, "WARNING!", CSIColor.RED);									//title
				csi.print(5, 5, "Your game might not have been saved. Are you sure you want to quit?", CSIColor.YELLOW);

				csi.print(11, 8, "Yes", CSIColor.LAVENDER);
				csi.print(11, 10, "Cancel", CSIColor.LAVENDER);

				csi.print(15, 20, "ENTER to select, arrow keys to change selection", CSIColor.LIGHT_GRAY);	//directions for using the menu

				csi.print(10, cursorY, ">", CSIColor.BRIGHT_GREEN);							//cursors on either side of the user's selection
				csi.print(17, cursorY, "<", CSIColor.BRIGHT_GREEN);

				csi.refresh();
				update = false;
			}

			//Wait for player input
			CharKey dir = csi.inkey();
			if(dir.isUpArrow()){
				switch(cursorY){
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
			if(dir.isDownArrow()){
				switch(cursorY){
					case 8:
						cursorY = 10;
						update = true;
						continue;
					case 10:
						cursorY = 8;
						update = true;
						continue;
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}

			if(dir.code == CharKey.ENTER){
				switch(cursorY){
					case 8:
						return;
					case 10:
						pauseMenu();
					default:
						System.out.println("Invalid cursor position!!!");
						System.exit(-1);
				}
			}
		}
	}

	//This method should be called in order to place the player into an actual game, using the class constants defined above
	private static void playGame(){
		//Main loop of the program, where we check for player input, update stuff, etc.
		updateScreen();
		while(true){
			//check if the player has reached the exit
			if(world.getTile(player.x, player.y) == Tile.EXIT){nextLevel();}

			//check if you ded
			if(player.hp <= 0){gameOver();}

			//update the player's location
			CharKey dir = csi.inkey();
			if((dir.code == CharKey.w||dir.code == CharKey.W)&&(!world.getTile(player.x, player.y-1).isTangible())){
				player.moveBy(0, -1);
				world.act();
				updateScreen();
				continue;
			}else if((dir.code == CharKey.s||dir.code == CharKey.S)&&(!world.getTile(player.x, player.y+1).isTangible())){
				player.moveBy(0, 1);
				world.act();
				updateScreen();
				continue;
			}else if((dir.code == CharKey.a||dir.code == CharKey.A)&&(!world.getTile(player.x-1, player.y).isTangible())){
				player.moveBy(-1, 0);
				world.act();
				updateScreen();
				continue;
			}else if((dir.code == CharKey.d||dir.code == CharKey.D)&&(!world.getTile(player.x+1, player.y).isTangible())){
				player.moveBy(1, 0);
				world.act();
				updateScreen();
				continue;
			}

			//attack if the user pressed an arrow key
			if(dir.isUpArrow()||dir.isDownArrow()||dir.isRightArrow()||dir.isLeftArrow()){
				attack(dir);
				world.act();
				updateScreen();
				continue;
			}

			//pause the game
			if(dir.code == CharKey.p){
				pauseMenu();
				continue;
			}

			//quit the game
			if(dir.code == CharKey.ESC){
				userQuit();
			}

			//build a new map
			if(dir.code == CharKey.r){
				nextLevel();
				updateScreen();
				continue;
			}
		}
	}

	//This method draws the player's attack thing on screen and damages enemies
	private static void attack(CharKey dir){
		if(pai.getPlayerClass() == PlayerClass.WIZARD){
			if(dir.isDownArrow()){
				for(int y = player.y + 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < player.getVisRadius());y++){
					csi.print(player.x - windowX, y - windowY, "|", CSIColor.CYAN);
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(25);}catch(Exception e){}
				}
			}else if(dir.isUpArrow()){
				for(int y = player.y - 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < player.getVisRadius());y--){
					csi.print(player.x - windowX, y - windowY, "|", CSIColor.CYAN);
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(25);}catch(Exception e){}
				}
			}else if(dir.isRightArrow()){
				for(int x = player.x + 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < player.getVisRadius());x++){
					csi.print(x - windowX, player.y - windowY, "-", CSIColor.CYAN);
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(25);}catch(Exception e){}
				}
			}else if(dir.isLeftArrow()){
				for(int x = player.x - 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < player.getVisRadius());x--){
					csi.print(x - windowX, player.y - windowY, "-", CSIColor.CYAN);
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(25);}catch(Exception e){}
				}
			}

		}else if(pai.getPlayerClass() == PlayerClass.PALADIN){
			if(dir.isDownArrow()){
				for(int y = player.y + 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < 3);y++){
					csi.print(player.x - windowX, y - windowY, "|", CSIColor.SILVER);
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isUpArrow()){
				for(int y = player.y - 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < 3);y--){
					csi.print(player.x - windowX, y - windowY, "|", CSIColor.SILVER);
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isRightArrow()){
				for(int x = player.x + 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < 3);x++){
					csi.print(x - windowX, player.y - windowY, "-", CSIColor.SILVER);
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isLeftArrow()){
				for(int x = player.x - 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < 3);x--){
					csi.print(x - windowX, player.y - windowY, "-", CSIColor.SILVER);
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}

		}else if(pai.getPlayerClass() == PlayerClass.GUNSLINGER){
			if(dir.isDownArrow()){
				for(int y = player.y + 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < player.getVisRadius());y++){
					updateScreen();
					csi.print(player.x - windowX, y - windowY, ".", CSIColor.WHITE);
					csi.refresh();
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					if(c != null)
						break;
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isUpArrow()){
				for(int y = player.y - 1;!world.getTile(player.x, y).isTangible()&&(Math.abs(player.y - y) < player.getVisRadius());y--){
					updateScreen();
					csi.print(player.x - windowX, y - windowY, ".", CSIColor.WHITE);
					csi.refresh();
					Creature c = world.getCreature(player.x, y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					if(c != null)
						break;
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isRightArrow()){
				for(int x = player.x + 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < player.getVisRadius());x++){
					updateScreen();
					csi.print(x - windowX, player.y - windowY, ".", CSIColor.WHITE);
					csi.refresh();
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					if(c != null)
						break;
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}else if(dir.isLeftArrow()){
				for(int x = player.x - 1;!world.getTile(x, player.y).isTangible()&&(Math.abs(player.x - x) < player.getVisRadius());x--){
					updateScreen();
					csi.print(x - windowX, player.y - windowY, ".", CSIColor.WHITE);
					csi.refresh();
					Creature c = world.getCreature(x, player.y);
					if(c != null&&c.damage(player.attackDamage()))	//if the attack defeats a creature
						pai.addXP(c.xpValue);
					if(c != null)
						break;
					csi.refresh();
					try{Thread.sleep(35);}catch(Exception e){}
				}
			}

		}else if(pai.getPlayerClass() == PlayerClass.GRENADIER){
			if(dir.isDownArrow()){
				projectileLoop:
				for(int y = player.y + 1; ;y++){
					//if it explodes
					if(world.getTile(player.x, y).isTangible()||world.getCreature(player.x, y) != null||Math.abs(player.y - y) >= player.getVisRadius()-1){
						//cycle through everything in the blast radius
						for(int blastX = player.x-1;blastX <= player.x+1;blastX++){
							for(int blastY = y-1;blastY <= y+1;blastY++){
								Creature c = world.getCreature(blastX, blastY);
								if(c != null&&c.damage(player.attackDamage()))		//damage a creature and take its xp if you defeat it
									pai.addXP(c.xpValue);
								world.destroyTile(blastX, blastY);					//destroy the tiles you hit with the explosion
							}
						}
						//display an explosion effect
						displayExplosion(player.x-windowX, y-windowY);
						
						//exit the loops
						break projectileLoop;
					}
					updateScreen();
					csi.print(player.x - windowX, y - windowY, "*", CSIColor.ORANGE);
					csi.refresh();
					try{Thread.sleep(45);}catch(Exception e){}
				}
				fov.update(player.x, player.y, player.getVisRadius());
			}else if(dir.isUpArrow()){
				projectileLoop:
				for(int y = player.y - 1; ;y--){
					//if it explodes
					if(world.getTile(player.x, y).isTangible()||world.getCreature(player.x, y) != null||Math.abs(player.y - y) >= player.getVisRadius()-1){
						//cycle through everything in the blast radius
						for(int blastX = player.x-1;blastX <= player.x+1;blastX++){
							for(int blastY = y-1;blastY <= y+1;blastY++){
								Creature c = world.getCreature(blastX, blastY);
								if(c != null&&c.damage(player.attackDamage()))		//damage a creature and take its xp if you defeat it
									pai.addXP(c.xpValue);
								world.destroyTile(blastX, blastY);					//destroy the tiles you hit with the explosion
							}
						}
						//display an explosion effect
						displayExplosion(player.x-windowX, y-windowY);
						
						//exit the loops
						break projectileLoop;
					}
					updateScreen();
					csi.print(player.x - windowX, y - windowY, "*", CSIColor.ORANGE);
					csi.refresh();
					try{Thread.sleep(45);}catch(Exception e){}
				}
				fov.update(player.x, player.y, player.getVisRadius());
			}else if(dir.isRightArrow()){
				projectileLoop:
				for(int x = player.x + 1; ;x++){
					//if it explodes
					if(world.getTile(x, player.y).isTangible()||world.getCreature(x, player.y) != null||Math.abs(player.x - x) >= player.getVisRadius()-1){
						//cycle through everything in the blast radius
						for(int blastX = x-1;blastX <= x+1;blastX++){
							for(int blastY = player.y-1;blastY <= player.y+1;blastY++){
								Creature c = world.getCreature(blastX, blastY);
								if(c != null&&c.damage(player.attackDamage()))		//damage a creature and take its xp if you defeat it
									pai.addXP(c.xpValue);
								world.destroyTile(blastX, blastY);					//destroy the tiles you hit with the explosion
							}
						}
						//display an explosion effect
						displayExplosion(x-windowX, player.y-windowY);

						//exit the loops
						break projectileLoop;
					}
					updateScreen();
					csi.print(x - windowX, player.y - windowY, "*", CSIColor.ORANGE);
					csi.refresh();
					try{Thread.sleep(45);}catch(Exception e){}
				}
				fov.update(player.x, player.y, player.getVisRadius());
			}else if(dir.isLeftArrow()){
				projectileLoop:
				for(int x = player.x - 1; ;x--){
					//if it explodes
					if(world.getTile(x, player.y).isTangible()||world.getCreature(x, player.y) != null||Math.abs(player.x - x) >= player.getVisRadius()-1){
						//cycle through everything in the blast radius
						for(int blastX = x-1;blastX <= x+1;blastX++){
							for(int blastY = player.y-1;blastY <= player.y+1;blastY++){
								Creature c = world.getCreature(blastX, blastY);
								if(c != null&&c.damage(player.attackDamage()))		//damage a creature and take its xp if you defeat it
									pai.addXP(c.xpValue);
								world.destroyTile(blastX, blastY);					//destroy the tiles you hit with the explosion
							}
						}
						//display an explosion effect
						displayExplosion(x-windowX, player.y-windowY);

						//exit the loops
						break projectileLoop;
					}
					updateScreen();
					csi.print(x - windowX, player.y - windowY, "*", CSIColor.ORANGE);
					csi.refresh();
					try{Thread.sleep(45);}catch(Exception e){}
				}
				fov.update(player.x, player.y, player.getVisRadius());
			}

		}else{//PlayerClass.SUMMONER
			if(dir.isUpArrow()){
				if(world.getTile(player.x, player.y-1) == Tile.FLOOR)
					cf.newDog(player.x, player.y-1, player);
				else
					cf.newDog(player.x, player.y, player);
			}else if(dir.isDownArrow()){
				if(world.getTile(player.x, player.y+1) == Tile.FLOOR)
					cf.newDog(player.x, player.y+1, player);
				else
					cf.newDog(player.x, player.y, player);
			}else if(dir.isLeftArrow()){
				if(world.getTile(player.x-1, player.y) == Tile.FLOOR)
					cf.newDog(player.x-1, player.y, player);
				else
					cf.newDog(player.x, player.y, player);
			}else if(dir.isRightArrow()){
				if(world.getTile(player.x+1, player.y) == Tile.FLOOR)
					cf.newDog(player.x+1, player.y, player);
				else
					cf.newDog(player.x, player.y, player);
			}
		}

		try{Thread.sleep(30);}catch(Exception e){}
		updateScreen();
	}

	//displays a 3x3 explosion centered around the given on-screen coordinates, throwing errors if you specify coordinates on the border of or off the screen
	private static void displayExplosion(int screenX, int screenY){
		for(int i = 0;i <= 50;i++){
			csi.print(screenX+rand.nextInt(3)-1, screenY+rand.nextInt(3)-1, '#', randExplosionColor());
			csi.refresh();
			try{Thread.sleep(2);}catch(Exception e){}
		}
	}

	//this method, as you might imagine, returns a random color which might commonly be found in an explosion
	private static CSIColor randExplosionColor(){
		switch(rand.nextInt(20)){
			case 1:
				return CSIColor.AMBER;
			case 2:
				return CSIColor.ATOMIC_TANGERINE;
			case 3:
				return CSIColor.AUBURN;
			case 4:
				return CSIColor.MUSTARD;
			case 5:
				return CSIColor.BURNT_ORANGE;
			case 6:
				return CSIColor.CARDINAL;
			case 7:
				return CSIColor.CARROT_ORANGE;
			case 8:
				return CSIColor.CRIMSON;
			case 9:
				return CSIColor.GRAY;
			case 10:
				return CSIColor.WHITE;
			case 11:
				return CSIColor.DARK_GRAY;
			case 12:
				return CSIColor.LIGHT_GRAY;
			case 13:
				return CSIColor.RED;
			case 14:
				return CSIColor.DARK_RED;
			case 15:
				return CSIColor.NAVAJO_WHITE;
			case 16:
				return CSIColor.ORANGE_PEEL;
			case 17:
				return CSIColor.ORANGE_RED;
			case 18:
				return CSIColor.SAFETY_ORANGE;
			case 19:
				return CSIColor.YELLOW;
			default:
				return CSIColor.ORANGE;
		}
	}

	//This method draws the in-game screen
	private static void updateScreen(){
		csi.cls();
		setWindowCoords();
		printWorld();
		csi.print(39, 12, '@', pai.getPlayerClass().getColor());//39, 12								//the player
		csi.print(70, 24,"HP: " + player.hp + "/" + player.maxHP, CSIColor.RED);						//the player's HP
		csi.print(0, 24, "Level: " + pai.getLevel() + " XP: " + pai.getXP() + "/1000", CSIColor.GOLD);	//the player's level and XP
		csi.print(0, 0, "Attack Strength: " + player.attackStrength, CSIColor.ORANGE_RED);
		csi.refresh();
	}

	//This method increments the level, creates a new map for the level, and starts the game
	private static void nextLevel(){
		level++;
		world.setMap(new WorldBuilder(30, 30)
					.makeCaves()
					.build()
					.getMap());

		world.resetCreatures();
		for(int i = 0;i < 37;i++){
			cf.newTrap(level);
		}
		for(int i = 0;i < 10;i++){
			cf.newZombie(level, player);
		}
		for(int i = 0;i < 15;i++){
			cf.newBat(level);
		}

		world.addAtValidLocation(player);
		fov = new FieldOfView(world);
		pai.setFOV(fov);
		
		pai.addXP(333);

		player.hp = player.maxHP;

		playGame();
	}

	//determines the coordinates of the upper left hand corner of the window with respect to the map
	private static void setWindowCoords(){
		windowX = player.x - 39;
		windowY = player.y - 12;
	}

	//displays the world to the console
	private static void printWorld(){
		fov.update(player.x, player.y, player.getVisRadius());

		for(int r = windowY;r < windowY + 25;r++){
			for(int c = windowX;c < windowX + 79;c++){
				if(fov.isVisible(c, r))//the same thing as player.canSee(c, r)
					csi.print(c - windowX, r - windowY, world.getSymbol(c, r), world.getColor(c, r));
				else
					csi.print(c - windowX, r - windowY, fov.getTile(c, r).getSymbol(), CSIColor.DARK_GRAY);
			}
		}
		csi.refresh();
	}

	//flashes the screen with some alarming randomness
	private static void flash(){
		csi.cls();
		/*for(int x = 0;x < 80;x++){//including this section covers the screen and then *twinkles*
			for(int y = 0;y < 25;y++){
				csi.print(x, y, "#", new CSIColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
			}
		}
		csi.refresh();*/

		for(int n = 0;n < 3000;n++){
			try{
				Thread.sleep(1);
			}catch(Exception e){
				//e.printStackTrace();
			}
			csi.print(rand.nextInt(80), rand.nextInt(25), "#", new CSIColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
			csi.refresh();
		}
	}

	//prints "Game Over" and exits to the main menu
	private static void gameOver(){
		flash();
		csi.cls();

		csi.print(35, 11, "GAME OVER", CSIColor.DARK_RED);
		csi.print(30, 13, "Levels completed: " + level, CSIColor.DARK_RED);
		csi.refresh();
		try{
			Thread.sleep(3000);
		}catch(Exception e){
			//e.printStackTrace();
		}
		mainMenu();
	}

	//prints "Goodbye!!!" and exits the game
	private static void userQuit(){
		//exit the program
		csi.cls();
		csi.print(35, 12, "Goodbye!!!", CSIColor.WHITE);
		csi.refresh();
		try{
			Thread.sleep(3000);
		}catch(Exception e){
			//e.printStackTrace();
		}
		System.exit(0);
	}
}