package sonar.core.common.block.properties;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
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