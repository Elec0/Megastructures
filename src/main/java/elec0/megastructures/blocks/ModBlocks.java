package elec0.megastructures.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
	
	public static TerminalBlock terminalBlock;
	
	public static void init()
	{
		terminalBlock = new TerminalBlock();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels()
	{
		
	}
}
