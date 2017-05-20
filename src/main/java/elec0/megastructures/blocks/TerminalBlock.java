package elec0.megastructures.blocks;

import elec0.megastructures.Megastructures;
import elec0.megastructures.tileentities.TerminalTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TerminalBlock extends Block implements ITileEntityProvider{

	public TerminalBlock() 
	{
		super(Material.ROCK);
		this.setUnlocalizedName(Megastructures.MODID + ".terminalblock");
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setRegistryName("terminalblock");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TerminalTileEntity.class, Megastructures.MODID + "_terminalblock");
	}

	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TerminalTileEntity();
    }

    private TerminalTileEntity getTE(IBlockAccess world, BlockPos pos) {
        return (TerminalTileEntity) world.getTileEntity(pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
