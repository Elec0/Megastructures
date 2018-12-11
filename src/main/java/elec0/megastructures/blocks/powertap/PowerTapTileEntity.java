package elec0.megastructures.blocks.powertap;

import elec0.megastructures.config.PowerTapConfig;
import elec0.megastructures.general.AbstractTileEnergy;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class PowerTapTileEntity extends AbstractTileEnergy implements ITickable
{

	public PowerTapTileEntity() {
		super(PowerTapConfig.MAX_POWER);
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
