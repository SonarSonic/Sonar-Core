package sonar.core.integration.multipart;

import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMachineBlock;

public abstract class BlockSonarMultipart extends BlockContainer implements IMultipart {

	protected BlockSonarMultipart(Material materialIn) {
		super(materialIn);
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public boolean canPlacePartAt(World world, BlockPos pos) {
		return true;// getBlock().canPlaceBlockAt(world, pos); FIXME?
	}

	public boolean canPlacePartOnSide(World world, BlockPos pos, EnumFacing side, IPartSlot slot) {
		return true;// getBlock().canPlaceBlockOnSide(world, pos, side); FIXME?
	}
}
