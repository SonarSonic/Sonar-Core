package sonar.core.integration;

import net.minecraft.block.state.IBlockState;

import java.util.List;

public interface IWailaInfo {

    List<String> getWailaInfo(List<String> currenttip, IBlockState state);
}
