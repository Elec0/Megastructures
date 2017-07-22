package elec0.megastructures.blocks;

import elec0.megastructures.Megastructures;
import elec0.megastructures.capabilities.MSWorldSavedData;
import elec0.megastructures.network.PacketHandler;
import elec0.megastructures.network.PacketSendTerminalData;
import elec0.megastructures.proxy.GUIProxy;
import elec0.megastructures.tileentities.TerminalTileEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerminalBlock extends BaseBlock implements ITileEntityProvider
{

	public TerminalBlock() 
	{
		super(Material.ROCK);
		setUnlocalizedName(Megastructures.MODID + ".terminalblock");
		setCreativeTab(CreativeTabs.DECORATIONS);
		setRegistryName("terminalblock");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!world.isRemote)
		{
			((TerminalTileEntity)getTE(world, pos)).setPlacer(placer.getUniqueID());
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
		PacketHandler.INSTANCE.sendTo(new PacketSendTerminalData(wsd.getGalaxy()), (EntityPlayerMP)player);

		wsd.save(world);

		// Return true on the client so MC doesn't try to place block
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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
