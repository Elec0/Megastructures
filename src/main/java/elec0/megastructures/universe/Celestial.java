package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

public class Celestial extends Location
{
	private double mass, radius; // In SI units. Duh.
	private Vector2l pos; // Position of the celestial in a solar system
	private Vector2l periapsisPos, apoapsisPos; // Not used yet, but eventually we will plot a path through these two points

	public Celestial() { super(); };
	public Celestial(long seed) { super(seed); }


	public void generate()
	{

	}

	public Vector2l getPos()
	{
		return pos;
	}
	public void setPos(Vector2l pos)
	{
		this.pos = pos;
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
