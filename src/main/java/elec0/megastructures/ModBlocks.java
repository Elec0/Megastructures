package elec0.megastructures;

import elec0.megastructures.blocks.TerminalBlock;
import elec0.megastructures.tileentities.TerminalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	@GameRegistry.ObjectHolder("megastructures:terminalblock")
	public static TerminalBlock terminalBlock;


	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		terminalBlock.initModel();
	}

	public static void register(IForgeRegistry<Block> registry)
	{
		registry.register(new TerminalBlock());
		GameRegistry.registerTileEntity(TerminalTileEntity.class, new ResourceLocation(Megastructures.MODID, "_terminalblock"));
	}
}
