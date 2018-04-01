import net.slashie.libjcsi.CSIColor;

public enum Tile{
	FLOOR('.', CSIColor.GRAY, false, true, false),
	WALL('#', CSIColor.LIGHT_GRAY, true, false, true),
	BOUNDARY('#', CSIColor.DARK_GRAY, true, false, false),
	EXIT('E', CSIColor.BLUE, false, true, false),
	UNKNOWN(' ', CSIColor.BLACK, true, false, false);

	private final char symbol;				//the char drawn on the screen to represent the enemy
	private final MyCSIColor color;			//my serializable version of the color class
	private final boolean isTangible;		//true if the user can't walk through the tile
	private final boolean isTransparent;	//false if the tile obscures the user's vision
	private final boolean isDestroyable;

	Tile(char symbol, CSIColor color, boolean isTangible, boolean isTransparent, boolean isDestroyable){
		this.symbol = symbol;
		this.color = new MyCSIColor(color);
		this.isTangible = isTangible;
		this.isTransparent = isTransparent;
		this.isDestroyable = isDestroyable;
	}

	public char getSymbol(){return symbol;}
	public CSIColor getColor(){return color.getCSI();}
	public boolean isTangible(){return isTangible;}
	public boolean isTransparent(){return isTransparent;}
	public boolean isDestroyable(){return isDestroyable;}
}