package elec0.megastructures.blocks.teleporter;

import elec0.megastructures.structures.Structure;
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

import java.security.KeyException;
import java.util.UUID;

public class TeleporterTileEntity extends TileEntity implements ITickable
{
	public static final int SIZE = 9;
	private UUID owner;

	public static final String NBT_OWNER = "owner";

	// This item handler will hold our nine inventory slots
	private TeleporterItemStackHandler itemStackHandler = new TeleporterItemStackHandler(SIZE) {
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
			consumeItems();
		}
	}

	/**
	 * The logic for eating the incoming items
	 */
	private void consumeItems() {

		// Read then clear the stack
		ItemStack curStack;
		for(int i = 0; i < itemStackHandler.getSlots(); ++i) {
			curStack = itemStackHandler.getStackInSlot(i);
			// Check if the stack is nothing, or if we don't want to accept it
			if(curStack.isEmpty() || !itemStackHandler.isItemValid(i, curStack))
				continue;

			for(int id : OreDictionary.getOreIDs(curStack))
			{
				// This is saved in isItemValid to reduce on loops
				Structure validStruct = itemStackHandler.getValidStructure();

				if(validStruct == null)
					continue;

				try
				{
					// If the material is accepted by the structure, only then actually remove it
					// This does currently eat the excess of a stack, but whatever
					if (validStruct.addMaterial(OreDictionary.getOreName(id), curStack.getCount())) {
						itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
					}
				 }
				 catch(KeyException e) { e.printStackTrace(); }
			}
		}
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
		itemStackHandler.setPlayerNetwork(owner);
		itemStackHandler.setWorld(this.world);
		markDirty();
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey(NBT_OWNER)) {
			owner = UUID.fromString(compound.getString(NBT_OWNER));
			itemStackHandler.setPlayerNetwork(owner);
			itemStackHandler.setWorld(world);
		}
		if (compound.hasKey("items")) {
			itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("items", itemStackHandler.serializeNBT());
		if(owner != null)
			compound.setString(NBT_OWNER, owner.toString());

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
