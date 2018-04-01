import java.io.Serializable;
import java.util.ArrayList;

/**
 * Welcome to the DogAI class!
 * Dogs are friendly. Woof woof!
 * 
 * They are summoned by the summoner in a summony fashion.
 */
public class DogAI extends AI implements Serializable{
    private static final long serialVersionUID = 5010000L;
    private Creature owner;

    public DogAI(Creature creature, World world, Creature owner){
        super(creature, world);
        isFriendly = true;
        this.owner = owner;

        //we can't have more than 4 dogs at a time :(
        PlayerAI pai = (PlayerAI) owner.getAI();
        pai.minions++;
        if(pai.minions > 4)
            creature.dispose();
    }

    public void act(){
        ArrayList<Creature> creatures = (ArrayList) world.getCreatures();
        Creature c = null;

        //find the closest enemy the dog can see, while counting all of the dogs
        for(int i = 0;i < creatures.size();i++){
            if(creatures.get(i).getAI().isFriendly)                         //skip the friendly creatures
                continue;
            if(!canSee(creatures.get(i).x, creatures.get(i).y))     //skip the enemies we can't see
                continue;
            if(c == null)                                           //if we don't yet have a target, initially target the first enemy we see
                c = creatures.get(i);
            else if(Math.sqrt((creatures.get(i).x * creatures.get(i).x) + (creatures.get(i).y * creatures.get(i).y)) < Math.sqrt((c.x * c.x) + (c.y * c.y)))//but if we find a closer enemy, target that one instead
                c = creatures.get(i);
        }

        if(c != null){                      //if we can see an enemy, move towards them and attack
            int dx = c.x - creature.x;
            int dy = c.y - creature.y;
            if(Math.abs(dx)>Math.abs(dy)){                          //if we're farther off in the x direction than in the y direction
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //then move horizontally                            //ternary operators (with trig functions?)
            }else if(Math.abs(dx)<Math.abs(dy)||Math.random()<0.5){ //otherwise, if we're farther off in the y direction or we just feel like it
                creature.moveBy(0, dy > 0 ? 1 : -1);                //then move vertically                             //woot woot
            }else{
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //otherwise, go back to the first plan and move horizontally         //java
            }
        }else{                              //if we can't see an enemy, then follow the owner
            int dx = owner.x - creature.x;
            int dy = owner.y - creature.y;
            if(Math.abs(dx)>Math.abs(dy)){                          //if we're farther off in the x direction than in the y direction
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //then move horizontally                            //ternary operators (with trig functions?)
            }else if(Math.abs(dx)<Math.abs(dy)||Math.random()<0.5){ //otherwise, if we're farther off in the y direction or we just feel like it
                creature.moveBy(0, dy > 0 ? 1 : -1);                //then move vertically                             //woot woot
            }else{
                creature.moveBy(dx > 0 ? 1 : -1,0);                 //otherwise, go back to the first plan and move horizontally         //java
            }
        }
    }

    @Override
    public void onDeath(){
        PlayerAI pai = (PlayerAI) owner.getAI();
        pai.minions--;
    }
}