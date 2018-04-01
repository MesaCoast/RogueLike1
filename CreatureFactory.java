import net.slashie.libjcsi.CSIColor;
import java.io.Serializable;
import java.util.Random;

/**
 * Welcome to the CreatureFactory class!
 * This class takes care of placing new creatures in our world.
 * @version 12/10/2017
 */
public class CreatureFactory implements Serializable{
	private World world;
	private static final long serialVersionUID = 3000000L;
	private Random rand;

	public CreatureFactory(World world){
		this.world = world;
		rand = new Random();
	}

	//Creature(World world, char symbol, CSIColor color, int maxHP, int visRadius, int attackStrength, int xpValue)

	//spawn the player
	public Creature newPlayer(FieldOfView fov, PlayerClass pc){
		int visRadius = 9;
		int maxHP = 20;
		if(pc == PlayerClass.GUNSLINGER)
			visRadius = 11;
		if(pc == PlayerClass.PALADIN)
			maxHP = 30;

		Creature player = new Creature(world, '@', pc.getColor(), maxHP, visRadius, 0, 500);
		world.addAtValidLocation(player);
		new PlayerAI(player, world, fov, pc);
		return player;
	}

	//IT'S A TRAP
	public Creature newTrap(int level){
		Creature trap = new Creature(world, 'X', CSIColor.GRAY, 1, 0, rand.nextInt(4)+2+level, 20 + (2 * (rand.nextInt(4)+2)));
		world.addAtValidLocation(trap);
		new TrapAI(trap, world);
		return trap;
	}

	//spawn a zombie
	public Creature newZombie(int level, Creature player){
		Creature zombie = new Creature(world, 'Z', CSIColor.GREEN, 8 + (2*level), 8, rand.nextInt(4)+2, 20 + (2 * (rand.nextInt(4)+5)));
		world.addAtValidLocation(zombie);
		new ZombieAI(zombie, world, player);
		return zombie;
	}

	//spawn a purple bat
	public Creature newBat(int level){
		Creature bat = new Creature(world, 'b', CSIColor.DARK_VIOLET, 8 + (2*level), 8, rand.nextInt(4)+2, 20 + (2 * (rand.nextInt(4)+4)));
		world.addAtValidLocation(bat);
		new BatAI(bat, world);
		return bat;
	}

	//spawn a cute little dog buddy
	public Creature newDog(int x, int y, Creature owner){
		PlayerAI pai = (PlayerAI) owner.getAI();
		Creature dog = new Creature(world, 'd', CSIColor.BROWN, 1 + (2*pai.getLevel()), owner.getVisRadius(), pai.getLevel() + 1, 0);
		world.addAtLocation(x, y, dog);
		new DogAI(dog, world, owner);
		return dog;
	}
}