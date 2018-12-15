package elec0.megastructures.blocks.teleporter;

import elec0.megastructures.capabilities.StructureData;
import elec0.megastructures.structures.Structure;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Used for only accepting items into the matter teleporter that are currently valid. Otherwise it'll eat everything
 * that is provided, which is just a recipe for disaster.
 */
public class TeleporterItemStackHandler extends ItemStackHandler
{
	private UUID playerNetwork;
	private World world;
	private Structure validStructure;

	public TeleporterItemStackHandler(int size) {
		super(size);
	}
	public void setPlayerNetwork(UUID playerNetwork)
	{
		this.playerNetwork = playerNetwork;
	}
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Check if the inputted item is valid in any of the structures that are currently in construction
	 * @param slot
	 * @param stack
	 * @return
	 */
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if(world == null || world.isRemote)
			return true;

		// Get the current structure and retrieve neededItems for this stage
		StructureData structureData = StructureData.getData(world);
		List<Structure> userStructures = structureData.getUserStructures(playerNetwork);

		if(userStructures == null)
			return false;

		for(Structure s : userStructures) {
			if(!s.isConstructing())
				continue;

			// We need to check all the IDs, since often there are items that have multiple IDs, although idk if we're
			// going to be accepting those, it's always good to plan for the future.
			for(int id : OreDictionary.getOreIDs(stack)) {
				// Check if the ore dict name is in the currently accepted list
				String name = OreDictionary.getOreName(id);

				boolean contains = s.getNeededMaterials().containsKey(name);

				// Since we're checking all of the possible IDs, only return false if they all fail
				if(contains) {
					// If this structure uses the given material, check if we actually need any more of it
					if(s.getCurMaterials().get(name) < s.getNeededMaterials().get(name)) {
						// Save the structure we found
						validStructure = s;
						return true;
					}
					// If it isn't needed, keep looping
				}
			}
		}
		return false;
	}

	public Structure getValidStructure() {return validStructure;}
}
