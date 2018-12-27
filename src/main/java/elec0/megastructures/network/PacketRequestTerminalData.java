package elec0.megastructures.network;

import elec0.megastructures.capabilities.MSWorldSavedData;
import elec0.megastructures.capabilities.StructureData;
import elec0.megastructures.general.Vector2i;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestTerminalData implements IMessage
{

	private Vector2i sectorRequest;
	private boolean structure = false; // Determines if we are sending structure information

	public static final int OPT_SECTOR = 0;
	public static final int OPT_STRUCT = 1;
	public static final int OPT_BOTH = 2;


	public PacketRequestTerminalData(){}

	/**
	 * For updating structure information and no sector information
	 * @param structure ignored
	 */
	public PacketRequestTerminalData(boolean structure) {
		// Not using the default constructor becuase I'm not sure if something else calls it, I think it does
		this.structure = true;
		this.sectorRequest = null;

	}
	/**
	 * Get a new sector
	 * @param sectorRequest
	 */
	public PacketRequestTerminalData(Vector2i sectorRequest)
	{
		this.sectorRequest = sectorRequest;
		this.structure = false;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		sectorRequest = null;
		structure = false;

		int opt = buf.readInt();
		switch(opt) {
			case OPT_SECTOR:
				sectorRequest = new Vector2i(buf.readInt(), buf.readInt());
				break;
			case OPT_STRUCT:
				structure = true;
				break;
			case OPT_BOTH:
				structure = true;
				sectorRequest = new Vector2i(buf.readInt(), buf.readInt());
				break;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		if(sectorRequest != null) {
			if(structure)
				buf.writeInt(OPT_BOTH);
			else
				buf.writeInt(OPT_SECTOR);
			buf.writeInt(sectorRequest.getX());
			buf.writeInt(sectorRequest.getY());
		}
		else
			buf.writeInt(OPT_STRUCT);
	}


	public static class Handler implements IMessageHandler<PacketRequestTerminalData, IMessage>
	{
		@Override
		public IMessage onMessage(PacketRequestTerminalData message, MessageContext ctx) {
			// Always use a construct like this to actually handle your message. This ensures that
			// your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
			// is called on the networking thread so it is not safe to do a lot of things
			// here.
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		/**
		 * Server-side handling of message
		 * @param message
		 * @param ctx
		 */
		private void handle(PacketRequestTerminalData message, MessageContext ctx)
		{
			// This is server-side
			EntityPlayerMP player = ctx.getServerHandler().player;
			MSWorldSavedData wsd = MSWorldSavedData.getData(player.world);
			StructureData structureData = StructureData.getData(player.world);

			if(message.structure && message.sectorRequest != null) {
				// Send both
				PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), message.sectorRequest,
						structureData.getUserStructures(player.getUniqueID())), player);
			}
			else if(message.structure) {
				// send struct
				PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(structureData.getUserStructures(player.getUniqueID())), player);
			}
			else {
				// Send sector
				// Send the packet back to the player's client with the sector requested
				PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), message.sectorRequest), player);
			}
		}
	}
}
