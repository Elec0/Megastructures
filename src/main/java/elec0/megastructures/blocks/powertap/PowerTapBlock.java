package elec0.megastructures.blocks.powertap;

import elec0.megastructures.Megastructures;
import elec0.megastructures.blocks.BaseBlock;
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

public class PowerTapBlock extends BaseBlock implements ITileEntityProvider
{

	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Megastructures.MODID, "powertapblock");

	public PowerTapBlock()
	{
		super(Material.ROCK);
		setRegistryName(RESOURCE_LOCATION);
		setUnlocalizedName(Megastructures.MODID + ".powertapblock");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!world.isRemote)
		{
			PowerTapTileEntity te = getTE(world, pos);
			if(te == null)
				return;
			te.setOwner(placer.getUniqueID());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity te = getTE(world, pos);
		if(te == null)
			return false;

		if(world.isRemote)
		{
			//player.openGui(Megastructures.instance, GUIProxy.TERMINAL_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		//MSWorldSavedData wsd = MSWorldSavedData.getData(world);
		//PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy(), wsd.getGalaxy().getSector()), (EntityPlayerMP)player);
		//wsd.save(world);

		// Return true on the client so MC doesn't try to place block
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new PowerTapTileEntity();
	}

	private PowerTapTileEntity getTE(IBlockAccess world, BlockPos pos) {
		try {
			return (PowerTapTileEntity) world.getTileEntity(pos);
		}
		catch(ClassCastException e) {
			e.printStackTrace();
			return null;
		}

	}
}
