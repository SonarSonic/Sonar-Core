package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.blocks.IConnectedBlock;
import sonar.core.api.blocks.IStableBlock;

import javax.annotation.Nonnull;

public class ConnectedBlock extends Block implements IConnectedBlock, IStableBlock {

    public int target;
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
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		IBlockState block = world.getBlockState(new BlockPos(x + side.getFrontOffsetX(), y + side.getFrontOffsetY(), z + side.getFrontOffsetZ()));
		int meta = state.getBlock().getMetaFromState(state);
        return type(state, block, meta, block.getBlock().getMetaFromState(block));
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
                    for (int aConnections1 : connections1) {
                        for (int aConnections2 : connections2) {
                            if (aConnections1 == aConnections2)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

    @Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

    @Nonnull
    @Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess w, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return state.withProperty(NORTH, checkBlockInDirection(w, x, y, z, EnumFacing.NORTH)).withProperty(SOUTH, checkBlockInDirection(w, x, y, z, EnumFacing.SOUTH)).withProperty(WEST, checkBlockInDirection(w, x, y, z, EnumFacing.WEST)).withProperty(EAST, checkBlockInDirection(w, x, y, z, EnumFacing.EAST)).withProperty(UP, checkBlockInDirection(w, x, y, z, EnumFacing.UP)).withProperty(DOWN, checkBlockInDirection(w, x, y, z, EnumFacing.DOWN));
	}

    @Nonnull
    @Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, DOWN, UP);
	}

	@Override
	public int[] getConnections() {
		return new int[] { target };
	}

	public static class Glass extends ConnectedBlock {

		public Glass(Material material, int target) {
			super(material, target);
		}

        @Nonnull
        @Override
		public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.TRANSLUCENT;
		}

		@Nonnull
        @Override
		public EnumBlockRenderType getRenderType(IBlockState state) {
			return EnumBlockRenderType.MODEL;
		}

        @Override
		public boolean isFullCube(IBlockState state) {
			return false;
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}

        @Override
		@SideOnly(Side.CLIENT)
		public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
			IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
			Block block = iblockstate.getBlock();
            return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
	}
}
