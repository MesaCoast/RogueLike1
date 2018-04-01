import java.io.Serializable;

/**
 * Welcome to the FieldOfView class!
 * This class tracks what the user has seen before and is used to determine what
 * is displayed on the screen.
 * 
 * 
 */
public class FieldOfView implements Serializable{
    private World world;
    private boolean[][] visible;
    private Tile[][] map;
    private static final long serialVersionUID = 4000000L;

    public FieldOfView(World world){
        this.world = world;
        visible = new boolean[world.getWidth()+160][world.getHeight()+50];
        map = new Tile[world.getWidth()+160][world.getHeight()+50];
    
        for (int x = 0; x < world.getWidth()+160; x++){
            for (int y = 0; y < world.getHeight()+50; y++){
                map[x][y] = Tile.UNKNOWN;
            }
        }
    }

    public boolean isVisible(int x, int y){
        if(x >= -80 && y >= -25 && x < visible.length && y < visible[0].length)
            return visible[x+80][y+25];
        else
            return true;
    }

    public Tile getTile(int x, int y){
        /*if(x < 0||x >= world.getWidth()||y < 0||y >= world.getHeight()){
            return Tile.UNKNOWN;
        }else{*/
            return map[x+80][y+25];
        //}
    }

    public void update(int wx, int wy, int r){//player X, player Y, and vision radius
        visible = new boolean[world.getWidth()+160][world.getHeight()+50];
    
        for(int x = -r; x < r; x++){
            for(int y = -r; y < r; y++){
                if(x*x + y*y > r*r)        //if the point is outside of our vision radius
                    continue;
         
                /*if(wx + x < 0||wx + x >= world.getWidth()||wy + y < 0||wy + y >= world.getHeight())
                    continue;*/
         
                for(Point p : (new Line(wx, wy, wx + x, wy + y)).getPoints()){
                    Tile t = world.getTile(p.x, p.y);
                    visible[p.x+80][p.y+25] = true;
                    map[p.x+80][p.y+25] = t;
             
                    if(!t.isTransparent())
                        break;
                }
            }
        }
    }
}