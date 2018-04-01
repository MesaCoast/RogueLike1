import net.slashie.libjcsi.CSIColor;
import java.util.*;
import java.io.Serializable;


/**
 * Welcome to the World class!
 * made on
 * @version 12/10/2017
 *
 * This class is the representation of our game's world.
 * It has several cleverly written methods used to make other parts of the game simpler.
 * It has a map, but it also stores a list of creatures and several other important items.
 * We leave the map creation separate to allow for the building of multiple world types.
 */
public class World implements Serializable{
	private Tile[][] map;
	private int width, height;
	private List<Creature> creatures;
	private static final long serialVersionUID = 1000000L;

	public World(Tile[][] map){
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		creatures = new ArrayList<Creature>();
	}

	public void act(){
		for(int i = 0;i < creatures.size();i++){
			creatures.get(i).act();
		}
	}

	//This method attempts to destroy the tile at the given location, returning true if the tile is destroyed or false otherwise
	public boolean destroyTile(int x, int y){
		if(getTile(x, y).isDestroyable()){
			map[x][y] = Tile.FLOOR;
			return true;
		}
		return false;
	}

	public Tile getTile(int x, int y){
		if(x < 0||x >= width||y < 0||y >= height){
			return Tile.BOUNDARY;
		}else{
			return map[x][y];
		}
	}

	public char getSymbol(int x, int y){
		Creature c = getCreature(x, y);
		return c != null ? c.getSymbol() : getTile(x, y).getSymbol();
	}

	public CSIColor getColor(int x, int y){
		Creature c = getCreature(x, y);
		return c != null ? c.getColor() : getTile(x, y).getColor();
	}

	public Creature getCreature(int x, int y){
		for(Creature c: creatures)
			if(c.x == x&&c.y == y)
				return c;
		return null;
	}

	//returns the list of creatures
	public List<Creature> getCreatures(){return creatures;}

	/*public ListIterator getCreatureIterator(){
		return creatures.listIterator();
	}*/

	public int getWidth(){return width;}

	public int getHeight(){return height;}

	public void addAtValidLocation(Creature creature){
	    int x;
	    int y;

	    do{
	        x = (int)(Math.random() * width);
	        y = (int)(Math.random() * height);
	    }while(getTile(x,y).isTangible()||getCreature(x, y) != null);

	    addAtLocation(x, y, creature);
	}

	//adds the specified creature to the world at the specified location
	public void addAtLocation(int x, int y, Creature c){
		c.x = x;
		c.y = y;
		creatures.add(c);
	}

	public void setMap(Tile[][] map){
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
	}

	public Tile[][] getMap(){return map;}

	public void removeCreature(Creature c){creatures.remove(c);}

	public void resetCreatures(){creatures = new LinkedList<Creature>();}
}