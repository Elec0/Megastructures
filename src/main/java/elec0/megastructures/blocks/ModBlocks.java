package elec0.megastructures.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

	@GameRegistry.ObjectHolder("megastructures:terminalblock")
	public static TerminalBlock terminalBlock;

	public static void init()
	{

	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		terminalBlock.initModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels()
	{

	}
}
