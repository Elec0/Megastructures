package elec0.megastructures.proxy;

import elec0.megastructures.Guis.TeleporterGui;
import elec0.megastructures.Guis.TerminalGui;
import elec0.megastructures.blocks.teleporter.TeleporterContainer;
import elec0.megastructures.blocks.teleporter.TeleporterTileEntity;
import elec0.megastructures.blocks.terminal.TerminalTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIProxy implements IGuiHandler
{
	public static final int TERMINAL_GUI 		= 0;
	public static final int TELEPORTER_GUI 		= 1;

	// Get the teleporter container whatever
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TeleporterTileEntity) {
			return new TeleporterContainer(player.inventory, (TeleporterTileEntity) te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

		// Make sure we aren't trying to open something weird where the tile entity doesn't match the ID or whatever
		if(te instanceof TerminalTileEntity) {
			if(ID == TERMINAL_GUI)
				return new TerminalGui();
		}
		else if (te instanceof TeleporterTileEntity) {
			if(ID == TELEPORTER_GUI) {
				TeleporterTileEntity containerTileEntity = (TeleporterTileEntity) te;
				return new TeleporterGui(containerTileEntity, new TeleporterContainer(player.inventory, containerTileEntity));
			}
		}

		return null;
	}
}