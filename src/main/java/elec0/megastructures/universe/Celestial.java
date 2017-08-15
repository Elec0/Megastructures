package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

public class Celestial extends Location
{
	// Celestial type IDs
	public static final int PLANET = -1;
	public static final int STAR = -2;

	private double mass, radius; // Mass in SI, radius in subsystem squares?
	private Vector2l periapsisPos, apoapsisPos; // Not used yet, but eventually we will plot a path through these two points

	public Celestial() { super(); };
	public Celestial(long seed) { super(seed); }


	public void generate()
	{

	}

	public double getMass()
	{
		return mass;
	}
	public void setMass(double mass)
	{
		this.mass = mass;
	}

	public double getRadius()
	{
		return radius;
	}
	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	public Vector2l getPeriapsisPos()
	{
		return periapsisPos;
	}
	public void setPeriapsisPos(Vector2l periapsisPos)
	{
		this.periapsisPos = periapsisPos;
	}

	public Vector2l getApoapsisPos()
	{
		return apoapsisPos;
	}
	public void setApoapsisPos(Vector2l apoapsisPos)
	{
		this.apoapsisPos = apoapsisPos;
	}
}
