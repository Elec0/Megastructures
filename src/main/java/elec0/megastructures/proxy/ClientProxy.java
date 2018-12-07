package elec0.megastructures.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import elec0.megastructures.ModBlocks;
import elec0.megastructures.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
	}

	@Override
	public void init(FMLInitializationEvent e)
	{
		super.init(e);

		//MinecraftForge.EVENT_BUS.register(new InputHandler());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModBlocks.initModels();
		ModItems.initModels();
	}

	@Override
	public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
		return Minecraft.getMinecraft().addScheduledTask(runnableToSchedule);
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
