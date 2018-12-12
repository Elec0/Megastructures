package elec0.megastructures.network;

import elec0.megastructures.Guis.TerminalGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * For client-bound messages relating to structures
 */
public class PacketSendStructure implements IMessage
{
	public PacketSendStructure() {}

	@Override
	public void fromBytes(ByteBuf buf)
	{

	}

	@Override
	public void toBytes(ByteBuf buf)
	{

	}

	public static class Handler implements IMessageHandler<PacketSendStructure, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSendStructure message, MessageContext ctx) {
			// Always use a construct like this to actually handle your message. This ensures that
			// your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
			// is called on the networking thread so it is not safe to do a lot of things
			// here.
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		/**
		 * Client-side handling of message
		 * @param message
		 * @param ctx
		 */
		private void handle(PacketSendStructure message, MessageContext ctx)
		{
			if(Minecraft.getMinecraft().currentScreen instanceof TerminalGui)
			{
//				TerminalGui termGui = (TerminalGui) Minecraft.getMinecraft().currentScreen;
//				termGui.setGalaxy(message.galaxy);
//				termGui.setViewSector(message.sectorToSend);
//				termGui.packedFinished();
			}
		}
	}
}
