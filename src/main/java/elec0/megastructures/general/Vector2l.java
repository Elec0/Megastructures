package elec0.megastructures.general;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector2l))
			return false;
		if (obj == this)
			return true;

		Vector2l vec = (Vector2l) obj;
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
