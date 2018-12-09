package elec0.megastructures;

import elec0.megastructures.blocks.powertap.PowerTapBlock;
import elec0.megastructures.blocks.powertap.PowerTapTileEntity;
import elec0.megastructures.blocks.teleporter.TeleporterBlock;
import elec0.megastructures.blocks.teleporter.TeleporterTileEntity;
import elec0.megastructures.blocks.terminal.TerminalBlock;
import elec0.megastructures.blocks.terminal.TerminalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	// These are holders, they aren't required but it helps *something*
	@GameRegistry.ObjectHolder("megastructures:terminalblock")
	public static TerminalBlock terminalBlock;

	@GameRegistry.ObjectHolder("megastructures:powertapblock")
	public static PowerTapBlock powerTapBlock;

	@GameRegistry.ObjectHolder("megastructures:teleporterblock")
	public static TeleporterBlock teleporterBlock;

	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		terminalBlock.initModel();
		powerTapBlock.initModel();
		teleporterBlock.initModel();
	}

	/**
	 * Initialize the blocks and tile entity classes
	 * @param registry
	 */
	public static void register(IForgeRegistry<Block> registry)
	{
		registry.register(new TerminalBlock());
		GameRegistry.registerTileEntity(TerminalTileEntity.class, new ResourceLocation(Megastructures.MODID, "_terminalblock"));

		registry.register(new PowerTapBlock());
		GameRegistry.registerTileEntity(PowerTapTileEntity.class, new ResourceLocation(Megastructures.MODID, "_powertapblock"));

		registry.register(new TeleporterBlock());
		GameRegistry.registerTileEntity(TeleporterTileEntity.class, new ResourceLocation(Megastructures.MODID, "_teleporterblock"));

	}
}
