import java.io.Serializable;

/**
 * The player ai does about as much as you would expect it to.
 * 
 */
public class PlayerAI extends AI implements Serializable{
	private FieldOfView fov;
	private static final long serialVersionUID = 50000002L;
	private int xp, level;
	protected int minions;
	private PlayerClass pc;

    public PlayerAI(Creature creature, World world, FieldOfView fov, PlayerClass pc){
        super(creature, world);
        isFriendly = true;
        this.fov = fov;
        this.pc = pc;
        xp = minions = 0;

        if(pc == PlayerClass.WIZARD){
        	creature.attackStrength = 7;
        }else if(pc == PlayerClass.PALADIN){
        	creature.attackStrength = 10;
        }else if(pc == PlayerClass.GUNSLINGER){
        	creature.attackStrength = 7;
        }else if(pc == PlayerClass.GRENADIER){
        	creature.attackStrength = 5;
        }else if(pc == PlayerClass.SUMMONER){
        	creature.attackStrength = 5;
        }
    }

    @Override
    public boolean canSee(int x, int y){
    	return fov.isVisible(x, y);
    }

    public void setFOV(FieldOfView fov){this.fov = fov;}

    //returns a number representing the amount of levels gained from the xp increase
    public int addXP(int xpToAdd){
    	xp += xpToAdd;
    	int gainedLevels = 0;

    	while(xp >= 1000){
    		xp -= 1000;
    		gainedLevels++;

    		if(creature.attackStrength * 1.2 > creature.attackStrength)//increase base attack strength by 20% if that would make a difference, or by 1 otherwise
    			creature.attackStrength *= 1.2;
    		else
    			creature.attackStrength += 1;

    		if(creature.maxHP * 1.2 > creature.maxHP)
    			creature.maxHP *= 1.2;
    		else
    			creature.maxHP += 1;

    		creature.hp = creature.maxHP;
    	}
    	level += gainedLevels;

    	return gainedLevels;
    }

    public int getLevel(){return level;}

    public int getXP(){return xp;}

    public PlayerClass getPlayerClass(){return pc;}
}