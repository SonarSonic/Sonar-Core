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

	// Material connectMaterial;

	public SonarFence(Material connectMaterial) {
		super(connectMaterial, MapColor.GRAY);
        setHardness(2.0f);
        setResistance(10.0f);
    }

    public SonarFence(Material connectMaterial, float hardness, float resistance) {
        super(connectMaterial, MapColor.GRAY);
        setHardness(hardness);
        setResistance(resistance);
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
        return block != Blocks.BARRIER && (block instanceof BlockFence && state.getMaterial() == this.blockMaterial || block instanceof BlockFenceGate || block.getMaterial(state).isOpaque() && block.isFullCube(state) && block.getMaterial(state) != Material.GOURD);
	}
}
