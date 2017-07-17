package elec0.megastructures.capabilities;

import elec0.megastructures.Megastructures;
import elec0.megastructures.universe.Galaxy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class MSWorldSavedData extends WorldSavedData
{
	private static final String DATA_NAME = Megastructures.MODID + "_WorldSavedData";

	private Galaxy galaxy;

	public MSWorldSavedData()
	{
		super(DATA_NAME);
		galaxy = new Galaxy();
	}
	public MSWorldSavedData(String s)
	{
		super(s);
	}

	// Save method
	public void save(World world)
	{
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		storage.setData(DATA_NAME, this);
		markDirty();
	}

	// The create or load method
	public static MSWorldSavedData getData(World world)
	{
		// The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
		//MapStorage storage = IS_GLOBAL ? world.getMapStorage() : world.getPerWorldStorage();
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		MSWorldSavedData instance = (MSWorldSavedData) storage.getOrLoadData(MSWorldSavedData.class, DATA_NAME);

		if (instance == null)
		{
			instance = new MSWorldSavedData();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	public Galaxy getGalaxy()
	{
		return galaxy;
	}

	public void setGalaxy(Galaxy galaxy)
	{
		this.galaxy = galaxy;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{

	}

}
