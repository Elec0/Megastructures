package elec0.megastructures.general;

import elec0.megastructures.capabilities.StructureData;
import elec0.megastructures.structures.Structure;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.UUID;

/**
 * The base class for any and all blocks that interface with the megastructures to get power in/out of the network
 * The blocks themselves aren't going to store any power, they're going to draw from the structure network via
 * a connection to the End.
 */
public abstract class AbstractTileStructureEnergy extends TileEntity implements IRestorableTileEntity
{
	// ----------------------------------------------------------------------------------------
	protected MyEnergyStorage energyStorage;

	private UUID owner;
	private EnumFacing[] powerFaces = EnumFacing.VALUES;

	public static final String NBT_OWNER = "owner";
	// ----------------------------------------------------------------------------------------

	public AbstractTileStructureEnergy() {
		energyStorage = new MyEnergyStorage(0, 0);
	}

	public AbstractTileStructureEnergy(UUID owner) {
		this.owner = owner;
		energyStorage = new MyEnergyStorage(0, 0);
	}

	/**
	 * Loop through the surrounding faces and push energy into the accepting devices
	 */
	protected void sendEnergy() {
		// Get the user structure data
		StructureData structureData = StructureData.getData(world);
		double energyStored = 0;

		// Get the energy stored in all the structures
		int highestPowerIndex = -1;
		double highestPower = 0;
		List<Structure> userStructures = structureData.getUserStructures(getOwner());

		// Calculate the total energy of the network, and get the highest energy structure to draw power from
		for(int i = 0; i < userStructures.size(); ++i) {
			double energy = userStructures.get(i).getEnergy();
			if(energy > highestPower) {
				highestPowerIndex = i;
				highestPower = energy;
			}
			energyStored += energy;
		}

		if(energyStored > 0) {
			for(EnumFacing facing : powerFaces) {
				TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

				if(tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
					IEnergyStorage handler = tileEntity.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());

					if(handler != null && handler.canReceive()) {
						// It's easily possible that we are going to have more energy in the network than MAX_VALUE,
						// and we want to avoid overflow errors, so give the max value int if that's the case.
						int energyToReceive = energyStored > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)energyStored;
						// The amount of energy we're transmitting out
						int accepted = handler.receiveEnergy(energyToReceive, false);

						// Deduce the energy from the structure with the highest power
						userStructures.get(highestPowerIndex).consume(accepted);

						double energyLeft = energyStored - accepted;
						// If it would draw more power than we have, break off
						if(energyLeft <= 0)
							break;
					}
				}
			}
			// Save the stuff
			markDirty();
			structureData.save(world);
		}
	}

	protected void setPowerFaces(EnumFacing[] newFaces) {
		this.powerFaces = newFaces;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}
	public UUID getOwner() { return owner; }

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

	// *********************************
	// ******* Generic NBT Stuff *******
	// *********************************
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readRestorableFromNBT(compound);
	}

	public void readRestorableFromNBT(NBTTagCompound compound) {
		this.owner = UUID.fromString(compound.getString(NBT_OWNER));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeRestorableToNBT(compound);
		return compound;
	}

	public void writeRestorableToNBT(NBTTagCompound compound) {
		compound.setString(NBT_OWNER, owner.toString());
	}
}
