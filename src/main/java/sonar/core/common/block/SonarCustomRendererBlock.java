package sonar.core.common.block;

import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.properties.BlockStateSpecial;
import sonar.core.common.block.properties.IBlockStateSpecial;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class SonarCustomRendererBlock<T extends TileEntity> extends SonarMachineBlock {

	protected SonarCustomRendererBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
	}

	public final IBlockStateSpecial<T, ? extends IBlockState> getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return new BlockStateSpecial<>(state, pos, (T) world.getTileEntity(pos));
	}
}
