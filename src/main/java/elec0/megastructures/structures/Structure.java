package elec0.megastructures.structures;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.security.KeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Structure implements INBTSerializable<NBTTagCompound>
{

	private UUID player;					// The player who owns this structure
	private String name;					//
	private int type;						// index for TYPES array
	private int progress;					// 0-100, percent of current stage construction completion
	private double energy;					// Amount of RF that is stored in this structure (1 RF = 2.5J)
	private double maxEnergyGen;			// Max amount of energy possible to generate per tick
	private int stage;						// Some stages don't generate power or have different options
											// Stage 0 is non-functional in every way. No power, computation, anything
	private HashMap<String, Integer> curMaterials;		// Map of oredict,count for the number of mats for the current construction that are in the network
	private HashMap<String, Integer> neededMaterials;	// Map of oredict,count for the total number of mats needed for the current construction
	private boolean constructing;			// Are we building something or not


	public static final String NBT_PLAYER = "player";
	public static final String NBT_NAME = "name";
	public static final String NBT_TYPE = "type";
	public static final String NBT_PROGRESS = "progress";
	public static final String NBT_ENERGY = "energy";
	public static final String NBT_MAX_ENERGY_GEN = "maxEnergyGen";
	public static final String NBT_STAGE = "stage";
	public static final String NBT_CUR_MATS = "curMats";
	public static final String NBT_TOT_MATS = "totMats";
	public static final String NBT_CONSTRUCTING = "constructing";

	public static final String[] TYPES = new String[]{"Dyson Sphere"};


	public Structure(UUID player, String name) {
		this.player = player;
		this.name = name;
	}

	public Structure(NBTTagCompound nbtStructure) {
		deserializeNBT(nbtStructure);
	}


	/**
	 * The main structure logic update loop
	 */
	public void update() {
		generate();
		checkConstruction();
	}

	/**
	 * Generate energy, if this is a structure that does that
	 * Note:
	 */
	public void generate() {
		if(stage > 0) {
			double prog = getProgress() / 100d; // Convert this into a percentage
			double energyToGen = prog * getMaxEnergyGen();
			setEnergy(getEnergy() + energyToGen);
		}
	}

	/**
	 * Check to see if there is construction ongoing, and if it's done yet
	 */
	public void checkConstruction() {
		if(!isConstructing())
			return;

		// Loop through all the cur materials to check if they are full

	}

	/**
	 * Deduct amount of power from structure
	 * @param amt
	 */
	public void consume(double amt) {
		if(stage > 0)
			setEnergy(getEnergy() - amt);
	}

	public void consume(int amt) { consume((double)amt); }


	/**
	 * Put some material into the map to tell what is required to build this thing.
	 * @param oreDict
	 * @param count
	 */
	public void addNeededConsutructionMaterial(String oreDict, int count) {
		if(!neededMaterials.containsKey(oreDict))
			neededMaterials.put(oreDict, count);
		else
			neededMaterials.put(oreDict, count + neededMaterials.get(oreDict));
	}

	/**
	 * Add to the curMaterials, this is actual items that the user inputs.
	 * @param oreDict
	 * @param count
	 */
	public boolean addMaterial(String oreDict, int count) throws KeyException {
		// This shouldn't happen, but just in case it does
		if(!neededMaterials.containsKey(oreDict))
			throw new KeyException("Trying to add material " + oreDict + " when it isn't needed");

		if(!curMaterials.containsKey(oreDict))
			curMaterials.put(oreDict, 0);

		// Stop if we try to put too many in
		if(curMaterials.get(oreDict) + count > neededMaterials.get(oreDict))
		{
			int diff = (curMaterials.get(oreDict) + count) - neededMaterials.get(oreDict);
			if(diff == 0) // We need no more to complete the structure
				return false;
			else // We do actually need some amount of this transfer to complete the structure
			{
				// TODO: Figure out how to give the excess back
				curMaterials.put(oreDict, neededMaterials.get(oreDict));

				return true;
			}
		}

		curMaterials.put(oreDict, curMaterials.get(oreDict) + count);
		return true;
	}

	/* ***************************
	// *** Getters and Setters ***
	   ***************************/
	public UUID getPlayer() { return player; }
	public void setPlayer(UUID player){this.player = player;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getTypeName() { return TYPES[type]; }
	public void setType(int type) { this.type = type; }
	public int getType() { return type; }
	public int getProgress(){return progress;}
	public void setProgress(int progress){this.progress = progress;}
	public double getEnergy() {return energy;}
	public void setEnergy(double energy){this.energy = energy;}
	public double getMaxEnergyGen(){return maxEnergyGen;}
	public void setMaxEnergyGen(double maxEnergyGen){this.maxEnergyGen = maxEnergyGen;}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public HashMap<String, Integer> getCurMaterials(){
		return curMaterials;
	}
	public HashMap<String, Integer> getNeededMaterials(){
		return neededMaterials;
	}
	public boolean isConstructing(){
		return constructing;
	}
	public void setConstructing(boolean constructing){
		this.constructing = constructing;
	}


	/* *****************************
	 *** Serializing Information ***
	 *******************************/

	/**
	 * Save the information in such a way that we can re-create the object
	 * Essentially serializing the structure
	 * @return NBTTagCompound
	 */
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString(NBT_PLAYER, player.toString());
		tag.setString(NBT_NAME, name);
		tag.setInteger(NBT_TYPE, type);
		tag.setInteger(NBT_PROGRESS, progress);
		tag.setDouble(NBT_ENERGY, energy);
		tag.setDouble(NBT_MAX_ENERGY_GEN, maxEnergyGen);
		tag.setInteger(NBT_STAGE, stage);

		char sepChar = ';';
		StringBuilder curMats = new StringBuilder();
		for (Map.Entry<String, Integer> entry : curMaterials.entrySet())
		{
			String oreName = entry.getKey();
			int count = entry.getValue();
			curMats = curMats.append(oreName).append(",").append(count).append(sepChar);
		}
		StringBuilder totMats = new StringBuilder();
		for (Map.Entry<String, Integer> entry : neededMaterials.entrySet())
		{
			String oreName = entry.getKey();
			int count = entry.getValue();
			totMats = totMats.append(oreName).append(",").append(count).append(sepChar);
		}
		// Trim trailing ';'
		tag.setString(NBT_CUR_MATS, curMats.toString().substring(0, curMats.length() - 1));
		tag.setString(NBT_TOT_MATS, totMats.toString().substring(0, curMats.length() - 1));
		tag.setBoolean(NBT_CONSTRUCTING, constructing);


		return tag;
	}

	/**
	 * Deseralize the saved structure
	 * @param nbt
	 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.player = UUID.fromString(nbt.getString(NBT_PLAYER));
		this.name = nbt.getString(NBT_NAME);
		this.type = nbt.getInteger(NBT_TYPE);
		this.progress = nbt.getInteger(NBT_PROGRESS);
		this.energy = nbt.getDouble(NBT_ENERGY);
		this.maxEnergyGen = nbt.getDouble(NBT_MAX_ENERGY_GEN);
		this.stage = nbt.getInteger(NBT_STAGE);

		String curMats = nbt.getString(NBT_CUR_MATS);
		String totMats = nbt.getString(NBT_TOT_MATS);
		this.curMaterials = readHashMapFromString(curMats);
		this.neededMaterials = readHashMapFromString(totMats);
		this.constructing = nbt.getBoolean(NBT_CONSTRUCTING);
	}

	private HashMap<String, Integer> readHashMapFromString(String input) {
		HashMap<String, Integer> result = new HashMap<>();

		String[] pairs = input.split(";");
		for(String s : pairs) {
				String[] kvp = s.split(",");
				int count = Integer.parseInt(kvp[1]);
				result.put(kvp[0], count);
		}

		return result;
	}
}
