import net.slashie.libjcsi.CSIColor;

public enum PlayerClass{
	WIZARD(CSIColor.MEDIUM_PURPLE), 
	PALADIN(CSIColor.PLATINUM), 
	GUNSLINGER(CSIColor.TERRA_COTTA), 
	GRENADIER(CSIColor.OLD_GOLD), 
	SUMMONER(CSIColor.SHAMROCK_GREEN);	

	private final MyCSIColor color;

	PlayerClass(CSIColor color){
		this.color = new MyCSIColor(color);
	}

	public CSIColor getColor(){return color.getCSI();}
}
/**
 * Class stat information:
 *
 *      WIZARD
 *      	attackStrength = 7;
 *			attack distance = visRadius = 9;
 *			attack pierces all enemies
 *      PALADIN
 *      	attackStrength = 10;
 *			attack distance = 2;
 *			attack pierces all enemies
 *			maxHP = 30;
 *      GUNSLINGER
 *      	attackStrength = 7;
 *			attack distance = visRadius = 11;
 *			attack pierces no enemies
 *      GRENADIER
 *         	attackStrength = 5;
 *			attack distance = visRadius = 9;
 *			attacks everything in a 3x3 area centered on the projectile
 *			attacks can destroy tiles
 *      SUMMONER
 *      	attackStrength = 5;
 *
 */