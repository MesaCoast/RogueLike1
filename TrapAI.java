import java.io.Serializable;

/**
 * Welcome to the TrapAI class!
 * 
 * ...yeah, I know, "Why do you need an AI for this?!?!?!?"
 * well, um, yeah, uhhh... I guess I don't
 *b u t   E y 3   l 1 K 3   t h 3    c 0 n 5 1 5 T 3 n C y 
 *
 * ...☃...
 */
public class TrapAI extends AI implements Serializable{
    private static final long serialVersionUID = 500000000L;

    public TrapAI(Creature creature, World world){
        super(creature, world);
        isFriendly = false;
    }

    @Override
    public void act(){
        
    }
}