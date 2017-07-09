package elec0.megastructures.proxy;

import elec0.megastructures.GUI.TestContainerGui;
import elec0.megastructures.blocks.TestContainer;
import elec0.megastructures.tileentities.TerminalTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIProxy implements IGuiHandler
{
	// This is written poorly, it should be more general.

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TerminalTileEntity) {
			return new TestContainer(player.inventory, (TerminalTileEntity) te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TerminalTileEntity) {
			TerminalTileEntity containerTileEntity = (TerminalTileEntity) te;
			return new TestContainerGui(containerTileEntity, new TestContainer(player.inventory, containerTileEntity));
		}
		return null;
	}
}