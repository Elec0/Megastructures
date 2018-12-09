package elec0.megastructures;

import elec0.megastructures.blocks.powertap.PowerTapBlock;
import elec0.megastructures.blocks.teleporter.TeleporterBlock;
import elec0.megastructures.blocks.terminal.TerminalBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems
{

	public static void register(IForgeRegistry<Item> registry) {
		// Inventory blocks for the actual Blocks
		registry.register(new ItemBlock(ModBlocks.terminalBlock).setRegistryName(TerminalBlock.RESOURCE_LOCATION));
		registry.register(new ItemBlock(ModBlocks.powerTapBlock).setRegistryName(PowerTapBlock.RESOURCE_LOCATION));
		registry.register(new ItemBlock(ModBlocks.teleporterBlock).setRegistryName(TeleporterBlock.RESOURCE_LOCATION));

		// Actual items
	}

	@SideOnly(Side.CLIENT)
	public static void initModels()
	{

	}
}
