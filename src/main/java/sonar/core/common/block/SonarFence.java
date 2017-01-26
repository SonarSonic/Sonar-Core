package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class SonarFence extends BlockFence {

	Material connectMaterial;
	
	public SonarFence(Material connectMaterial) {
		super(SonarMaterials.fence, MapColor.GRAY);
		this.connectMaterial=connectMaterial;
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		return block == Blocks.BARRIER ? false : ((!(block instanceof BlockFence) || state.getMaterial() != connectMaterial) && !(block instanceof BlockFenceGate || block instanceof SonarGate) ? (state.getMaterial().isOpaque() && state.isFullCube() ? state.getMaterial() != Material.GOURD : false) : true);
	}
}
