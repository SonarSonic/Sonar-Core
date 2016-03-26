package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block {

	public BlockBase(Material material, float hardness, float resistance) {
		super(material);
		this.setHardness(hardness);
		this.setResistance(resistance);
	}

	public BlockBase(float hardness, float resistance) {
		this(Material.rock, hardness, resistance);
	}

	public BlockBase() {
		this(2.0f, 10.0f);
	}
}