package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

public class Location
{
	public static int LAST_ID = 0; // Iterates every time a new object is created

	private long seed;
	private Vector2l position; // Not really used at this point
	private String name;
	private int ID; // A unique ID 1-MAXINT for everything inheriting from Location, which should be pretty much everything

	public Location() { ID = ++LAST_ID; name = ""+ID;}
	public Location(long seed) { this.seed = seed; ID = ++LAST_ID; name = ""+ID;}

	public long getSeed()
	{
		return seed;
	}
	public void setSeed(long seed)
	{
		this.seed = seed;
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
}
