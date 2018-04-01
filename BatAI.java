import java.io.Serializable;

/**
 * Welcome to the BatAI class!
 * Bats like to do bat things. Like move around randomly, I guess.
 * 
 * Overall, they're not very strong because they don't target the player.
 * However, if the player doesn't bother dealing with them until they're everywhere, then they're in for a bad time
 * 
 */
public class BatAI extends AI implements Serializable{
    private static final long serialVersionUID = 5009000L;

    public BatAI(Creature creature, World world){
        super(creature, world);
        isFriendly = false;
    }

    public void act(){
        creature.wander();
        if(Math.random() > 0.25)
            creature.wander();
    }
}