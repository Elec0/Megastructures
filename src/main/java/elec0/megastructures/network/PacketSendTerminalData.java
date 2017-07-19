package elec0.megastructures.network;

import elec0.megastructures.universe.Galaxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class PacketSendTerminalData implements IMessage
{
	private Galaxy galaxy;

	public PacketSendTerminalData()
	{

	}

	public PacketSendTerminalData(Galaxy galaxy)
	{
		this.galaxy = galaxy;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		// Retrieve info from message
		galaxy = new Galaxy(buf.readLong());
		galaxy.setName(buf.readCharSequence(buf.readInt(), StandardCharsets.ISO_8859_1).toString());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		// Send info to message
		buf.writeLong(galaxy.getSeed());
		buf.writeInt(galaxy.getName().length());
		buf.writeCharSequence(galaxy.getName(), StandardCharsets.ISO_8859_1);
	}
	
	public static class Handler implements IMessageHandler<PacketSendTerminalData, IMessage>
	{
        @Override
        public IMessage onMessage(PacketSendTerminalData message, MessageContext ctx) {
            // Always use a construct like this to actually handle your message. This ensures that
            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
            // here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendTerminalData message, MessageContext ctx)
        {
			// This is client-side

			// System.out.println("Player " + Minecraft.getMinecraft().player.getName());
            System.out.println("Galaxy: " + message.galaxy.getName() + ", " + message.galaxy.getSeed());

		}
    }
}
