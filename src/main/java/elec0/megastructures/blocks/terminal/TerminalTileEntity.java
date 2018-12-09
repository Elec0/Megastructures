package elec0.megastructures.blocks.terminal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class TerminalTileEntity extends TileEntity
{
	private UUID placer;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("placer"))
		{
			placer = UUID.fromString(compound.getString("placer"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if(placer != null)
			compound.setString("placer", placer.toString());

		return compound;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	public void setPlacer(UUID placer)
	{
		this.placer = placer;
		markDirty();
	}
	public UUID getPlacer()
	{
		return placer;
	}
}
