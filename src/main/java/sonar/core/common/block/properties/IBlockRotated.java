package sonar.core.common.block.properties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface IBlockRotated {

    EnumFacing getRotation(IBlockState state);
}
