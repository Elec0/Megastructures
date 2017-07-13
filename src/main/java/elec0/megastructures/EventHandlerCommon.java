package elec0.megastructures;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandlerCommon 
{

	private static int worldTickCount = 0;

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.side == Side.SERVER)
		{
			worldTickCount++;
			if(worldTickCount == 10) // Run this once every half second
			{

				worldTickCount = 0;
			}
		}
	}

	// Player events
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
