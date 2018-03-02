package sonar.core.client.gui;


import mcmultipart.multipart.Multipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class MultipartStateOverride {
	public Multipart part;

	public MultipartStateOverride(Multipart part) {
		this.part = part;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getActualState(world, pos);
	}
}