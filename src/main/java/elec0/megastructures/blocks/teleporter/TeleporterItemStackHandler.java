package elec0.megastructures.blocks.teleporter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Used for only accepting items into the matter teleporter that are currently valid. Otherwise it'll eat everything
 * that is provided, which is just a recipe for disaster.
 */
public class TeleporterItemStackHandler extends ItemStackHandler
{
	public TeleporterItemStackHandler(int size) {
		super(size);
	}

	/**
	 * Check if the inputted item is valid in any of the structures that are currently in construction
	 * @param slot
	 * @param stack
	 * @return
	 */
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return true;
	}
}
