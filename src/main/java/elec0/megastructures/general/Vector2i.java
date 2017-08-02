package elec0.megastructures.general;


public class Vector2i
{
	private int x = 0, y = 0;

	public Vector2i()
	{
	}
	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public long getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public long getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}

	@Override
	public String toString()
	{
		return "[" + x + ", " + y + "]";
	}
}
