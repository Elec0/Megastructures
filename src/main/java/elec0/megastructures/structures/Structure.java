package elec0.megastructures.structures;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Structure implements INBTSerializable<NBTTagCompound>
{

	private UUID player;					// The player who owns this structure
	private String name;					//
	private int type;						// index for TYPES array
	private int progress;					// 0-100, percent of construction completion
	private double energy;					// Amount of RF that is stored in this structure (1 RF = 2.5J)
	private double maxEnergyGen;			// Max amount of energy possible to generate per tick

	public static final String NBT_PLAYER = "player";
	public static final String NBT_NAME = "name";
	public static final String NBT_TYPE = "type";
	public static final String NBT_PROGRESS = "progress";
	public static final String NBT_ENERGY = "energy";
	public static final String NBT_MAX_ENERGY_GEN = "maxEnergyGen";

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
	}

	/**
	 * Generate energy, if this is a structure that does that
	 * Note:
	 */
	public void generate() {
		double prog = getProgress()/100d; // Convert this into a percentage
		double energyToGen = prog * getMaxEnergyGen();
		setEnergy(getEnergy() + energyToGen);
	}

	/**
	 * Deduct amount of power from structure
	 * @param amt
	 */
	public void consume(double amt) {
		setEnergy(getEnergy() - amt);
	}

	public void consume(int amt) { consume(amt); }


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
	}
}
