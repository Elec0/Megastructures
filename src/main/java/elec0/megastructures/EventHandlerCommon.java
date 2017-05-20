package elec0.megastructures;

import elec0.simplypowers.capabilities.IPowerData;
import elec0.simplypowers.capabilities.PowerData;
import elec0.simplypowers.capabilities.PowerDataProvider;
import elec0.simplypowers.input.KeyBindings;
import elec0.simplypowers.network.PacketHandler;
import elec0.simplypowers.network.PacketSendKeyHold;
import elec0.simplypowers.powers.IPower;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EventHandlerCommon 
{	
	@SubscribeEvent
	public void onLivingJumpEvent(LivingJumpEvent event)
	{

	}
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event)
	{

	}
    
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{

	}
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event)
	{
		
	}
	
	@SubscribeEvent
	public void onPlayerFalls(LivingFallEvent event)
	{

	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		
	}
	
	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event)
	{

	}
}
