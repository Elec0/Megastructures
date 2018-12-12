package elec0.megastructures.network;

import com.jcraft.jogg.Packet;
import elec0.megastructures.capabilities.MSWorldSavedData;
import elec0.megastructures.capabilities.StructureData;
import elec0.megastructures.structures.DysonSphereStructure;
import elec0.megastructures.structures.Structure;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;

/**
 * For server-bound messages relating to structures
 */
public class PacketRequestStructure implements IMessage
{
	private String command;

	public PacketRequestStructure() {}

	public PacketRequestStructure(String action)
	{
		this.command = action;
		System.out.println("PacketRequestStructure");
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		command = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, command);
	}


	public static class Handler implements IMessageHandler<PacketRequestStructure, IMessage>
	{
		@Override
		public IMessage onMessage(PacketRequestStructure message, MessageContext ctx) {
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
		private void handle(PacketRequestStructure message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			StructureData structureData = StructureData.getData(world);

			// DEBUG creating a structure to test
			Structure s = new DysonSphereStructure(ctx.getServerHandler().player.getUniqueID(), "Test1", 0);
			structureData.addStructure(s.getPlayer(), s);
			ctx.getServerHandler().player.sendMessage(new TextComponentString("A new structure was created."));
			// END DEBUG

			// Handle whatever the message was


			// We need to save whenever we finish making a change
			structureData.save(world);

			// Send the packet back to the player's client with the sector requested
		}
	}
}
