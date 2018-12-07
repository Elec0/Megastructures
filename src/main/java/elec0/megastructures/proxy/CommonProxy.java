package elec0.megastructures.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import elec0.megastructures.EventHandlerCommon;
import elec0.megastructures.Megastructures;
import elec0.megastructures.ModBlocks;
import elec0.megastructures.ModItems;
import elec0.megastructures.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


@Mod.EventBusSubscriber
public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent e)
	{
		// Initialize our packet handler. Make sure the name is 20 characters or less!
		PacketHandler.registerMessages("megastructures");
		OBJLoader.INSTANCE.addDomain(Megastructures.MODID);

		// Init entities and liquids
	}

	public void init(FMLInitializationEvent e)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Megastructures.instance, new GUIProxy());
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		//MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		//CapabilityManager.INSTANCE.register(IPowerData.class, new PowerDataStorage(), PowerData.class);
	}

	public void postInit(FMLPostInitializationEvent e)
	{
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.register(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		ModItems.register(event.getRegistry());
	}

	public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
		throw new IllegalStateException("This should only be called from client side");
	}

	public EntityPlayer getClientPlayer() {
		throw new IllegalStateException("This should only be called from client side");
	}

}