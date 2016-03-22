package sonar.core.common.block;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.utils.ISonarSides;
import sonar.core.utils.ISpecialTooltip;
import sonar.core.utils.MachineSide;
import sonar.core.utils.helpers.SonarHelper;

public abstract class SonarSidedBlock extends SonarMachineBlock {

	public static final PropertyEnum<MachineSide> NORTH = PropertyEnum.<MachineSide> create("north", MachineSide.class, MachineSide.INPUT, MachineSide.INPUT_ANIMATE, MachineSide.OUTPUT, MachineSide.OUTPUT_ANIMATE);
	public static final PropertyEnum<MachineSide> NORTH_NO_ANIMATE = PropertyEnum.<MachineSide> create("north", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);
	public static final PropertyEnum<MachineSide> EAST = PropertyEnum.<MachineSide> create("east", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);
	public static final PropertyEnum<MachineSide> SOUTH = PropertyEnum.<MachineSide> create("south", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);
	public static final PropertyEnum<MachineSide> WEST = PropertyEnum.<MachineSide> create("west", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);
	public static final PropertyEnum<MachineSide> UP = PropertyEnum.<MachineSide> create("up", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);
	public static final PropertyEnum<MachineSide> DOWN = PropertyEnum.<MachineSide> create("down", MachineSide.class, MachineSide.INPUT, MachineSide.OUTPUT);

	protected SonarSidedBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(hasAnimatedFront() ? NORTH : NORTH_NO_ANIMATE, MachineSide.INPUT).withProperty(EAST, MachineSide.INPUT).withProperty(SOUTH, MachineSide.INPUT).withProperty(WEST, MachineSide.INPUT).withProperty(UP, MachineSide.INPUT).withProperty(DOWN, MachineSide.INPUT));
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		TileEntity target = w.getTileEntity(pos);
		if (target != null) {
			if (target instanceof ISonarSides) {
				ISonarSides sides = (ISonarSides) target;
				EnumFacing front = state.getValue(FACING);
				MachineSide frontConfig = sides.getSideConfig(SonarHelper.offsetFacing(EnumFacing.NORTH, front));
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

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { FACING, hasAnimatedFront() ? NORTH : NORTH_NO_ANIMATE, EAST, SOUTH, WEST, UP, DOWN });
	}
}
