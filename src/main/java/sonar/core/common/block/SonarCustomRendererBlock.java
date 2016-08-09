package sonar.core.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sonar.core.common.block.properties.BlockStateSpecial;
import sonar.core.common.block.properties.IBlockStateSpecial;

public abstract class SonarCustomRendererBlock<T extends TileEntity> extends SonarMachineBlock {

	protected SonarCustomRendererBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
	}

	public final IBlockStateSpecial<T, ? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return new BlockStateSpecial<>(state, pos, (T) world.getTileEntity(pos));
	}
}
