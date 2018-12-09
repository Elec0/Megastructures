package elec0.megastructures.blocks.terminal;

import elec0.megastructures.Megastructures;
import elec0.megastructures.blocks.BaseBlock;
import elec0.megastructures.capabilities.MSWorldSavedData;
import elec0.megastructures.network.PacketHandler;
import elec0.megastructures.network.PacketSendTerminalData;
import elec0.megastructures.proxy.GUIProxy;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TerminalBlock extends BaseBlock implements ITileEntityProvider
{

	public static final ResourceLocation TERMINAL_BLOCK = new ResourceLocation(Megastructures.MODID, "terminalblock");

	public TerminalBlock()
	{
		super(Material.ROCK);
		setRegistryName(TERMINAL_BLOCK);
		setUnlocalizedName(Megastructures.MODID + ".terminalblock");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!world.isRemote)
		{
			getTE(world, pos).setPlacer(placer.getUniqueID());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{

		TileEntity te = getTE(world, pos);
		if(!(te instanceof TerminalTileEntity))
			return false;

		if(world.isRemote)
		{
			player.openGui(Megastructures.instance, GUIProxy.TERMINAL_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		//player.sendMessage(new TextComponentString("Placer: " + ((TerminalTileEntity)getTE(world,pos)).getPlacer().toString()));

		MSWorldSavedData wsd = MSWorldSavedData.getData(world);
		PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), wsd.getGalaxy().getSector()), (EntityPlayerMP)player);

		wsd.save(world);

		// Return true on the client so MC doesn't try to place block
		return true;
	}

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TerminalTileEntity();
    }

    private TerminalTileEntity getTE(IBlockAccess world, BlockPos pos)
	{
        return (TerminalTileEntity) world.getTileEntity(pos);
    }


}
