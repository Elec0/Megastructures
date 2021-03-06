package elec0.megastructures.network;

import elec0.megastructures.Guis.TerminalGui;
import elec0.megastructures.general.Vector2i;
import elec0.megastructures.general.Vector2l;
import elec0.megastructures.structures.Structure;
import elec0.megastructures.universe.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PacketSendTerminalData implements IMessage
{
	private Galaxy galaxy;
	private Vector2i sectorToSend; // If this is null then we're only sending structures
	private List<Structure> structures;

	public PacketSendTerminalData()
	{

	}

	/**
	 * Sending new sector information
	 * @param galaxy
	 * @param sector
	 */
	public PacketSendTerminalData(Galaxy galaxy, Vector2i sector)
	{
		this.galaxy = galaxy;
		this.sectorToSend = sector;
		this.structures = null;
	}

	/**
	 * Sending structure information like energy, etc
	 * Is called very often
	 * @param structures
	 */
	public PacketSendTerminalData(List<Structure> structures) {
		this.structures = structures;
		this.sectorToSend = null;
	}

	/**
	 * Get sector and structure information
	 * @param galaxy
	 * @param sector
	 * @param structures
	 */
	public PacketSendTerminalData(Galaxy galaxy, Vector2i sector, List<Structure> structures) {
		this.galaxy = galaxy;
		this.sectorToSend = sector;
		this.structures = structures;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int opt = buf.readInt();

		// Sometimes we have to send a -1, if so just fail silently
		if(opt == -1)
			return;

		switch(opt) {
			case PacketRequestTerminalData.OPT_SECTOR:
				readSector(buf);
				break;

			case PacketRequestTerminalData.OPT_STRUCT:
				readStructureList(buf);
				break;

			case PacketRequestTerminalData.OPT_BOTH:
				readSector(buf);
				readStructureList(buf);
				break;
			default:
				System.err.println(String.format("PacketSendTerminalData.fromBytes(): opt %s is not a valid option", opt));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{

		int opt = -1;

		// This is kinda stupid but whatever.
		// We write an int that's either sector, structure, or both
		// Since opt is -1 by def., in structures if it hasn't been set then just send structures, otherwise send all
		if(sectorToSend != null)
			opt = PacketRequestTerminalData.OPT_SECTOR;

		if(structures != null)
			if(opt != -1)
				opt = PacketRequestTerminalData.OPT_BOTH;
			else
				opt = PacketRequestTerminalData.OPT_STRUCT;

		buf.writeInt(opt);

		// This bothers me but I can't think of a better way to do it right now
		switch(opt) {
			case PacketRequestTerminalData.OPT_SECTOR:
				writeSector(buf);
				break;

			case PacketRequestTerminalData.OPT_STRUCT:
				writeStructureList(buf);
				break;

			case PacketRequestTerminalData.OPT_BOTH:
				writeSector(buf);
				writeStructureList(buf);
				break;

		}
	}

	/**
	 * Read the sector information from the network
	 * @param buf
	 */
	private void readSector(ByteBuf buf) {
		// Retrieve info from message
		// Read the galaxy
		galaxy = new Galaxy(buf.readLong());
		readLocation(buf, galaxy);

		sectorToSend = new Vector2i(buf.readInt(), buf.readInt());

		// Read the solar systems
		int SSlen = buf.readInt();
		for(int i = 0; i < SSlen; ++i)
		{
			SolarSystem s = new SolarSystem(buf.readLong());
			readLocation(buf, s); // General stuff

			// Read the celestials
			int Clen = buf.readInt();
			for(int j = 0; j < Clen; ++j)
			{
				int CType = buf.readInt();

				Celestial cel;

				// Determine what type of celestial it is and initialize the object accordingly
				if(CType == Celestial.PLANET)
					cel = new Planet(buf.readLong());
				else if(CType == Celestial.STAR)
					cel = new Star(buf.readLong());
				else
					cel = new Celestial(buf.readLong());

				readLocation(buf, cel); // General stuff
				cel.setMass(buf.readDouble()); // Mass
				cel.setRadius(buf.readDouble()); // Radius

				// If the celestial is a planet
				if(cel instanceof Planet)
				{
					// Read satellites
					int SatNum = buf.readInt();
					if(SatNum > 0)
					{
						// Read all the satellites coming in for this planet
						for(int k = 0; k < SatNum; ++k)
						{
							Satellite sat = new Satellite(buf.readLong());
							readLocation(buf, sat);
							sat.setMass(buf.readDouble()); // Mass
							sat.setRadius(buf.readDouble()); // Radius

							((Planet) cel).addSatellite(sat);
						}
					}
				}
				else if(cel instanceof Star) // It's a star
				{

				}

				// Finally, add the celestial to the solar system
				s.addCelestial(cel);
			}

			galaxy.addSolarSystem(s);
		}
	}


	/**
	 * Send the sector information via network
	 * @param buf
	 */
	private void writeSector(ByteBuf buf) {
		// Send info to message
		sendLocation(buf, galaxy);

		// Sector to view
		buf.writeInt(sectorToSend.getX());
		buf.writeInt(sectorToSend.getY());

		// Number of solar systems in sector
		List<SolarSystem> sector = galaxy.getSectorList(sectorToSend);
		if(sector == null)
		{
			// If the sector hasn't been generated yet, do that
			galaxy.generate(sectorToSend);
			sector = galaxy.getSectorList(sectorToSend);
		}

		// Send the solar systems
		// Need to know how many to read, on the client side
		buf.writeInt(sector.size());
		for(int i = 0; i < sector.size(); ++i)
		{
			SolarSystem s = sector.get(i);
			sendLocation(buf, s);

			// Send the celestials
			buf.writeInt(s.getCelestials().size());
			for(int j = 0; j < s.getCelestials().size(); ++j)
			{
				Celestial c = s.getCelestials().get(j);
				// Type of celestial
				if(c instanceof Planet)
					buf.writeInt(Celestial.PLANET);
				else if(c instanceof Star)
					buf.writeInt(Celestial.STAR);

				sendLocation(buf, c);

				buf.writeDouble(c.getMass()); // Mass
				buf.writeDouble(c.getRadius()); // Radius

				// Satellites, if this is a planet and they exist
				if(c instanceof Planet)
				{
					Planet p = (Planet)c;

					// Send the number even if it's 0 so the client knows what to do
					buf.writeInt(p.getSatellites().size());

					// If there are satellites around the planet
					if(p.getSatellites().size() > 0)
					{
						// Loop through said satellites
						for(int k = 0; k < p.getSatellites().size(); ++k)
						{
							Satellite sat = p.getSatellites().get(k);
							sendLocation(buf, sat);
							buf.writeDouble(sat.getMass()); // Mass
							buf.writeDouble(sat.getRadius()); // Radius
						}
					}
				}
				else if(c instanceof Star)
				{
					// TODO: After Star is fully defined
				}
			}
		}
	}


	/**
	 * Get the list of structures from the byte buffer
	 * @param buf
	 */
	private void readStructureList(ByteBuf buf) {
		structures = new ArrayList<>();
		int structSize = buf.readInt();

		for(int i = 0; i < structSize; ++i) {
			try {
				// Read the JSON from the server, then convert it into an nbt tag
				NBTTagCompound curTag = JsonToNBT.getTagFromJson(ByteBufUtils.readUTF8String(buf));

				// Then use the code we've already written for loading from nbt to just load the object
				Structure cur = new Structure();
				cur.deserializeNBT(curTag);
				structures.add(cur);
			}
			catch(NBTException e) {
				System.out.println("Error in reading the NBT from the server.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Serialize and send the structure objects to the client
	 * @param buf
	 */
	private void writeStructureList(ByteBuf buf) {
		if(structures == null) {
			buf.writeInt(0);
			return;
		}
		buf.writeInt(structures.size());

		for(Structure s : structures) {
			ByteBufUtils.writeUTF8String(buf, s.serializeNBT().toString());
		}
	}

	/**
	 * Send data common to all Locations
	 * @param buf
	 * @param loc
	 */
	private void sendLocation(ByteBuf buf, Location loc)
	{
		// Seed
		buf.writeLong(loc.getSeed());
		// Name
		ByteBufUtils.writeUTF8String(buf, loc.getName());
		// Position
		buf.writeLong(loc.getPosition().getX());
		buf.writeLong(loc.getPosition().getY());
	}

	/**
	 * Read common elements from the buf into the Location object
	 * @param buf
	 * @param loc
	 */
	private void readLocation(ByteBuf buf, Location loc)
	{
		loc.setName(ByteBufUtils.readUTF8String(buf));
		loc.setPosition(new Vector2l(buf.readLong(), buf.readLong()));
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

				int opt = -1;
				// Only read the stuff that we've actually sent
				if(message.sectorToSend != null) {
					termGui.setGalaxy(message.galaxy);
					termGui.setViewSector(message.sectorToSend);
					opt = PacketRequestTerminalData.OPT_SECTOR;
				}
				if(message.structures != null) {
					termGui.setUserStructures(message.structures);
					if(opt != -1)
						opt = PacketRequestTerminalData.OPT_BOTH;
					else
						opt = PacketRequestTerminalData.OPT_STRUCT;
				}

				termGui.packetFinished(opt);
			}
		}
    }
}
