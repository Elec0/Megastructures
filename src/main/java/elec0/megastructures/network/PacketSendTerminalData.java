package elec0.megastructures.network;

import elec0.megastructures.Guis.TerminalGui;
import elec0.megastructures.general.Vector2l;
import elec0.megastructures.universe.Galaxy;
import elec0.megastructures.universe.SolarSystem;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

		int SSlen = buf.readInt();
		for(int i = 0; i < SSlen; ++i)
		{
			SolarSystem s = new SolarSystem(buf.readLong());
			s.setName(buf.readCharSequence(buf.readInt(), StandardCharsets.ISO_8859_1).toString());
			s.setPosition(new Vector2l(buf.readLong(), buf.readLong()));
			galaxy.addSolarSystem(s);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		// Send info to message
		buf.writeLong(galaxy.getSeed());
		buf.writeInt(galaxy.getName().length());
		buf.writeCharSequence(galaxy.getName(), StandardCharsets.ISO_8859_1);

		// Number of solar systems in galaxy
		buf.writeInt(galaxy.getSolarSystems().size());
		for(int i = 0; i < galaxy.getSolarSystems().size(); ++i)
		{
			SolarSystem s = galaxy.getSolarSystems().get(i);
			buf.writeLong(s.getSeed());
			buf.writeInt(s.getName().length()); 								// Write name 1/2
			buf.writeCharSequence(s.getName(), StandardCharsets.ISO_8859_1); 	// 2/2
			buf.writeLong(s.getPosition().getX());
			buf.writeLong(s.getPosition().getY());
		}
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

            if(Minecraft.getMinecraft().currentScreen instanceof TerminalGui)
			{
				TerminalGui termGui = (TerminalGui) Minecraft.getMinecraft().currentScreen;
				termGui.setGalaxy(message.galaxy);
			}
			//player.openGui(Megastructures.instance, GUIProxy.TERMINAL_GUI, Minecraft.getMinecraft().world, (int)player.posX, (int)player.posY, (int)player.posZ);


		}
    }
}
