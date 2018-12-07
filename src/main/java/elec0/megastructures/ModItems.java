package elec0.megastructures;

import elec0.megastructures.blocks.TerminalBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems
{

	public static void register(IForgeRegistry<Item> registry) {
		registry.register(new ItemBlock(ModBlocks.terminalBlock).setRegistryName(TerminalBlock.TERMINAL_BLOCK));
	}

	@SideOnly(Side.CLIENT)
	public static void initModels()
	{

	}
}
