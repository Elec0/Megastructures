package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2i;
import elec0.megastructures.general.Vector2l;

import java.util.Random;

public class Location
{
	public static int LAST_ID = 0; // Iterates every time a new object is created
	public static final int SECTOR_SIZE = 10000; // x and y size of a sector
	public static final int SUBSECTOR_SIZE = 1000; // The 'square' size of a solar system. No solar system can be closer than this to another
	public static final int SUBSECTORS = SECTOR_SIZE / SUBSECTOR_SIZE; // Number of subsectors in a sector


	private long seed; // The random seed used for generating things, usually through the generate() method
	private Random rand; // Generating cross-method requires a constant random object, or things are just going to be regenerated
	private Vector2l position; // Position of the object in space
	private Vector2i sector; // x,y sector location of the object, which correlates to the position
								// positionToSector() returns the position of the upper-left part of the sector
	private String name; // User-facing name of the object
	private int ID; // A unique ID 1-MAXINT for everything inheriting from Location, which should be pretty much everything

	public Location() { ID = ++LAST_ID; name = "";}
	public Location(long seed) { this.seed = seed; ID = ++LAST_ID; name = ""; rand = new Random(seed); }

	public long getSeed()
	{
		return seed;
	}
	public void setSeed(long seed)
	{
		this.seed = seed;
		rand = new Random(seed);
	}

	public Random getRand() { return rand; }
	//public void setRand(Random rand) { this.rand = rand; }

	/**
	 * Converts a position Vector2l into a sector
	 * Sector 0,0 corresponds to position 0,0
	 * Sectors are squares, sector 1,1 is (SECTOR_SIZE + 1, SECTOR_SIZE + 1)
	 * @param position
	 * @return
	 */
	public static Vector2i positionToSector(Vector2l position)
	{
		if(position == null)
			return null;
		return new Vector2i((int)(position.getX() / SECTOR_SIZE), (int)(position.getY() / SECTOR_SIZE));
	}

	/**
	 * Takes a positon and returns the subsector
	 * (0 - SUBSECTORS)
	 * Subsector 0,0 is top left of sector
	 * @param position
	 * @return
	 */
	public static Vector2i positionToSubsector(Vector2l position)
	{
		if(position == null)
			return null;
		// Get position in sector
		// ||pX| - |sector.X * SIZE|| / SUB_SIZE
		Vector2i pos = new Vector2i((int)Math.abs(Math.abs(positionToSector(position).getX() * SECTOR_SIZE) - Math.abs(position.getX())) / SUBSECTOR_SIZE, (int)Math.abs(Math.abs(positionToSector(position).getY() * SECTOR_SIZE) - Math.abs(position.getY())) / SUBSECTOR_SIZE);

		return pos;
	}

	/**
	 * Converts a sector Vector2i to a position
	 * @param sector
	 * @return
	 */
	public static Vector2l sectorToPositon(Vector2i sector)
	{
		if(sector == null)
			return null;
		return new Vector2l(sector.getX() * SECTOR_SIZE, sector.getY() * SECTOR_SIZE);
	}

	public Vector2l getPosition()
	{
		return position;
	}
	public void setPosition(Vector2l position)
	{
		this.position = position;
	}


	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	public int getID()
	{
		return ID;
	}
	public void setID(int ID)
	{
		this.ID = ID;
	}

	public Vector2i getSector()
	{
		// If the sector hasn't been initialized, but the position has, calculate the sector
		if(sector == null && getPosition() != null)
			sector = positionToSector(getPosition());
		return sector;
	}
	public void setSector(Vector2i sector)
	{
		this.sector = sector;
	}
}
