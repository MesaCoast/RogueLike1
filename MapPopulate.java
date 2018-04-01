import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;


/**
 * Welcome to the MapPopulate class
 * created by
 * @author Sam Coates, sam@ryancoates.com, coatess@purdue.edu
 * on
 * @version 12/8/2017 - 12/9/2017
 * 
 * This class takes a cave built using the CaveGen class, removes extra empty spaces, and populates the map.
 * It also contains a second class down below used for tracking ordered pairs of coordinates.
 * 
 */
public class MapPopulate{
	private String[][] map;
	private int width, height;
	private String wall;
	private CoordSet internalSpace;
	private Random r;

	public MapPopulate(){
		map = null;
		width = 0;
		height = 0;
		wall = null;
		internalSpace = null;
		r = new Random();
	}

	public void populate(String[][] map, String wall){
		this.map = map;
		this.wall = wall;
		width = map.length;
		height = map[0].length;

		//First, remove extra empty spaces in the map
		removeExcessEmptySpaces();

		//Now, decide on a spawn location
		addFeature(r.nextInt(internalSpace.size()), "S");

		//Add some enemy spawns
		int spawnsToAdd = internalSpace.size() / 60;
		for(int added = 0;added < spawnsToAdd;added++){
			addFeature(r.nextInt(internalSpace.size()), "X");
		}

		//Add an exit too, we'll probably want a way to escape
		addFeature(r.nextInt(internalSpace.size()), "E");

	}

	//Sets the item in internalSpace with the given index to feature and removes that index from internalSpace
	private void addFeature(int index, String feature){
		//Find the coordinates of the feature
		CoordSet featureCoords = internalSpace.getCoords(index);

		//Add the feature to the map
		map[featureCoords.getX(0)][featureCoords.getY(0)] = feature;

		//Remove the feature's location from internalSpace
		internalSpace.remove(featureCoords);
	}

	//Fills in all but the largest empty space in the map with wall and saves the remaining internal space as internalSpace
	private void removeExcessEmptySpaces(){
		//Make an ArrayList full of sets of coordinates, each set initially containing 1 coordinate which represents an open space
		ArrayList<CoordSet> openSpaces = new ArrayList<CoordSet>();

		for(int r = 1;r < height-1;r++){
			for(int c = 1;c < width-1;c++){
				if(!map[c][r].equals(wall)){
					openSpaces.add(new CoordSet(c, r));
				}
			}
		}

		/**
		 * For each CoordSet:
		 *  - Check if it is neigboring another CoordSet
		 * 	   - If so, add its contents to that CoordSet and remove the original CoordSet
		 * 	   - If not, continue
		 */
		for(int i = 0;i < openSpaces.size();i++){
			for(int i2 = 1;i2 < openSpaces.size();i2++){
				if(i >= openSpaces.size()){
					i = 0;
				}
				if(i == i2)
					continue;
				if(openSpaces.get(i).isAdjacent(openSpaces.get(i2))){	//if the first space we're checking is touching the second space we're checking at an edge
					openSpaces.get(i).addCoordSet(openSpaces.get(i2));	//then it's part of the same space
					openSpaces.remove(i2);
					i = 0;												//start over with the first two spaces
					i2 = 1;
				}
			}
		}

		/**
		 * openSpaces now contains a list of CoordSets with each representing a separate open space
		 * Find the largest open space
		 */
		int indexOfLargest = 0;
		for(int i = 1;i < openSpaces.size();i++){
			if(openSpaces.get(i).size() > openSpaces.get(indexOfLargest).size())
				indexOfLargest = i;
		}

		//Save the coordinates of the largest open space for later use
		internalSpace = openSpaces.get(indexOfLargest);

		//Remove the largest CoordSet from the list and fill in all the rest
		openSpaces.remove(indexOfLargest);

		for(int i = 0;i < openSpaces.size();i++){
			fill(openSpaces.get(i));
		}
	}

	//Replaces all (x, y) in map which can be found in cs with wall
	private void fill(CoordSet cs){
		ListIterator xli = cs.x.listIterator();
		ListIterator yli = cs.y.listIterator();

		while(xli.hasNext()){
			map[(int)xli.next()][(int)yli.next()] = wall;
		}
	}
}


/**
 * A class which represents sets of x and y coordinates, here implemented with two LinkedLists
 * It took me a lot of deliberation to decide upon this implementation, but for now I'm using LinkedLists because of the amount of adding and removal involved
 * with this program. It may be better to create a single LinkedList containing a class which represents a singular ordered pair of coordinates. Also consider
 * switching between this and an ArrayList if necessary. I'll run some tests later on once I finish some other implementations
 * Also consider ArrayDeque or constructing an ArrayList with high initial capacity
 */
class CoordSet{
	LinkedList<Integer> x;
	LinkedList<Integer> y;

	public CoordSet(int x, int y){
		this.x = new LinkedList<Integer>();
		this.y = new LinkedList<Integer>();
		this.x.add(x);
		this.y.add(y);
	}

	//Returns the number of ordered pairs stored by this CoordSet
	public int size(){return x.size();}

	//Returns a CoordSet representing the coordinates at the given index (Try to avoid using this function too much, it requires list traversal)
	public CoordSet getCoords(int index){return new CoordSet((int)x.get(index), (int)y.get(index));}

	//Returns the x value with the given index
	public int getX(int index){return (int) x.get(index);}

	//Returns the y value with the given index
	public int getY(int index){return (int) y.get(index);}

	//Adds all coordinates in cs to this CoordSet
	public void addCoordSet(CoordSet cs){
		x.addAll(cs.x);
		y.addAll(cs.y);
	}

	//Removes the first instance of the given coordinates from this CoordSet, returning true if the coordinates are found and false otherwise
	public boolean remove(int x, int y){
		ListIterator xli = this.x.listIterator();
		ListIterator yli = this.y.listIterator();
		while(xli.hasNext()){
			if(((Integer)(xli.next())).intValue() == x && ((Integer)(yli.next())).intValue() == y){
				xli.remove();
				yli.remove();
				return true;
			}
		}
		return false;
	}

	//As above, but provide a size 1 CoordSet as the argument
	public boolean remove(CoordSet cs){return remove((int) cs.x.get(0), (int) cs.y.get(0));}

	//Checks to see if the given coordinates neighbor any of the coordinates in this CoordSet
	public boolean isAdjacent(int x, int y){
		ListIterator xli = this.x.listIterator();
		ListIterator yli = this.y.listIterator();
		int curX, curY;

		while(xli.hasNext()){
			curX = ((Integer)(xli.next())).intValue();
			curY = ((Integer)(yli.next())).intValue();

			if(Math.abs(curX - x) == 1&&curY == y)
				return true;
			if(Math.abs(curY - y) == 1&&curX == x)
				return true;
		}
		return false;
	}

	//Checks to see if the given CoordSet neighbors this one
	public boolean isAdjacent(CoordSet cs){
		ListIterator xli = cs.x.listIterator();
		ListIterator yli = cs.y.listIterator();

		while(xli.hasNext()){
			if(isAdjacent(((Integer)(xli.next())).intValue(), ((Integer)(yli.next())).intValue()))
				return true;
		}
		return false;
	}

	//for debugging (prints all of the coordinates as ordered pairs to stdout)
	public void print(){
		System.out.print("[");
		ListIterator xli = x.listIterator();
		ListIterator yli = y.listIterator();

		while(xli.hasNext()){
			System.out.print("(" + ((Integer)(xli.next())).intValue() + ", " + ((Integer)(yli.next())).intValue() + ")");
		}
		System.out.println("]\n");
	}
}