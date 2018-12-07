package elec0.megastructures;

import elec0.megastructures.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Megastructures.MODID, name = Megastructures.MODNAME, version =  Megastructures.MODVERSION, dependencies = "required-after:forge@[14.19.1.2197,)", useMetadata = true)
public class Megastructures
{
	public static final String MODID = "megastructures";
	public static final String MODNAME = "Megastructures";
	public static final String MODVERSION = "0.0.1";


	@SidedProxy(clientSide = "elec0.megastructures.proxy.ClientProxy", serverSide = "elec0.megastructures.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static Megastructures instance;

	public static CreativeTabs creativeTab = new CreativeTabs("megastructures") {
		public ItemStack getTabIconItem() {
			return new ItemStack(ModBlocks.terminalBlock);
		}
	};

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
	
	@Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) 
	{
        //event.registerServerCommand(new SimplyPowersCommand());
    }

}
