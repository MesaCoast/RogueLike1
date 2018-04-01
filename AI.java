import java.io.Serializable;

/**
 * Welcome to the AI class!
 * This class is extended by our different AI types and determines stuff such as the movement of the creature.
 * For most enemy AI's, we'll want to pass the player's Creature object and the ArrayList of creatures to the AI.
 * 
 * 
 * 
 */
public class AI implements Serializable{
    protected Creature creature;
    protected World world;
    protected boolean isFriendly;
    private static final long serialVersionUID = 5000000L;

    public AI(Creature creature, World world){
        this.creature = creature;
        this.world = world;
        this.creature.setAI(this);
    }

    public void onDeath(){}

    public void onEnter(int x, int y){}

    public void act(){}

    public boolean canSee(int x, int y){
    	if(Math.pow((creature.x-x), 2)+Math.pow((creature.y-y), 2) > Math.pow(creature.getVisRadius(), 2))//if it's outside the vision radius
    		return false;

    	for(Point p: (new Line(creature.x, creature.y, x, y)).getPoints()){
    		if(world.getTile(p.x, p.y).isTransparent()||p.x == x&&p.y == y)
    			continue;
    		return false;
    	}
    	return true;
    }
}