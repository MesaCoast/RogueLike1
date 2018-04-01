import net.slashie.libjcsi.CSIColor;
import java.util.Random;
import java.io.Serializable;

/**
 * Welcome to the Creature class!
 * This class is used to represent creatures (whether player, monster, or otherwise)
 * in our dungeon.
 * 
 * 
 * 
 */
public class Creature implements Serializable{
	public int x, y, hp, maxHP, visRadius, attackStrength, xpValue;
	private char symbol;
	protected MyCSIColor color;
	protected AI ai;
	private World world;
	private Random rand;
	private static final long serialVersionUID = 2000000L;

	public Creature(World world, char symbol, CSIColor color, int maxHP, int visRadius, int attackStrength, int xpValue){
		this.symbol = symbol;
		this.color = new MyCSIColor(color);
		this.world = world;
		this.maxHP = maxHP;
		hp = maxHP;
		this.visRadius = visRadius;
		this.attackStrength = attackStrength;
		this.xpValue = xpValue;
		rand = new Random();
	}

	//returns the symbol of the creature
	public char getSymbol(){return symbol;}

	//returns the color of the creature
	public CSIColor getColor(){return color.getCSI();}

	//returns the vision radius of the creature
	public int getVisRadius(){return visRadius;}
	
	//checks if the AI can see the targeted coordinates
	public boolean canSee(int x, int y){return ai.canSee(x, y);}

	//Sets the creature's AI to the designated AI, duh
	public void setAI(AI newAI){ai = newAI;}//Why do this? This way we can have debuffs that influence behavior at some point in the future. (there are other methods of doing that, but it also lets us get a reference to the player's AI)
	
	//returns a reference to this creature's AI
	public AI getAI(){return ai;}

	//This method is called by the AI when it wants to move the creature, and checks all the corner cases.
	public void moveBy(int dx, int dy){
		if(dx == 0&&dy == 0)
			return;
		if(!(getAI() instanceof PlayerAI)&&(world.getTile(x+dx, y+dy) == Tile.EXIT))//prevent creatures other than the player from entering the exit
			return;
		Creature c = world.getCreature(x+dx, y+dy);
		if(c != null&&ai.isFriendly&&c.getAI() instanceof PlayerAI){					//if we're a friendly AI and we're trying to move towards the player, just walk through it
			x += dx;
			y += dy;
			ai.onEnter(x, y);
		}else if(c != null&&ai instanceof PlayerAI&&c.getAI().isFriendly){			//if we're the player and we're obstructed by a dog, just walk through it
			x += dx;
			y += dy;
			ai.onEnter(x, y);
		}else if(c != null&&c.getSymbol() != this.getSymbol()){							//if there is a creature in the targeted spot and it's not the same type as this one, then damage it
			if(c.damage(attackDamage())&&getAI() instanceof PlayerAI){
				PlayerAI pai = (PlayerAI) getAI();//if this creature is the player and it defeated the other creature, then add xp to the player
				pai.addXP(c.xpValue);
			}
			if(c.getAI() instanceof TrapAI){
				damage(c.attackDamage());
				x += dx;
				y += dy;
				ai.onEnter(x, y);
			}
		}else{
			if(!world.getTile(x+dx, y+dy).isTangible()){//if we're not trying to move into a creature of the same isFriendly or a wall, move
				x += dx;
				y += dy;
				ai.onEnter(x, y);
			}
		}
	}

	//moves the creature in a random direction, up to 1 space vertically, horizontally, or diagonally
	public void wander(){
	    int dx = rand.nextInt(3) - 1;
	    int dy = rand.nextInt(3) - 1;
	    moveBy(dx, dy);
	}

	//deals damage damage to this creature
	public boolean damage(int damage){//returns true if the creature dies from the attack, false otherwise
		hp -= damage;
		if(hp <= 0){
			dispose();
			return true;
		}
		return false;
	}

	//removes the creature from the world
	public void dispose(){
		world.removeCreature(this);
		ai.onDeath();
	}

	//Returns an amount of damage within +/-15% of the creature's max damage value, but always >= 1
	public int attackDamage(){
		int damage = attackStrength + (rand.nextInt((int)((double)attackStrength * 0.30) + 1) - ((int)((double)attackStrength * 0.15)));
		return damage > 0 ? damage : 1;
	}

	//Tells the AI to take a turn
	public void act(){ai.act();}
}