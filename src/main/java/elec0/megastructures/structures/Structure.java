package elec0.megastructures.structures;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Structure implements INBTSerializable<NBTTagCompound>
{

	private UUID player;					// The player who owns this structure
	private final String NBT_PLAYER = "player";
	private String name;					//
	private final String NBT_NAME = "name";


	public Structure(NBTTagCompound nbtStructure) {
		deserializeNBT(nbtStructure);
	}


	/**
	 * The main structure logic update loop
	 */
	public void update() {
		System.out.println("Structure Update");
	}


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
	}
}
