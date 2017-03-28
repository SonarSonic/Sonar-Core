package sonar.core.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.blocks.IConnectedBlock;
import sonar.core.api.blocks.IStableBlock;

public class ConnectedBlock extends Block implements IConnectedBlock, IStableBlock {

	public int target = 0;
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool UP = PropertyBool.create("up");

	public ConnectedBlock(Material material, int target) {
		super(material);
		this.target = target;
		this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(UP, false).withProperty(DOWN, false));
	}

	public boolean checkBlockInDirection(IBlockAccess world, int x, int y, int z, EnumFacing side) {
		EnumFacing dir = side;
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		IBlockState block = world.getBlockState(new BlockPos(x + dir.getFrontOffsetX(), y + dir.getFrontOffsetY(), z + dir.getFrontOffsetZ()));
		int meta = state.getBlock().getMetaFromState(state);
		if (block != null) {
			if (type(state, block, meta, block.getBlock().getMetaFromState(block))) {
				return true;
			}
		}
		return false;
	}

	public static boolean type(IBlockState state1, IBlockState state2, int m1, int m2) {
		Block block1 = state1.getBlock();
		Block block2 = state2.getBlock();
		if (!(block1 instanceof IConnectedBlock) || !(block2 instanceof IConnectedBlock) || m1 == m2) {
			if (block1 instanceof IConnectedBlock) {
				IConnectedBlock c1 = (IConnectedBlock) block1;
				int[] connections1 = ((IConnectedBlock) block1).getConnections();

				if (block2 instanceof IConnectedBlock) {
					int[] connections2 = ((IConnectedBlock) block2).getConnections();
					for (int i = 0; i < connections1.length; i++) {
						for (int i2 = 0; i2 < connections2.length; i2++) {
							if (connections1[i] == connections2[i2])
								return true;
						}
					}
				}
			}
		}
		return false;

	}

	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return state.withProperty(NORTH, checkBlockInDirection(w, x, y, z, EnumFacing.NORTH)).withProperty(SOUTH, checkBlockInDirection(w, x, y, z, EnumFacing.SOUTH)).withProperty(WEST, checkBlockInDirection(w, x, y, z, EnumFacing.WEST)).withProperty(EAST, checkBlockInDirection(w, x, y, z, EnumFacing.EAST)).withProperty(UP, checkBlockInDirection(w, x, y, z, EnumFacing.UP)).withProperty(DOWN, checkBlockInDirection(w, x, y, z, EnumFacing.DOWN));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, DOWN, UP });
	}

	@Override
	public int[] getConnections() {
		return new int[] { target };
	}

	public static class Glass extends ConnectedBlock {

		public Glass(Material material, int target) {
			super(material, target);
		}

		public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.TRANSLUCENT;
		}

		@Override
		public EnumBlockRenderType getRenderType(IBlockState state) {
			return EnumBlockRenderType.MODEL;
		}

		public boolean isFullCube(IBlockState state) {
			return false;
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}

		@SideOnly(Side.CLIENT)
		public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
			IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
			Block block = iblockstate.getBlock();
			return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
	}

}
