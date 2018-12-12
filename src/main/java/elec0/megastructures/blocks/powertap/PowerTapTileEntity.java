package elec0.megastructures.blocks.powertap;

import elec0.megastructures.general.AbstractTileStructureEnergy;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class PowerTapTileEntity extends AbstractTileStructureEnergy implements ITickable
{

	public PowerTapTileEntity() {
		super();
		setPowerFaces(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH});
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			sendEnergy();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}
}
