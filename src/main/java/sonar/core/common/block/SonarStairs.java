package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class SonarStairs extends BlockStairs {

	public SonarStairs(Block block) {
		super(block.getStateFromMeta(0));
		this.setLightOpacity(0);    
		this.useNeighborBrightness = true;
	}

}
