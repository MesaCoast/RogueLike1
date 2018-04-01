import java.util.Random;

/**
 * Welcome to the WorldBuilder class!
 * @version 12/10/2017
 * This class is based off of the CaveGen class, but it's rewritten to
 * 	1) work better and
 *  2) use Tiles
 */
public class WorldBuilder{
	private int width, height;
	private final int VERTICAL = 0;
	private final int HORIZONTAL = 1;
	private Random rand;
	private Tile[][] map;

	public WorldBuilder(int width, int height){
		this.width = width;
		this.height = height;
		this.map = new Tile[width][height];
		rand = new Random();
	}

	public World build(){return new World(map);}

	public WorldBuilder makeMaze(int roomSize){
		//Clear the maze
		for(int r = 0;r < height;r++){
			for(int c = 0;c < width;c++){
				map[c][r] = Tile.FLOOR;
			}
		}

		//Build the outer walls of the maze
		for(int r = 0;r < height;r++){
			map[0][r] = Tile.WALL;
			map[width-1][r] = Tile.WALL;
		}
		for(int c = 0;c < width;c++){
			map[c][0] = Tile.WALL;
			map[c][height-1] = Tile.WALL;
		}

		//Call the mazesplitter
		split(2, 2, width-4, height-4, roomSize);

		return this;
	}

	/**
	 * Split the map in half, split each half in half, etc. recursively
	 * The if statement at the start of the method is what causes it to bottom out
	 * x and y are the minimum valid coordinates to place the wall
	 * width and height are the differences between the max and min valid indexes for wall placements (x and y indexes respectively) + 1
	 */
	private void split(int x, int y, int width, int height, int roomSize){
		//stop if the target size has been reached, leaving a room if cutoff is large (comparatively to 1)
		int cutoff = 1 + rand.nextInt(roomSize);
		if(width < cutoff || height < cutoff)
			return;

		//Split the wall
		switch(chooseOrient(width, height)){

			case HORIZONTAL:
				//Choose an even y coordinate for the wall
				int wallY = y + (2*rand.nextInt((height-1)/2 + 1));

				//Choose an odd x coordinate for the hole in the wall
				int holeX = x + (2*rand.nextInt((width-1)/2 + 2)) - 1;

				//Add the wall with some random holes
				for(int c = x-1;c < (x + width + 1);c++){
					if(rand.nextInt(6) > 0&&c != holeX)
						map[c][wallY] = Tile.WALL;
				}

				//Split the section above the wall
				split(x, y, width, wallY - y - 1, roomSize);
				//Split the section below the wall
				split(x, wallY+2, width, height - (wallY-y) - 2, roomSize);
				break;

			case VERTICAL:
				//Choose an even x coordinate for the wall
				int wallX = x + (2*rand.nextInt((width-1)/2 + 1));

				//Choose an odd y coordinate for the hole in the wall
				int holeY = y + (2*rand.nextInt((height-1)/2 + 2)) - 1;

				//Add the wall with some random holes
				for(int r = y-1;r < (y + height + 1);r++){
					if(rand.nextInt(6) > 0&&r != holeY)
						map[wallX][r] = Tile.WALL;
				}

				//Split the section to the left of the wall
				split(x, y, wallX - x - 1, height, roomSize);
				//Split the section to the right of the wall
				split(wallX+2, y, width - (wallX-x) - 2, height, roomSize);
				break;

			default:
				System.out.println("Invalid orientation!");
				System.exit(-1);
		}
	}

	//Return one of the two orientation integers depending on the width and the height
	private int chooseOrient(int width, int height){
		if(width > height)
			return VERTICAL;
		if(height > width)
			return HORIZONTAL;
		return rand.nextInt(2);
	}

	//Multiplies the size of the map by factor
	public WorldBuilder enlargeMap(int factor){
		Tile[][] newMap = new Tile[width * factor][height * factor];

		for(int r = 0;r < height;r++){
			for(int c = 0;c < width;c++){
				for(int dr = 0;dr < factor;dr++){
					for(int dc = 0;dc < factor;dc++){
						newMap[(factor*c)+dc][(factor*r)+dr] = map[c][r];
					}
				}
			}
		}
		map = newMap;
		height *= factor;
		width *= factor;

		return this;
	}

	//Warning: this will generate a world 3x larger than you requested.
	public WorldBuilder makeCaves(){
		return makeMaze(1).enlargeMap(3).iterateCellularAutomata(0, 4).iterateCellularAutomata(1, 3).addExit();
	}

	public WorldBuilder addExit(){
		int x, y;
		do{
	        x = (int)(Math.random() * width);
	        y = (int)(Math.random() * height);
	    }while(map[x][y] != Tile.FLOOR);
	    map[x][y] = Tile.EXIT;

		return this;
	}

	//Smoothes the map
	public WorldBuilder iterateCellularAutomata(int phase, int times){
		for(int i = 0;i < times;i++){
			Tile[][] newMap = map;

			for(int r = 1;r < height-1;r++){
				for(int c = 1;c < width-1;c++){
					if(willBeWall(c, r, phase))
						newMap[c][r] = Tile.WALL;
					else
						newMap[c][r] = Tile.FLOOR;
				}
			}
			map = newMap;
		}
		return this;
	}

	//Helper function for the above, apply the proper rule to the given coordinates given the phase and return the result
	private boolean willBeWall(int x, int y, int phase){
		int n = getNeighbors(x, y);
		int fn = getFarNeighbors(x, y);
		if(phase == 0){
			return n >= 5||fn <= 2;
		}else{
			return n >= 5;
		}
	}

	//Helper function for the above
	private int getFarNeighbors(int x, int y){
		int neighbors = 0;
		for(int r = y - 2;r < y + 3;r++){
			for(int c = x - 2;c < x + 3;c++){
				//if it's off the map
				if(r < 0||c < 0||r >= height||c >= width)
					continue;
				//if it's more than 2 taxicab moves away from the center
				if(Math.abs(r - y) == 2 && Math.abs(c - x) == 2)
					continue;
				if(map[c][r] == Tile.WALL)
					neighbors++;
			}
		}
		return neighbors;
	}

	//Another helper function
	private int getNeighbors(int x, int y){
		int neighbors = 0;
		for(int r = y - 1;r < (y + 2);r++){
			for(int c = x - 1;c < (x + 2);c++){
				if(r < 0||c < 0||r >= height||c >= width)
					continue;
				if(map[c][r] == Tile.WALL)
					neighbors++;
			}
		}
		return neighbors;
	}
}