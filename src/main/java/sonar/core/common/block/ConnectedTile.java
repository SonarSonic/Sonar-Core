package sonar.core.common.block;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.blocks.IConnectedBlock;

public abstract class ConnectedTile extends SonarMachineBlock implements IConnectedBlock {

	protected ConnectedTile(int target) {
		super(SonarMaterials.machine, false, true);
		this.target = target;
	}

	public int target = 0;
	public static final PropertySonarFacing NORTH = PropertySonarFacing.create("north", EnumFacing.NORTH);
	public static final PropertySonarFacing EAST = PropertySonarFacing.create("east", EnumFacing.EAST);
	public static final PropertySonarFacing SOUTH = PropertySonarFacing.create("south", EnumFacing.SOUTH);
	public static final PropertySonarFacing WEST = PropertySonarFacing.create("west", EnumFacing.WEST);
	public static final PropertySonarFacing DOWN = PropertySonarFacing.create("down", EnumFacing.DOWN);
	public static final PropertySonarFacing UP = PropertySonarFacing.create("up", EnumFacing.UP);
	public static final ArrayList<PropertySonarFacing> faces = Lists.newArrayList(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	public static final ArrayList<PropertySonarFacing> horizontals = Lists.newArrayList(NORTH, SOUTH, WEST, EAST);

	public static class PropertySonarFacing extends PropertyBool {
		public EnumFacing facing;

		protected PropertySonarFacing(String name, EnumFacing facing) {
			super(name);
			this.facing = facing;
		}

		public static PropertySonarFacing create(String name, EnumFacing facing) {
			return new PropertySonarFacing(name, facing);
		}
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

	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState();
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();

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

}
