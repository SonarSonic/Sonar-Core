package sonar.core.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class StableStone extends ConnectedBlock {

	public StableStone(Material material, int target) {
		super(material, target);
		// TODO Auto-generated constructor stub
	}

	public int getMetaFromState(IBlockState state) {
		return 0;
	}
}
