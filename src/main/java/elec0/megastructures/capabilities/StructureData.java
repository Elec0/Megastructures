package elec0.megastructures.capabilities;

import com.sun.istack.internal.NotNull;
import elec0.megastructures.Megastructures;
import elec0.megastructures.general.Constants;
import elec0.megastructures.structures.DysonSphereStructure;
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
 * This does mean that finding a player based on a structure takes O(n) time,
 * but we're going to get around that by storing the player's UUID in the structure
 */
public class StructureData extends WorldSavedData
{
	private static final String DATA_NAME = Megastructures.MODID + "_StructureWSD";

	private HashMap<UUID, List<Structure>> structureHash;

	// This is for calling update on the structures, the list will be reflective of all the items in all player lists
	private List<Structure> tickList;

	public StructureData(String s) {
		super(s);
		structureHash = new HashMap<>();
		tickList = new ArrayList<>();
	}
	public StructureData() {
		super(DATA_NAME);
		structureHash = new HashMap<>();
		tickList = new ArrayList<>();
	}

	/**
	 * Runs the update method on all of the structures
	 */
	public void update() {
//		System.out.println("update: " + tickList.size());
		for(Structure s : tickList)
			s.update();
	}

	/**
	 *
	 * @param world
	 */
	public void save(World world)
	{
		MapStorage storage = world.getPerWorldStorage(); // Per dimension world data
		storage.setData(DATA_NAME, this);
		markDirty();
	}

	/**
	 * The create or load method
	 * @param world
	 * @return
	 */
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
	 * Add a Structure to the user's list of structures
	 * @param uuid
	 * @param structure
	 */
	public void addStructure(UUID uuid, Structure structure) {
		if(structureHash != null) {
			// If the hashmap doesn't have the user's structure list, create it
			if(!structureHash.containsKey(uuid)) {
				structureHash.put(uuid, new ArrayList<>());
			}

			// Add the structure to the player's list of structures
			structureHash.get(uuid).add(structure);
			// Any changes made to the structures in structureHash will also be reflected here, because of
			// pass by reference
			tickList.add(structure);
		}
		else
			throw new NullPointerException("structureHash is null, initialize it first.");
	}

	/**
	 * Delete all of a user's structures
	 * @param uuid
	 */
	public void clearAllUserStructures(UUID uuid) {
		if(structureHash == null)
			return;
		if(!structureHash.containsKey(uuid))
			return;

		structureHash.get(uuid).clear();

	}

	/**
	 * Get a user's list of structures
	 * @param uuid
	 * @return
	 */
	public List<Structure> getUserStructures(UUID uuid) {
		if(structureHash != null) {
			if(!structureHash.containsKey(uuid)) {
				return null;
			}
			return structureHash.get(uuid);
		}
		else
			throw new NullPointerException("structureHash is null, initialize it first.");
	}

	/**
	 * Save all the data to NBT
	 * We're gonna do this in the following way:
	 * Make a string list of all the players UUIDs, concat them separated with ','
	 * Loop through all the UUID, List pairs and serialize the Structure List into a NBTTag
	 * Save that tag with the UUID as the key
	 * Save the full string UUID list for reference later
	 *
	 * What we're doing is allowing the storage of an arbitrary number of players information, essentially creating
	 * an array of Strings where each entry is a List, but saving it in NBT.
	 * @param compound
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		StringBuilder uuidList = new StringBuilder();

		for (Map.Entry<UUID, List<Structure>> entry : structureHash.entrySet())
  		{
      		UUID uuid = entry.getKey();
      		List<Structure> structureList = entry.getValue();

      		uuidList.append(uuid.toString()).append(",");

      		// Serialize the structure into NBT
      		NBTTagCompound userStructures = new NBTTagCompound();
      		// Save how many structures the user has
      		userStructures.setInteger(Constants.NBT_STRUCTURES_LEN, structureList.size());

      		for(int i = 0; i < structureList.size(); ++i) {
      			// Convert the structures into tags, append their uuid + i to be able to get them back
      			userStructures.setTag(uuid.toString() + i, structureList.get(i).serializeNBT());
			}

      		// Actually set the data
      		compound.setTag(uuid.toString(), userStructures);
	 	}
		// Trim the trailing comma
		String uuids = uuidList.toString().substring(0, uuidList.length() - 1);
		compound.setString(Constants.NBT_STRUCTURES_UUID_LIST, uuids);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		String uuids = compound.getString(Constants.NBT_STRUCTURES_UUID_LIST);

		// Make sure we have data to read from the NBT
		if(uuids.equals("")) {
			System.out.println(String.format("StructureData: readFromNBT failed due to uninitialized component '%s'",
					Constants.NBT_STRUCTURES_UUID_LIST));
			return;
		}

		HashMap<UUID, List<Structure>> newStructureHash = new HashMap<>();

		// Each entry is a String UUID
		String[] uuidList = uuids.split(",");

		for(String uuid : uuidList) {
			// This has all of the users structure information in it, we need to parse it out
			NBTTagCompound userStructures = (NBTTagCompound)compound.getTag(uuid);

			List<Structure> curUserStructList = new ArrayList<>();

			int numStruct = userStructures.getInteger(Constants.NBT_STRUCTURES_LEN);
			// Go through and deseralize the structures
			for(int i = 0; i < numStruct; ++i) {
				NBTTagCompound curStructure = (NBTTagCompound) userStructures.getTag(uuid + i);
				Structure curStruct;

				// Build the proper objects so the polymorphism actually makes sense and data doesn't just get lost
				switch(curStructure.getInteger(Structure.NBT_TYPE)) {
					case 0:
						curStruct = new DysonSphereStructure(curStructure);
						break;

					default:
						curStruct = new Structure(curStructure);
				}

				curUserStructList.add(curStruct);
				tickList.add(curStruct);
			}

			// Save the list into the new hashmap
			newStructureHash.put(UUID.fromString(uuid), curUserStructList);
		}

		// The new hashmap has been fully loaded
		this.structureHash = newStructureHash;
	}
}
