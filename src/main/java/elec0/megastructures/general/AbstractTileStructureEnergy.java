package elec0.megastructures.general;

import elec0.megastructures.blocks.BaseBlock;
import elec0.megastructures.blocks.powertap.PowerTapBlock;
import elec0.megastructures.capabilities.StructureData;
import elec0.megastructures.structures.Structure;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import scala.xml.dtd.impl.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The base class for any and all blocks that interface with the megastructures to get power in/out of the network
 * The blocks themselves aren't going to store any power, they're going to draw from the structure network via
 * a connection to the End.
 */
public abstract class AbstractTileStructureEnergy extends TileEntity
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

		// When initializing I think blocks tick before stuff is loaded
		if(userStructures == null)
			return;

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

	/**
	 * This is called in the constructor
	 * @param newFaces
	 */
	protected void setPowerFaces(EnumFacing[] newFaces) {
		this.powerFaces = newFaces;
	}

	/**
	 * To be called at the same time at setOwner
	 * Need to rotate newFaces such that North is towards FACING
	 * We want to apply one of three functions to all faces: rotateY(), rotateYCCW(), or getOpposite()
	 */
	public void initPowerFaces() {
		EnumFacing facing = this.world.getBlockState(this.pos).getValue(BaseBlock.FACING);
		EnumFacingInterface enumRotate = null;
		switch(facing) {
			case NORTH:
				// Do nothing
				return;
			case EAST:
				enumRotate = EnumFacing::rotateY;
				break;
			case WEST:
				enumRotate = EnumFacing::rotateYCCW;
				break;
			case SOUTH:
				enumRotate = EnumFacing::getOpposite;
				break;
		}
		List<EnumFacing> newFaces = new ArrayList<>();
		for(EnumFacing face : powerFaces) {
			newFaces.add(enumRotate.init(face));
		}
		this.powerFaces = newFaces.toArray(new EnumFacing[]{});
	}

	@FunctionalInterface
	public interface EnumFacingInterface {
		EnumFacing init(EnumFacing dir);
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
		markDirty();
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
		if(compound.hasKey(NBT_OWNER))
			this.owner = UUID.fromString(compound.getString(NBT_OWNER));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if(owner != null)
			compound.setString(NBT_OWNER, owner.toString());

		return compound;
	}
}
