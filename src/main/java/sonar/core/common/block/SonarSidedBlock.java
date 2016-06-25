package sonar.core.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public abstract class SonarSidedBlock extends SonarMachineBlock {

	public static final PropertyEnum<MachineSideConfig> NORTH = PropertyEnum.<MachineSideConfig> create("north", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.INPUT_ANIMATE, MachineSideConfig.OUTPUT, MachineSideConfig.OUTPUT_ANIMATE);
	public static final PropertyEnum<MachineSideConfig> NORTH_NO_ANIMATE = PropertyEnum.<MachineSideConfig> create("north", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);
	public static final PropertyEnum<MachineSideConfig> EAST = PropertyEnum.<MachineSideConfig> create("east", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);
	public static final PropertyEnum<MachineSideConfig> SOUTH = PropertyEnum.<MachineSideConfig> create("south", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);
	public static final PropertyEnum<MachineSideConfig> WEST = PropertyEnum.<MachineSideConfig> create("west", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);
	public static final PropertyEnum<MachineSideConfig> UP = PropertyEnum.<MachineSideConfig> create("up", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);
	public static final PropertyEnum<MachineSideConfig> DOWN = PropertyEnum.<MachineSideConfig> create("down", MachineSideConfig.class, MachineSideConfig.INPUT, MachineSideConfig.OUTPUT);

	protected SonarSidedBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(hasAnimatedFront() ? NORTH : NORTH_NO_ANIMATE, MachineSideConfig.INPUT).withProperty(EAST, MachineSideConfig.INPUT).withProperty(SOUTH, MachineSideConfig.INPUT).withProperty(WEST, MachineSideConfig.INPUT).withProperty(UP, MachineSideConfig.INPUT).withProperty(DOWN, MachineSideConfig.INPUT));
	}
	
	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		TileEntity target = w.getTileEntity(pos);
		if (target != null) {
			if (target instanceof IMachineSides) {
				MachineSides sides = ((IMachineSides) target).getSideConfigs();
				EnumFacing front = state.getValue(FACING);
				MachineSideConfig frontConfig = sides.getSideConfig(SonarHelper.offsetFacing(EnumFacing.NORTH, front));
				return state.withProperty(hasAnimatedFront() ? NORTH : NORTH_NO_ANIMATE, (hasAnimatedFront() && isAnimated(state,w,pos)) ? frontConfig.getAnimated() : frontConfig).withProperty(SOUTH, sides.getSideConfig(SonarHelper.offsetFacing(EnumFacing.SOUTH, front))).withProperty(WEST, sides.getSideConfig(SonarHelper.offsetFacing(EnumFacing.WEST, front))).withProperty(EAST, sides.getSideConfig(SonarHelper.offsetFacing(EnumFacing.EAST, front))).withProperty(UP, sides.getSideConfig(EnumFacing.UP)).withProperty(DOWN, sides.getSideConfig(EnumFacing.DOWN));
			}
		}
		return state;
	}
	
	public boolean hasAnimatedFront() {
		return true;
	}

	public boolean isAnimated(IBlockState state, IBlockAccess w, BlockPos pos) {
		return true;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, hasAnimatedFront() ? NORTH : NORTH_NO_ANIMATE, EAST, SOUTH, WEST, UP, DOWN });
	}
}
