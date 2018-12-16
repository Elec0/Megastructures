package elec0.megastructures.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class MegastructuresWSDBase extends WorldSavedData
{
	private World world;

	public MegastructuresWSDBase(String s) {
		super(s);
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		return null;
	}
}
