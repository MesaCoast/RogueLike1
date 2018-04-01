import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Welcome to the Point class!
 * This class represents points in 3 dimensions.
 * It's part of an experiment to improve the performance of some of the parts of this program.
 * hippity hoppity get off my property
 * 
 * 
 */
public class Point{
    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode(){//I usually hate overriding Object methods, but the ones here might actually be necessary
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
        	return true;
        if(obj == null)
        	return false;
        if(!(obj instanceof Point))
        	return false;
        Point other = (Point) obj;
        if(x != other.x)
        	return false;
        if(y != other.y)
        	return false;
        return true;
    }

    public List<Point> neighbors(){
	    List<Point> points = new ArrayList<Point>();
	  
	    for(int ox = -1;ox < 2;ox++){
	        for(int oy = -1;oy < 2;oy++){
	            if(ox == 0&&oy == 0)
	                continue;
	            points.add(new Point(x+ox, y+oy));
	        }
	    }
	    Collections.shuffle(points);//avoids biasing the order of things when we check neighboring stuff
	    return points;
	}
}