import net.slashie.libjcsi.CSIColor;
import java.io.Serializable;

/**
 * Welcome to the MyCSIColor class!
 * I made this class because the class I was using to represent colors was from a library and wasn't serializable
 * ...so, I made this serializable version for when I need to store a creature's color
 * 
 * 
 * 
 */
public class MyCSIColor implements Serializable{
	int r, g, b;
	private static final long serialVersionUID = 8000L;
	public MyCSIColor(CSIColor c){
		r = c.getR();
		g = c.getG();
		b = c.getB();
	}

	public CSIColor getCSI(){
		return new CSIColor(r, g, b);
	}

	public static CSIColor getCSI(int r, int g, int b){
		return new CSIColor(r, g, b);
	}

	public static MyCSIColor getMyCSI(CSIColor c){
		return new MyCSIColor(c);
	}
}