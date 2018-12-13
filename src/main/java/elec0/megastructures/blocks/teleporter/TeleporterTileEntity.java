package elec0.megastructures.blocks.teleporter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TeleporterTileEntity extends TileEntity implements ITickable
{
	// This item handler will hold our nine inventory slots
	private TeleporterItemStackHandler itemStackHandler = new TeleporterItemStackHandler(32) {
		@Override
		protected void onContentsChanged(int slot) {
			// We need to tell the tile entity that something has changed so
			// that the chest contents persist
			TeleporterTileEntity.this.markDirty();
		}
	};

	@Override
	public void update() {
		if(!world.isRemote) {
			// Read then clear the stack
			ItemStack curStack;
			for(int i = 0; i < itemStackHandler.getSlots(); ++i) {
				curStack = itemStackHandler.getStackInSlot(i);
				// Check if the stack is nothing, or if we don't want to accept it
				if(curStack.isEmpty() || !itemStackHandler.isItemValid(i, curStack))
					continue;

				for(int id : OreDictionary.getOreIDs(curStack)) {
					System.out.println(OreDictionary.getOreName(id) + ", " + curStack.getCount());
				}
				itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	/**
	 * The logic for eating the incoming items
	 */
	private void consumeItems() {

	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("items")) {
			itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("items", itemStackHandler.serializeNBT());
		return compound;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
		}
		return super.getCapability(capability, facing);
	}
}
