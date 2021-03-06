package elec0.megastructures.blocks.powertap;

import elec0.megastructures.Megastructures;
import elec0.megastructures.blocks.BaseBlock;
import elec0.megastructures.blocks.terminal.TerminalTileEntity;
import elec0.megastructures.capabilities.StructureData;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.UUID;

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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!world.isRemote)
		{
			getTE(world, pos).setOwner(placer.getUniqueID());
			getTE(world, pos).initPowerFaces();
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity te = getTE(world, pos);
		if(te == null)
			return false;

		if(world.isRemote)
		{
			// Return true on the client so MC doesn't try to place block
			return true;
		}
		try {
			UUID owner = getTE(world, pos).getOwner();
			EntityPlayerMP pl = world.getMinecraftServer().getPlayerList().getPlayerByUUID(owner);

			player.sendMessage(new TextComponentString(String.format("Owner: %s", pl.getName())));
		}
		catch(NullPointerException ignore) {}

		// Return true on the server so we don't place a block too?
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
