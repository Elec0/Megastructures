package elec0.megastructures.blocks.teleporter;

import elec0.megastructures.Megastructures;
import elec0.megastructures.blocks.BaseBlock;
import elec0.megastructures.blocks.powertap.PowerTapTileEntity;
import elec0.megastructures.proxy.GUIProxy;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TeleporterBlock extends BaseBlock implements ITileEntityProvider
{
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Megastructures.MODID, "teleporterblock");

	public TeleporterBlock()
	{
		super(Material.ROCK);
		setRegistryName(RESOURCE_LOCATION);
		setUnlocalizedName(Megastructures.MODID + ".teleporterblock");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!world.isRemote)
		{
			//getTE(world, pos).setPlacer(placer.getUniqueID());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{

		TileEntity te = getTE(world, pos);
		if(te == null)
			return false;

		if(world.isRemote)
			return true;

		player.openGui(Megastructures.instance, GUIProxy.TELEPORTER_GUI, world, pos.getX(), pos.getY(), pos.getZ());

		//MSWorldSavedData wsd = MSWorldSavedData.getData(world);
		//PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), wsd.getGalaxy().getSector()), (EntityPlayerMP)player);
		//wsd.save(world);

		// Return true on the client so MC doesn't try to place block
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TeleporterTileEntity();
	}

	private TeleporterTileEntity getTE(IBlockAccess world, BlockPos pos)
	{
		try {
			return (TeleporterTileEntity) world.getTileEntity(pos);
		}
		catch(ClassCastException e) {
			e.printStackTrace();
			return null;
		}

	}
}
