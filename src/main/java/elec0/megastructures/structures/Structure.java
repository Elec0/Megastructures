package elec0.megastructures.structures;

import net.minecraft.nbt.NBTTagCompound;

public class Structure
{
	/**
	 * Deseralize the saved structure
	 * @param nbtStructure
	 */
	public Structure(NBTTagCompound nbtStructure) {

	}

	/**
	 * Save the information in such a way that we can re-create the object
	 * Essentially serializing the structure
	 * @return
	 */
	public NBTTagCompound getNBTTag() {
		return null;
	}
}
