package elec0.megastructures.general;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class AbstractTileEnergy extends TileEntity implements IRestorableTileEntity
{
	// ----------------------------------------------------------------------------------------
	protected MyEnergyStorage energyStorage;
	protected int clientEnergy = -1;

	private EnumFacing[] powerFaces = EnumFacing.VALUES;
	// ----------------------------------------------------------------------------------------

	public AbstractTileEnergy(int maxEnergy) {
		energyStorage = new MyEnergyStorage(maxEnergy, 0);
		energyStorage.setEnergy(maxEnergy);
	}

	/**
	 * Loop through the surrounding faces and push energy into the accepting devices
	 */
	protected void sendEnergy() {
		if (energyStorage.getEnergyStored() > 0) {
			for (EnumFacing facing : powerFaces) {

				TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

				if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite()))
				{

					IEnergyStorage handler = tileEntity.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());

					if (handler != null && handler.canReceive()) {
						// The amount of energy we're transmitting out
						int accepted = handler.receiveEnergy(energyStorage.getEnergyStored(), false);
						energyStorage.consumePower(accepted);

						if (energyStorage.getEnergyStored() <= 0)
							break;
					}
				}
			}
			markDirty();
		}
	}

	protected void setPowerFaces(EnumFacing[] newFaces) {
		this.powerFaces = newFaces;
	}


	// *********************************
	// ******** Generic Energy *********
	// *********************************
	public int getClientEnergy() {
		return clientEnergy;
	}

	public void setClientEnergy(int clientEnergy) {
		this.clientEnergy = clientEnergy;
	}

	public int getEnergy() {
		return energyStorage.getEnergyStored();
	}


	// *********************************
	// ******* Generic NBT Stuff *******
	// *********************************
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readRestorableFromNBT(compound);
	}

	public void readRestorableFromNBT(NBTTagCompound compound) {
		energyStorage.setEnergy(compound.getInteger("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeRestorableToNBT(compound);
		return compound;
	}

	public void writeRestorableToNBT(NBTTagCompound compound) {
		compound.setInteger("energy", energyStorage.getEnergyStored());
	}

//	@Override
//	public GuiContainer createGui(EntityPlayer player) {
//		return new GuiGenerator(this, new ContainerGenerator(player.inventory, this));
//	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			// We only have the power interface on the faces that are specified
			for(EnumFacing face : powerFaces)
				if(face == facing)
					return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			// We only have the power interface on the faces that are specified
			for(EnumFacing face : powerFaces)
				if(face == facing)
					return CapabilityEnergy.ENERGY.cast(energyStorage);
		}
		return super.getCapability(capability, facing);
	}
}
