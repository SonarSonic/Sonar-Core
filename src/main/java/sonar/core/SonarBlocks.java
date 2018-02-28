package sonar.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import sonar.core.common.block.BlockBase;
import sonar.core.common.block.ConnectedBlock;
import sonar.core.common.block.SonarFence;
import sonar.core.common.block.SonarGate;
import sonar.core.common.block.SonarStairs;
import sonar.core.common.block.StableStone;
import sonar.core.common.block.StableStone.Variants;

public class SonarBlocks extends SonarCore {

   	public static void registerSlab(String name, Item slab, BlockSlab singleSlab, BlockSlab doubleSlab) {
		//reinforcedStoneBrickSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneBrickSlab");
		//reinforcedStoneBrickSlab_half = registerBlock("ReinforcedStoneBrickSlab", new SonarSlab.Half(Material.ROCK));
	}

	public static void registerBlocks() {
		//white_dev_block = registerBlock("WhiteDevBlock", new BlockBase(Material.ROCK, 0.1f, 650.0f));
		//black_dev_block = registerBlock("BlackDevBlock", new BlockBase(Material.ROCK, 0.1f, 650.0f));
		// common blocks
		reinforcedStoneBlock = SonarRegister.addBlock(modid, tab, "ReinforcedStoneBlock", new BlockBase(Material.ROCK, 2.0f, 50.0f));
		reinforcedStoneStairs = SonarRegister.addBlock(modid, tab, "ReinforcedStoneStairs", new SonarStairs(reinforcedStoneBlock));
		reinforcedStoneFence = SonarRegister.addBlock(modid, tab, "ReinforcedStoneFence", new SonarFence(Material.ROCK));
		reinforcedStoneGate = SonarRegister.addBlock(modid, tab, "ReinforcedStoneGate", new SonarGate(reinforcedStoneBlock));
		//reinforcedStoneSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneSlab");
		//reinforcedStoneSlab_half = registerBlock("ReinforcedStoneSlab", new SonarSlab.Half(Material.ROCK));

		reinforcedStoneBrick = SonarRegister.addBlock(modid, tab, "ReinforcedStoneBrick", new BlockBase(Material.ROCK, 2.0f, 50.0f));
		reinforcedStoneBrickStairs = SonarRegister.addBlock(modid, tab, "ReinforcedStoneBrickStairs", new SonarStairs(reinforcedStoneBrick));
		reinforcedStoneBrickFence = SonarRegister.addBlock(modid, tab, "ReinforcedStoneBrickFence", new SonarFence(Material.ROCK));
		reinforcedStoneBrickGate = SonarRegister.addBlock(modid, tab, "ReinforcedStoneBrickGate", new SonarGate(reinforcedStoneBrick));
		//reinforcedStoneBrickSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneBrickSlab");
		//reinforcedStoneBrickSlab_half = registerBlock("ReinforcedStoneBrickSlab", new SonarSlab.Half(Material.ROCK));

		reinforcedDirtBlock = SonarRegister.addBlock(modid, tab, "ReinforcedDirtBlock", new BlockBase(Material.GROUND, 1.0f, 20.0f));
		reinforcedDirtStairs = SonarRegister.addBlock(modid, tab, "ReinforcedDirtStairs", new SonarStairs(reinforcedDirtBlock));
		reinforcedDirtFence = SonarRegister.addBlock(modid, tab, "ReinforcedDirtFence", new SonarFence(Material.GROUND));
		reinforcedDirtGate = SonarRegister.addBlock(modid, tab, "ReinforcedDirtGate", new SonarGate(reinforcedDirtBlock));
		//reinforcedDirtSlab_double = new SonarSlab.Double(Material.GROUND).setUnlocalizedName("ReinforcedDirtSlab");
		//reinforcedDirtSlab_half = registerBlock("ReinforcedDirtSlab", new SonarSlab.Half(Material.GROUND));

		reinforcedDirtBrick = SonarRegister.addBlock(modid, tab, "ReinforcedDirtBrick", new BlockBase(Material.GROUND, 1.0f, 20.0f));
		reinforcedDirtBrickStairs = SonarRegister.addBlock(modid, tab, "ReinforcedDirtBrickStairs", new SonarStairs(reinforcedDirtBrick));
		reinforcedDirtBrickFence = SonarRegister.addBlock(modid, tab, "ReinforcedDirtBrickFence", new SonarFence(Material.GROUND));
		reinforcedDirtBrickGate = SonarRegister.addBlock(modid, tab, "ReinforcedDirtBrickGate", new SonarGate(reinforcedDirtBrick));
		//reinforcedDirtBrickSlab_double = new SonarSlab.Double(Material.GROUND).setUnlocalizedName("ReinforcedDirtBrickSlab");
		//reinforcedDirtBrickSlab_half = registerBlock("ReinforcedDirtBrickSlab", new SonarSlab.Half(Material.GROUND));
		
		int pos = 0;
		for (Variants variant : Variants.values()) {
            Block normal = SonarRegister.addBlock(modid, tab, "StableStone" + '_' + variant.name(), new StableStone(Material.ROCK, 100 + pos).setHardness(2.0F).setResistance(50.0f));
            Block rimmed = SonarRegister.addBlock(modid, tab, "StableStoneRimmed" + '_' + variant.name(), new StableStone(Material.ROCK, 200 + pos).setHardness(2.0F).setResistance(50.0f));
            Block black = SonarRegister.addBlock(modid, tab, "StableStoneBlackRimmed" + '_' + variant.name(), new StableStone(Material.ROCK, 300 + pos).setHardness(2.0F).setResistance(50.0f));
			//if (pos == 0) {
				stableStone[pos] = normal;
				stablestonerimmedBlock[pos] = rimmed;
				stablestonerimmedblackBlock[pos] = black;
			//}
			pos++;
		}
		stableGlass = SonarRegister.addBlock(modid, tab, "StableGlass", new ConnectedBlock.Glass(Material.GLASS, 1)).setLightLevel(0.625F).setHardness(0.6F).setResistance(50.0f);
		clearStableGlass = SonarRegister.addBlock(modid, tab, "ClearStableGlass", new ConnectedBlock.Glass(Material.GLASS, 2)).setLightLevel(0.625F).setHardness(0.6F).setResistance(50.0f);
	}
}
