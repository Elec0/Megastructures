package elec0.megastructures.capabilities;

import elec0.megastructures.Megastructures;
import elec0.megastructures.structures.Structure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;

public class StructureData extends WorldSavedData
{
	private static final String DATA_NAME = Megastructures.MODID + "_StructureWSD";

	private List<Structure> structureList;

	public StructureData() { super(DATA_NAME); }
	public StructureData(String s) { super(s); }

	public void save(World world)
	{

	}


	// The create or load method
	public static StructureData getData(World world)
	{
		// The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
		//MapStorage storage = IS_GLOBAL ? world.getMapStorage() : world.getPerWorldStorage();
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		StructureData instance = (StructureData) storage.getOrLoadData(StructureData.class, DATA_NAME);

		if (instance == null)
		{
			instance = new StructureData();

			storage.setData(DATA_NAME, instance); // TODO: I think I'm not saving the WSD correctly
		}
		return instance;
	}

	public void setStructureList(List<Structure> list)
	{
		this.structureList = list;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		// I will probably eventually make it so that the data actually saves instead of re-generating for every load
		// But for now, this works as a test to keep things fairly simple
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{

	}
}
