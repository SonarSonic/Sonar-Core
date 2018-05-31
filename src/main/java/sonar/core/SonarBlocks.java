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

	public static void registerBlocks() {
		//white_dev_block = registerBlock("WhiteDevBlock", new BlockBase(Material.ROCK, 0.1f, 650.0f));
		//black_dev_block = registerBlock("BlackDevBlock", new BlockBase(Material.ROCK, 0.1f, 650.0f));

		reinforcedStoneBlock = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneBlock", new BlockBase(Material.ROCK, 2.0f, 50.0f));
		reinforcedStoneStairs = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneStairs", new SonarStairs(reinforcedStoneBlock));
		reinforcedStoneFence = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneFence", new SonarFence(Material.ROCK));
		reinforcedStoneGate = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneGate", new SonarGate(reinforcedStoneBlock));

		reinforcedStoneBrick = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneBrick", new BlockBase(Material.ROCK, 2.0f, 50.0f));
		reinforcedStoneBrickStairs = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneBrickStairs", new SonarStairs(reinforcedStoneBrick));
		reinforcedStoneBrickFence = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneBrickFence", new SonarFence(Material.ROCK));
		reinforcedStoneBrickGate = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedStoneBrickGate", new SonarGate(reinforcedStoneBrick));

		reinforcedDirtBlock = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtBlock", new BlockBase(Material.GROUND, 1.0f, 20.0f));
		reinforcedDirtStairs = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtStairs", new SonarStairs(reinforcedDirtBlock));
		reinforcedDirtFence = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtFence", new SonarFence(Material.GROUND));
		reinforcedDirtGate = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtGate", new SonarGate(reinforcedDirtBlock));

		reinforcedDirtBrick = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtBrick", new BlockBase(Material.GROUND, 1.0f, 20.0f));
		reinforcedDirtBrickStairs = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtBrickStairs", new SonarStairs(reinforcedDirtBrick));
		reinforcedDirtBrickFence = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtBrickFence", new SonarFence(Material.GROUND));
		reinforcedDirtBrickGate = SonarRegister.addBlock(SonarConstants.MODID, tab, "ReinforcedDirtBrickGate", new SonarGate(reinforcedDirtBrick));
		
		int pos = 0;
		for (Variants variant : Variants.values()) {
			stableStone[pos] = SonarRegister.addBlock(SonarConstants.MODID, tab, "StableStone" + '_' + variant.name(), new StableStone(Material.ROCK, 100 + pos).setHardness(2.0F).setResistance(50.0f));
			stablestonerimmedBlock[pos] = SonarRegister.addBlock(SonarConstants.MODID, tab, "StableStoneRimmed" + '_' + variant.name(), new StableStone(Material.ROCK, 200 + pos).setHardness(2.0F).setResistance(50.0f));
			stablestonerimmedblackBlock[pos] = SonarRegister.addBlock(SonarConstants.MODID, tab, "StableStoneBlackRimmed" + '_' + variant.name(), new StableStone(Material.ROCK, 300 + pos).setHardness(2.0F).setResistance(50.0f));
			pos++;
		}
		stableGlass = SonarRegister.addBlock(SonarConstants.MODID, tab, "StableGlass", new ConnectedBlock.Glass(Material.GLASS, 1)).setLightLevel(0.625F).setHardness(0.6F).setResistance(50.0f);
		clearStableGlass = SonarRegister.addBlock(SonarConstants.MODID, tab, "ClearStableGlass", new ConnectedBlock.Glass(Material.GLASS, 2)).setLightLevel(0.625F).setHardness(0.6F).setResistance(50.0f);
	}
}
