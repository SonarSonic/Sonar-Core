package sonar.core.integration;

import java.util.List;

import net.minecraft.block.state.IBlockState;

public interface IWailaInfo {

    List<String> getWailaInfo(List<String> currenttip, IBlockState state);
}
