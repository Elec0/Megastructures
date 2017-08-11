package elec0.megastructures.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler 
{
	private static int packetId = 0;
	
	public static SimpleNetworkWrapper INSTANCE = null;
	
	public PacketHandler()
	{
		
	}
	
	public static int nextID()
	{
		return packetId++;
	}
	
	public static void registerMessages(String channelName)
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}
	
	public static void registerMessages()
	{
		// Register messages from server to client
		INSTANCE.registerMessage(PacketSendTerminalData.Handler.class, PacketSendTerminalData.class, nextID(), Side.CLIENT);

		// Register messages from client to server
		INSTANCE.registerMessage(PacketRequestTerminalData.Handler.class, PacketRequestTerminalData.class, nextID(), Side.SERVER);
	}
}
