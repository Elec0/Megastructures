package elec0.megastructures.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class TerminalTileEntity extends TileEntity
{

	private UUID uuid = null;

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
		markDirty();
	}

	public UUID getUUID()
	{
		return uuid;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		uuid = UUID.fromString(compound.getString("placer"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setString("placer", uuid.toString());

		return compound;
	}
}
