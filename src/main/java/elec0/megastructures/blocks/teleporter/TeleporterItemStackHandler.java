package elec0.megastructures.blocks.teleporter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;

/**
 * Used for only accepting items into the matter teleporter that are currently valid. Otherwise it'll eat everything
 * that is provided, which is just a recipe for disaster.
 */
public class TeleporterItemStackHandler extends ItemStackHandler
{
	private UUID playerNetwork;

	public TeleporterItemStackHandler(int size) {
		super(size);
	}
	public TeleporterItemStackHandler(int size, UUID playerNetwork) {
		super(size);
		this.playerNetwork = playerNetwork;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		String[] accepted = new String[]{"ingotIron"};

		// We need to check all the IDs, since often there are items that have multiple IDs, although idk if we're
		// going to be accepting those, it's always good to plan for the future.
		for(int id : OreDictionary.getOreIDs(stack)) {
			// Check if the ore dict name is in the currently accepted list
			String name = OreDictionary.getOreName(id);
			boolean contains = Arrays.stream(accepted).anyMatch(name::equals);

			// Since we're checking all of the possible IDs, only return false if they all fail
			if(contains)
				return true;
		}
		return false;
	}
}
