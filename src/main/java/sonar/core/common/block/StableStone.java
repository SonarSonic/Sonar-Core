package sonar.core.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.common.block.properties.IMetaVariant;

public class StableStone extends ConnectedBlock {
	public static final PropertyEnum<Variants> VARIANT = PropertyEnum.<Variants>create("variant", Variants.class);

	public static enum Variants implements IStringSerializable, IMetaVariant {

		Normal(7), Black(0), Blue(4), Brown(3), Cyan(6), Green(2), LightBlue(12), LightGrey(8), Lime(10), Magenta(13), Orange(14), Pink(9), Plain(15), Purple(5), Red(1), Yellow(11);

		// Black, Red, Green, Brown, Blue, Purple, Cyan, Normal, LightGrey, Pink, Lime, Yellow, LightBlue, Magenta, Orange, Plain;

		public int dyeMeta;

		Variants(int dyeMeta) {
			this.dyeMeta = dyeMeta;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		public int getDyeMeta() {
			return dyeMeta;
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
		// return this.getDefaultState().withProperty(VARIANT, Variants.values()[meta]);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, DOWN, UP });
	}
	/* @Override public IItemVariant[] getVariants() { return Variants.values(); }
	 * @Override public IMetaVariant getVariant(int metadata) { return Variants.values()[metadata]; } */
}
