package elec0.megastructures;

import elec0.megastructures.capabilities.StructureData;
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
			if(worldTickCount >= 10) // Run this once every half second
			{
				StructureData structureData = StructureData.getData(event.world);

				// Have the structures actually do their update every half second
				// There's no real reason to have them update every tick, since they don't exist in the 'real' world,
				// and we can interpolate the behavior in between so it'll look mostly seamless.
				structureData.update();

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
