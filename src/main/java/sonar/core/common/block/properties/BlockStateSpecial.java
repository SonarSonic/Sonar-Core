package sonar.core.common.block.properties;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateSpecial<T extends TileEntity, S extends IBlockState> extends BlockStateContainer.StateImplementation implements IBlockStateSpecial<T, S> {

	private final T tile;
	private final BlockPos pos;
	private final S state;

	public BlockStateSpecial(S state, BlockPos pos, T tile) {
		super(state.getBlock(), state.getProperties());
		this.state = state;
		this.tile = tile;
		this.pos = pos;
	}

	@Override
	public T getTileEntity() {
		return tile;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public S getWrappedState() {
		return this.state;
	}

	@Override
	public T getTileEntity(World world) {
		return (T) world.getTileEntity(pos);
	}
}