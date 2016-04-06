package sonar.core.common.block;

import java.util.List;

import sonar.core.common.block.properties.IItemVariant;
import sonar.core.common.block.properties.IMetaRenderer;
import sonar.core.common.block.properties.IMetaVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StableStone extends ConnectedBlock{
	public static final PropertyEnum<Variants> VARIANT = PropertyEnum.<Variants> create("variant", Variants.class);

	public static enum Variants implements IStringSerializable, IMetaVariant {

		Normal, Black, Blue, Brown, Cyan, Green, LightBlue, LightGrey, Lime, Magenta, Orange, Pink, Plain, Purple, Red, Yellow;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}
	}

	public StableStone(Material material, int target) {
		super(material, target);
		// this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, Variants.NORMAL));
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		super.getSubBlocks(item, tab, list);
	}

	public int getMetaFromState(IBlockState state) {
		// return state.getValue(VARIANT).ordinal();
		return 0;
	}

	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta);
		//return this.getDefaultState().withProperty(VARIANT, Variants.values()[meta]);
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, DOWN, UP});
	}
/*
	@Override
	public IItemVariant[] getVariants() {
		return Variants.values();
	}

	@Override
	public IMetaVariant getVariant(int metadata) {
		return Variants.values()[metadata];
	}
	*/
}
