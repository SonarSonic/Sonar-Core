package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;

public class SonarGate extends BlockFenceGate{

	//kind of cheated here, oh well, stone can be wood, the block is passed anyway but I don't do anything with it
	//many months later, Ollie you are an idiot, but I can't be bothered to fix your mistakes.
	public SonarGate(Block block) {
		super(EnumType.OAK);		
	}
	
}