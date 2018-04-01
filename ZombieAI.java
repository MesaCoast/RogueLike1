import java.io.Serializable;

/**
 * Welcome to the ZombieAI class!
 * Zombies like to beeline straight for the player
 * 
 * 
 * 
 * 
 */
public class ZombieAI extends AI implements Serializable{
    private static final long serialVersionUID = 5008000L;
    private Creature player;

    public ZombieAI(Creature creature, World world, Creature player){
        super(creature, world);
        isFriendly = false;
        this.player = player;
    }

    public void act(){
        if(canSee(player.x, player.y)){                             //If we can see the player, then use a very simple tracking algorithm (typical of a zombie)
            int dx = player.x - creature.x;
            int dy = player.y - creature.y;
            if(Math.abs(dx)>Math.abs(dy)){                          //if we're farther off in the x direction than in the y direction
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //then move horizontally                            //ternary operators (with trig functions?)
            }else if(Math.abs(dx)<Math.abs(dy)||Math.random()<0.5){ //otherwise, if we're farther off in the y direction or we just feel like it
                creature.moveBy(0, dy > 0 ? 1 : -1);                //then move vertically                             //woot woot
            }else{
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //otherwise, go back to the first plan and move horizontally         //java
            }
        }else{
            creature.wander();
        }
    }
}