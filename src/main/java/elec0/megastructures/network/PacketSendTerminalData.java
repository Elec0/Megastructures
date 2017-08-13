package elec0.megastructures.network;

import elec0.megastructures.Guis.TerminalGui;
import elec0.megastructures.general.Vector2i;
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
import java.util.List;

public class PacketSendTerminalData implements IMessage
{
	private Galaxy galaxy;
	private Vector2i sectorToSend;

	public PacketSendTerminalData()
	{

	}

	public PacketSendTerminalData(Galaxy galaxy, Vector2i sector)
	{
		this.galaxy = galaxy;
		this.sectorToSend = sector;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		// Retrieve info from message
		galaxy = new Galaxy(buf.readLong());
		galaxy.setName(buf.readCharSequence(buf.readInt(), StandardCharsets.ISO_8859_1).toString());
		galaxy.setPosition(new Vector2l(buf.readLong(), buf.readLong()));
		sectorToSend = new Vector2i(buf.readInt(), buf.readInt());

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
		buf.writeInt(galaxy.getName().length()); // Galaxy name 1/2
		buf.writeCharSequence(galaxy.getName(), StandardCharsets.ISO_8859_1); // Name 2/2
		buf.writeLong(galaxy.getPosition().getX()); // Galaxy position
		buf.writeLong(galaxy.getPosition().getY());

		buf.writeInt(sectorToSend.getX());
		buf.writeInt(sectorToSend.getY());

		// Number of solar systems in sector
		List<SolarSystem> sector = galaxy.getSectorList(sectorToSend);
		if(sector == null)
		{
			System.out.println("SendData: generate sector " + sectorToSend);
			// If the sector hasn't been generated yet, do that
			galaxy.generate(sectorToSend);
			sector = galaxy.getSectorList(sectorToSend);
		}

		buf.writeInt(sector.size());
		for(int i = 0; i < sector.size(); ++i)
		{
			SolarSystem s = sector.get(i);
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
				termGui.setViewSector(message.sectorToSend);
				termGui.packedFinished();
			}
		}
    }
}
