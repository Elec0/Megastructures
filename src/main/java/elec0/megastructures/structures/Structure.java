package elec0.megastructures.structures;

import elec0.megastructures.MegastructuresUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.security.KeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Structure implements INBTSerializable<NBTTagCompound>
{

	private UUID player;					// The player who owns this structure
	private String name;					//
	private int type;						// index for TYPES array
	private int[] progress;					// 0-100, percent of current curStage construction completion
	private double[] realProgress;			// The actual progress to be calculated, not just the int
	private double energy;					// Amount of RF that is stored in this structure (1 RF = 2.5J)
	private double maxEnergyGen;			// Max amount of energy possible to generate per tick
	private int curStage;					// Some stages don't generate power or have different options
											// Stage 0 is non-functional in every way. No power, computation, anything
	private int maxStage;					// The total number of stages a given structure has
	private String[] stageName;
	private String[] stageDesc;
	private HashMap<String, Integer>[] curMaterials;		// Map of oredict,count for the number of mats for the current construction that are in the network
	private HashMap<String, Integer>[] neededMaterials;		// Map of oredict,count for the total number of mats needed for the current construction
	private boolean constructing;			// Are we building something or not

	private World world;


	public static final String NBT_PLAYER = "player";
	public static final String NBT_NAME = "name";
	public static final String NBT_TYPE = "type";
	public static final String NBT_PROGRESS = "progress";
	public static final String NBT_REAL_PROGRESS = "realProgress";
	public static final String NBT_ENERGY = "energy";
	public static final String NBT_MAX_ENERGY_GEN = "maxEnergyGen";
	public static final String NBT_STAGE = "curStage";
	public static final String NBT_CUR_MATS = "curMats";
	public static final String NBT_TOT_MATS = "totMats";
	public static final String NBT_CONSTRUCTING = "constructing";
	public static final String NBT_MAX_STAGE = "dsMaxStage";
	public static final String NBT_STAGE_NAME = "stageName";
	public static final String NBT_STAGE_DESC = "stageDesc";

	public static final String[] TYPES = new String[]{"Dyson Sphere"};
	public static final double PROG_THRESHOLD = 0.000000001d; // Also the min. amount of stuff you have to start generating power

	/**
	 * Use with caution
	 */
	public Structure() {}

	@SuppressWarnings("unchecked")
	public Structure(UUID player, String name, int maxStage) {
		this.player = player;
		this.name = name;
		this.maxStage = maxStage;
		curMaterials = new HashMap[maxStage + 1];
		neededMaterials = new HashMap[maxStage + 1];
		stageName = new String[maxStage + 1];
		stageDesc = new String[maxStage + 1];
		progress = new int[maxStage + 1];
		realProgress = new double[maxStage + 1];

		for(int i = 0; i < maxStage + 1; ++i) {
			curMaterials[i] = new HashMap<>();
			neededMaterials[i] = new HashMap<>();

			stageName[i] = "";
			stageDesc[i] = "";
		}

	}

	public Structure(NBTTagCompound nbtStructure) {
		deserializeNBT(nbtStructure);
	}


	/**
	 * The main structure logic update loop
	 */
	public void update() {
		generate();
		checkProgress();
	}

	/**
	 * Generate energy, if this is a structure that does that
	 * Default behavior is power increasing as progress does, override this method to change that
	 * Note: No power is generated in stage 0
	 */
	public void generate() {
		if(curStage > 0) {
			double prog = getRealProgress(getCurStage());
			double energyToGen = prog * getMaxEnergyGen(); // Get that percentage of the total output
			setEnergy(getEnergy() + energyToGen);
		}
	}

	/**
	 * Check to see if there is construction ongoing, and if it's done yet
	 * If it is done, call constructionFinished
	 * If it isn't done, figure out what percentage is done
	 * Progress is an array with each value being the progress of the given stage
	 * Once a stage is finished, leave progress at 100
	 * @return the current progress, or -1 if invalid
	 */
	public int checkProgress() {
		double totalNeeded = 0;
		double totalHave = 0;


		// Make sure shit's loaded first
		if(getNeededMaterials() == null)
			return -1;

		// Loop through all the materials and check how much of them we have gotten across the board
		for (Map.Entry<String, Integer> entry : getNeededMaterials().entrySet())
		{
			String oreName = entry.getKey();
			int count = entry.getValue();

			totalNeeded += count;
			if(getCurMaterials() != null && getCurMaterials().containsKey(oreName))
				totalHave += getCurMaterials().get(oreName);
		}
		setRealProgress(getCurStage(), totalHave/totalNeeded);
		setProgress(getCurStage(), (int)((getRealProgress(getCurStage()) * 100)));

		// This needs to go above constructionFinished
		// It shouldn't always be constructing
		setConstructing(true);

		// There's probably somewhere better to put this, but for now it's here
		if(getProgress(getCurStage()) >= 100)
			constructionFinished();


		return getProgress(getCurStage());
	}

	/**
	 * Finish the construction of the current stage
	 */
	protected void constructionFinished() {
		if(getWorld() != null)
			getWorld().getMinecraftServer().getPlayerList().getPlayerByUUID(player).sendMessage(
					new TextComponentString("Structure " + getName() + " has completed Stage " + getCurStage() + "!"));
		else
			System.err.println("getWorld() is null. Something has gone wrong.");

		// We've gotten here, so that means that we have all the needed requirements
		setConstructing(false);
		setRealProgress(getCurStage(), 100);

		// If we are at the max stage, then don't increment this value, otherwise we're gonna get errors
		// Plus we check the current stage's progress in generate()
		if(getCurStage() != getMaxStage())
			setCurStage(getCurStage() + 1);
	}

	/**
	 * Deduct amount of power from structure
	 * @param amt
	 */
	public void consume(double amt) {
		if(curStage > 0)
			setEnergy(getEnergy() - amt);
	}

	public void consume(int amt) { consume((double)amt); }


	/**
	 * Put some material into the map to tell what is required to build this thing.
	 * @param oreDict
	 * @param count
	 */
	public void addNeededConsutructionMaterial(String oreDict, int stage, int count) {
		if(neededMaterials != null) {
			neededMaterials[stage].put(oreDict, count);
			curMaterials[stage].put(oreDict, 0);
			setConstructing(true);
		}
	}

	/**
	 * Add to the curMaterials, this is actual items that the user inputs.
	 * @param oreDict
	 * @param count
	 */
	public boolean addMaterial(String oreDict, int count) throws KeyException {
		// This will happen if a material has 2 different values in oreDict, one that is accepted by the structure and one that isn't
		if(getNeededMaterials() == null)
			return false;

		if(!getNeededMaterials().containsKey(oreDict))
			throw new KeyException("Trying to add material " + oreDict + " when it isn't needed");

		// Make sure we're actually building something before we eat materials
		if(!isConstructing())
			return false;

		if(!getCurMaterials().containsKey(oreDict))
			getCurMaterials().put(oreDict, 0);

		// Stop if we try to put too many in
		if(getCurMaterials().get(oreDict) + count > getNeededMaterials().get(oreDict))
		{
			int diff = (getCurMaterials().get(oreDict) + count) - getNeededMaterials().get(oreDict);
			if(diff == 0) // We need no more to complete the structure
				return false;
			else // We do actually need some amount of this transfer to complete the structure
			{
				// TODO: Figure out how to give the excess back
				getCurMaterials().put(oreDict, getNeededMaterials().get(oreDict));

				return true;
			}
		}

		getCurMaterials().put(oreDict, getCurMaterials().get(oreDict) + count);
		System.out.println("Adding " + oreDict + ": " + getCurMaterials().get(oreDict) + "/" + getNeededMaterials().get(oreDict));
		System.out.println("Progress: " + getRealProgress(getCurStage()));

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
	public int get(int stage){return progress[stage];}
	public int getProgress(int stage){return this.progress[stage];}
	public void setProgress(int stage, int progress){this.progress[stage] = progress;}
	public double getEnergy() {return energy;}
	public void setEnergy(double energy){this.energy = energy;}
	public double getMaxEnergyGen(){return maxEnergyGen;}
	public void setMaxEnergyGen(double maxEnergyGen){this.maxEnergyGen = maxEnergyGen;}
	public int getCurStage() {return curStage;}
	public void setCurStage(int curStage) {this.curStage = curStage;}
	public HashMap<String, Integer> getCurMaterials(){return curMaterials[curStage];}
	@Nullable
	public HashMap<String, Integer> getNeededMaterials(){
		if(neededMaterials == null || curStage > neededMaterials.length || curStage < 0)
			return null;
		return neededMaterials[curStage];
	}
	@SuppressWarnings("unchecked")
	public void setNeededMaterialsArray(HashMap[] neededMaterials) {
		this.neededMaterials = neededMaterials;
		// Loop through the needed materials and init curMaterials with that type
		for(int i = 0; i < neededMaterials.length; ++i) {
			HashMap<String, Integer> map = neededMaterials[i];
			for (Map.Entry entry : map.entrySet()) {
				curMaterials[i].put(entry.getKey().toString(), 0);

			}
		}
	}
	public boolean isConstructing(){return constructing;}
	public void setConstructing(boolean constructing){this.constructing = constructing;}
	public int getMaxStage() {return maxStage;}
	public void setMaxStage(int maxStage) {this.maxStage = maxStage;}
	public String getStageName() {return stageName[curStage];}
	public void setStageName(String stageName) {this.stageName[curStage] = stageName;}
	public String getStageDesc() {return stageDesc[curStage];}
	public void setStageDesc(String stageDesc) {this.stageDesc[curStage] = stageDesc;}
	public String[] getAllStageNames() { return stageName; }
	public String[] getAllStageDescs() { return stageDesc; }
	public World getWorld(){return world;}
	public void setWorld(World world){this.world = world;}
	public double getRealProgress(int stage){
		if(realProgress[stage] < PROG_THRESHOLD)
			return 0;
		return realProgress[stage];
	}
	public void setRealProgress(int stage, double realProgress){this.realProgress[stage] = realProgress;}


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
		tag.setString(NBT_PROGRESS, StringUtils.join(ArrayUtils.toObject(progress), ";"));
		tag.setString(NBT_REAL_PROGRESS, StringUtils.join(ArrayUtils.toObject(realProgress), ";"));
		tag.setDouble(NBT_ENERGY, energy);
		tag.setDouble(NBT_MAX_ENERGY_GEN, maxEnergyGen);
		tag.setInteger(NBT_STAGE, curStage);

		tag.setInteger(NBT_MAX_STAGE, maxStage);
		tag.setString(NBT_STAGE_NAME, String.join(";", stageName));
		tag.setString(NBT_STAGE_DESC, String.join(";", stageDesc));

		for(int i = 0; i < maxStage + 1; ++i)
		{
			tag.setString(NBT_CUR_MATS + i,  MegastructuresUtils.createStringFromHashMap(curMaterials[i]));
			//tag.setString(NBT_TOT_MATS + i,  MegastructuresUtils.createStringFromHashMap(neededMaterials[i]));
		}
		tag.setBoolean(NBT_CONSTRUCTING, constructing);


		return tag;
	}

	/**
	 * Deseralize the saved structure
	 * @param nbt
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey(NBT_PLAYER))
			this.player = UUID.fromString(nbt.getString(NBT_PLAYER));
		if(nbt.hasKey(NBT_NAME))
			this.name = nbt.getString(NBT_NAME);
		if(nbt.hasKey(NBT_TYPE))
			this.type = nbt.getInteger(NBT_TYPE);
		if(nbt.hasKey(NBT_PROGRESS))
			this.progress = Arrays.stream(nbt.getString(NBT_PROGRESS).split(";")).mapToInt(num -> (int)Double.parseDouble(num)).toArray();
		if(nbt.hasKey(NBT_REAL_PROGRESS))
			this.realProgress = Arrays.stream(nbt.getString(NBT_REAL_PROGRESS).split(";")).mapToDouble(Double::parseDouble).toArray();
		if(nbt.hasKey(NBT_ENERGY))
			this.energy = nbt.getDouble(NBT_ENERGY);
		if(nbt.hasKey(NBT_MAX_ENERGY_GEN))
			this.maxEnergyGen = nbt.getDouble(NBT_MAX_ENERGY_GEN);
		if(nbt.hasKey(NBT_STAGE))
			this.curStage = nbt.getInteger(NBT_STAGE);
		if(nbt.hasKey(NBT_MAX_STAGE))
			this.maxStage = nbt.getInteger(NBT_MAX_STAGE);
		if(nbt.hasKey(NBT_CONSTRUCTING))
			this.constructing = nbt.getBoolean(NBT_CONSTRUCTING);
		if(nbt.hasKey(NBT_STAGE_NAME))
			this.stageName = nbt.getString(NBT_STAGE_NAME).split(";");
		if(nbt.hasKey(NBT_STAGE_DESC))
			this.stageDesc = nbt.getString(NBT_STAGE_DESC).split(";");

		// Since we have multiple stages stored here, initialize the array
		this.curMaterials = new HashMap[maxStage + 1];
		//this.neededMaterials = new HashMap[dsMaxStage + 1];

		// Now go through and load the values of each one
		for(int i = 0; i < maxStage + 1; ++i) {
			String curMats = nbt.getString(NBT_CUR_MATS + i);
			//String totMats = nbt.getString(NBT_TOT_MATS + i);
			this.curMaterials[i] = MegastructuresUtils.readHashMapFromString(curMats);
			//this.neededMaterials[i] =  MegastructuresUtils.readHashMapFromString(totMats);
		}
	}
}
