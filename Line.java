import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Welcome to the Line class!
 * This class uses an implementation of Bresenham's line algorithm to generate a list of
 * the points between two points (although technically this class includes more points in
 * the list than Bresenham's is supposed to)
 * 
 * It'll be useful somewhere down the line.
 * 
 */
public class Line{
    private List<Point> points;

    public Line(int x0, int y0, int x1, int y1){//hehe we're using actual proper math notation for this one
        points = new ArrayList<Point>();
    
        //find the differences in x coordinates and in y coordinates
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);

        //hehe ternary operators
        //ok what we're doing here is saying "if the goal x coordinate is to the right, then we're moving right"
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;


        //check if the points share coordinates
        if(dx == 0){
            while(y0 != y1){
                points.add(new Point(x0, y0));
                y0 += sy;
            }
        }else if(dy == 0){
            while(x0 != x1){
                points.add(new Point(x0, y0));
                x0 += sx;
            }
        }else{
            //this is telling us approximately how far off we'll be if we go diagonally (we use this)
            int error = dx - dy;
        
            while(x0 != x1&&y0 != y1){//keep going until we reach the desired point
                //add the next point to the list
                points.add(new Point(x0, y0));
            
                int e2 = error * 2;     //represents the error, multiplied by two. If you think about it, we're looking at the center of pixels, and we want to figure out when we're an entire pixel off
                if (e2 > -dx){          //if we're a pixel off of where we want to be in the x direction
                    error -= dy;        //then lower the error because we're correcting it now
                    x0 += sx;           //and correct it so we're the proper distance off
                    points.add(new Point(x0, y0));
                }
                if (e2 < dx){           //same thing as above, but with the coordinates switched
                    error += dx;
                    y0 += sy;
                    points.add(new Point(x0, y0));
                }
            }
        }
    }

    public List<Point> getPoints(){return points;}

    public Iterator<Point> iterator(){return points.iterator();}
}