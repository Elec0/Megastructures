package elec0.megastructures.input;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class InputHandler 
{	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if(KeyBindings.powerKey1.isPressed())
		{
			//PacketHandler.INSTANCE.sendToServer(new PacketSendTerminalData(1, KeyBindings.powerKey1.getKeyCode()));
		}
		if(KeyBindings.powerKey2.isPressed())
		{
			//PacketHandler.INSTANCE.sendToServer(new PacketSendTerminalData(2, KeyBindings.powerKey2.getKeyCode()));
		}
		
	}
}
