package elec0.megastructures.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler 
{

	//public static final ResourceLocation POWER_CAP = new ResourceLocation(SimplyPowers.MODID, "powerdata");
	
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent.Entity event)
	{
		if (!(event.getEntity() instanceof EntityPlayer)) 
			return;
		//event.addCapability(POWER_CAP, new PowerDataProvider());
	}
}
