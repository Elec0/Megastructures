package elec0.megastructures.network;

import elec0.simplypowers.capabilities.IPowerData;
import elec0.simplypowers.capabilities.PowerDataProvider;
import elec0.simplypowers.powers.IPower;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendKey implements IMessage
{
	public int powerNum, keyCode;
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// Retrieve info from message
		powerNum = buf.readInt();
		keyCode = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(powerNum);
		buf.writeInt(keyCode);
	}
	
	public PacketSendKey()
	{
		
	}
	public PacketSendKey(int power, int key)
	{
		powerNum = power - 1;
		keyCode = key;
	}
	
	public static class Handler implements IMessageHandler<PacketSendKey, IMessage> 
	{
        @Override
        public IMessage onMessage(PacketSendKey message, MessageContext ctx) {
            // Always use a construct like this to actually handle your message. This ensures that
            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
            // here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendKey message, MessageContext ctx) 
        {
            // This code is run on the server side. So you can do server-side calculations here
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.worldObj;
            
            IPowerData powerData = playerEntity.getCapability(PowerDataProvider.POWER_CAP, null);
            IPower[] powers = powerData.getPowers();
            if(powers[0] != null && powers[1] != null)
            {
            	// Toggle power activate status
            	powers[message.powerNum].activate(playerEntity, message.keyCode); 
            }
            
            playerEntity.addChatMessage(new TextComponentString(TextFormatting.GREEN + "Power " + (message.powerNum+1) + " " 
            		+ (powers[message.powerNum].getActive() == 0 ? "off" : "on") + "."));

        }
    }
}
