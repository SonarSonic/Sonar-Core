package sonar.core.common.block.properties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface IBlockRotated {

	public EnumFacing getRotation(IBlockState state);
}
