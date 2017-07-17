package elec0.megastructures.general;


public class Vector2l
{
	private long x = 0, y = 0;

	public Vector2l()
	{
	}
	public Vector2l(long x, long y)
	{
		this.x = x;
		this.y = y;
	}

	public long getX()
	{
		return x;
	}
	public void setX(long x)
	{
		this.x = x;
	}
	public long getY()
	{
		return y;
	}
	public void setY(long y)
	{
		this.y = y;
	}
}
