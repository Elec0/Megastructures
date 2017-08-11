package elec0.megastructures.general;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector2i))
			return false;
		if (obj == this)
			return true;

		Vector2i vec = (Vector2i) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(getX(), vec.getX());
		builder.append(getY(), vec.getY());
		return builder.isEquals();
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(getX());
		builder.append(getY());
		return builder.toHashCode();
	}

	@Override
	public String toString()
	{
		return "[" + x + ", " + y + "]";
	}
}
