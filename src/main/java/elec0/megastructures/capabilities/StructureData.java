package elec0.megastructures.capabilities;

import elec0.megastructures.Megastructures;
import elec0.megastructures.structures.Structure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

/**
 * Holds all of the player-made structures as a data type
 * We're gonna save everything as a HashMap(UUID, List(Structure))
 * This is so we can get a user's structures in O(1) time.
 *
 * Looping through the structures should be done via
 * for (Map.Entry<String, Structure> entry : map.entrySet())
 * {
 *     String key = entry.getKey();
 *     List value = entry.getValue();
 *     //use key and value
 * }
 */
public class StructureData extends WorldSavedData
{
	private static final String DATA_NAME = Megastructures.MODID + "_StructureWSD";

	private HashMap<UUID, List<Structure>> structureHash;

	public StructureData() {
		super(DATA_NAME);
		structureHash = new HashMap<>();
	}

	public void save(World world)
	{
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		storage.setData(DATA_NAME, this);
		markDirty();
	}


	// The create or load method
	public static StructureData getData(World world)
	{
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		StructureData instance = (StructureData) storage.getOrLoadData(StructureData.class, DATA_NAME);

		if (instance == null) {
			instance = new StructureData();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	/**
	 *
	 * @param uuid
	 * @param structure
	 */
	public void addStructure(UUID uuid, Structure structure) {
		if(structureHash != null) {
			// Add the structure to the player's list of structures
			structureHash.get(uuid).add(structure);
		}
	}

	/**
	 * Save all the data to NBT
	 * @param compound
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		String uuidList = "";

		for (Map.Entry<UUID, List<Structure>> entry : structureHash.entrySet())
  		{
      		UUID uuid = entry.getKey();
      		List<Structure> structureList = entry.getValue();

      		uuidList += uuid.toString() + ",";
      		NBTTagCompound userStructures = new NBTTagCompound();

      		for(int i = 0; i < structureList.size(); ++i) {
      			userStructures.setTag(uuid.toString(), structureList.get(i).getNBTTag());
			}

	 	}
		// Trim the trailing comma
		uuidList = uuidList.substring(0, uuidList.length() - 1);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{

	}
}
