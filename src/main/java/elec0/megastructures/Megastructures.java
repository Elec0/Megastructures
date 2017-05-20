package elec0.megastructures;

import elec0.simplypowers.blocks.ModBlocks;
import elec0.simplypowers.capabilities.CapabilityHandler;
import elec0.simplypowers.capabilities.IPowerData;
import elec0.simplypowers.capabilities.PowerData;
import elec0.simplypowers.capabilities.PowerDataStorage;
import elec0.simplypowers.commands.SimplyPowersCommand;
import elec0.simplypowers.input.InputHandler;
import elec0.simplypowers.input.KeyBindings;
import elec0.simplypowers.items.ModItems;
import elec0.simplypowers.network.PacketHandler;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Megastructures.MODID, name = Megastructures.MODNAME, version =  Megastructures.MODVERSION, dependencies = "required-after:forge@[13.19.1.2197,)", useMetadata = true)
public class Megastructures 
{
	public static final String MODID = "megastructures";
	public static final String MODNAME = "Megastructures";
	public static final String MODVERSION = "0.0.1";
	
	
	@SidedProxy
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static Megastructures instance;
	
	
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
	
	public static class CommonProxy
	{
		public void preInit(FMLPreInitializationEvent e)
		{
			ModItems.init();
			ModBlocks.init();
			
			OBJLoader.INSTANCE.addDomain(MODID);
			// Initialize our packet handler. Make sure the name is 20 characters or less!
            //PacketHandler.registerMessages("simplypowers");
		}
		
		 public void init(FMLInitializationEvent e) 
		 {	
			 //MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
			 //MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
			 //CapabilityManager.INSTANCE.register(IPowerData.class, new PowerDataStorage(), PowerData.class);

			 
		 }

        public void postInit(FMLPostInitializationEvent e) 
        {

        }
	}
	
	public static class ClientProxy extends CommonProxy
	{
		@Override 
		public void preInit(FMLPreInitializationEvent e)
		{
			super.preInit(e);
			
			// Init models
			ModItems.initModels();
			ModBlocks.initModels();
		}
		
		@Override
		public void init(FMLInitializationEvent e)
		{
			super.init(e);
			
			//MinecraftForge.EVENT_BUS.register(new InputHandler());
			//KeyBindings.init();
			
		}
	}
	
	public static class ServerProxy extends CommonProxy
	{
		@Override
		public void init(FMLInitializationEvent e)
		{
			
		}
	}

}
