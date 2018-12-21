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

	public PacketRequestTerminalData()
	{
	}

	public PacketRequestTerminalData(Vector2i sectorRequest)
	{
		this.sectorRequest = sectorRequest;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		sectorRequest = new Vector2i(buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(sectorRequest.getX());
		buf.writeInt(sectorRequest.getY());
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

			// Send the packet back to the player's client with the sector requested
			PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), message.sectorRequest,
					structureData.getUserStructures(player.getUniqueID())), player);
		}
	}
}
