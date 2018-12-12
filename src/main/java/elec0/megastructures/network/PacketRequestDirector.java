package elec0.megastructures.network;

import elec0.megastructures.general.Vector2i;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestDirector {

	public PacketRequestDirector()
	{
	}

	/**
	 * Requests relating to getting galaxy/system/planet data for the terminal
	 * @param sectorRequest
	 */
	public static IMessage request(Vector2i sectorRequest)
	{
		return new PacketRequestTerminalData(sectorRequest);
	}

	/**
	 * Requests for commands
	 * @param command 'create','delete'
	 * @param args Parameter for incoming data
	 */
	public static IMessage request(String command, Object args)
	{
		switch(command) {
			case "create":
				return new PacketRequestStructure(command);
			case "delete":
				return new PacketRequestStructure(command);
		}

		return null;
	}

	public static IMessage request(String command) { return request(command, null); }
}
