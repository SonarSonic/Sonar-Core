package sonar.core;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sonar.core.common.block.BlockBase;
import sonar.core.common.block.ConnectedBlock;
import sonar.core.common.block.SonarBlockTip;
import sonar.core.common.block.SonarFence;
import sonar.core.common.block.SonarGate;
import sonar.core.common.block.SonarMetaBlock;
import sonar.core.common.block.SonarSlab;
import sonar.core.common.block.SonarStairs;

public class SonarBlocks extends SonarCore {

	public static ArrayList<Block> registeredBlocks = new ArrayList();
	
	public static Block registerBlock(String name, Block block) {
		block.setCreativeTab(tab);
		GameRegistry.registerBlock(block.setUnlocalizedName(name), SonarBlockTip.class, name);
		registeredBlocks.add(block);
		return block;
	}	
	public static Block registerMetaBlock(String name, Block block) {
		block.setCreativeTab(tab);
		GameRegistry.registerBlock(block.setUnlocalizedName(name), SonarMetaBlock.class, name);
		registeredBlocks.add(block);
		return block;
	}	

	public static void registerBlocks() {
		// common blocks
		reinforcedStoneBlock = registerBlock("ReinforcedStoneBlock", new BlockBase(Material.rock, 2.0f, 10.0f));
		reinforcedStoneStairs = registerBlock("ReinforcedStoneStairs", new SonarStairs(reinforcedStoneBlock));
		reinforcedStoneFence = registerBlock("ReinforcedStoneFence", new SonarFence(Material.rock));
		reinforcedStoneGate = registerBlock("ReinforcedStoneGate", new SonarGate(reinforcedStoneBlock));
		reinforcedStoneSlab_double = new SonarSlab.Double(reinforcedStoneBlock).setUnlocalizedName("ReinforcedStoneSlab");
		reinforcedStoneSlab_half = registerBlock("ReinforcedStoneSlab", new SonarSlab.Half(reinforcedStoneBlock));

		reinforcedStoneBrick = registerBlock("ReinforcedStoneBrick", new BlockBase(Material.rock, 2.0f, 10.0f));
		reinforcedStoneBrickStairs = registerBlock("ReinforcedStoneBrickStairs", new SonarStairs(reinforcedStoneBrick));
		reinforcedStoneBrickFence = registerBlock("ReinforcedStoneBrickFence", new SonarFence(Material.rock));
		reinforcedStoneBrickGate = registerBlock("ReinforcedStoneBrickGate", new SonarGate(reinforcedStoneBrick));
		reinforcedStoneBrickSlab_double = new SonarSlab.Double(reinforcedStoneBrick).setUnlocalizedName("ReinforcedStoneBrickSlab");
		reinforcedStoneBrickSlab_half = registerBlock("ReinforcedStoneBrickSlab", new SonarSlab.Half(reinforcedStoneBrick));
		
		reinforcedDirtBlock = registerBlock("ReinforcedDirtBlock", new BlockBase(Material.ground, 1.0f, 4.0f));
		reinforcedDirtStairs = registerBlock("ReinforcedDirtStairs", new SonarStairs(reinforcedDirtBlock));
		reinforcedDirtFence = registerBlock("ReinforcedDirtFence", new SonarFence(Material.ground));
		reinforcedDirtGate = registerBlock("ReinforcedDirtGate", new SonarGate(reinforcedDirtBlock));
		reinforcedDirtSlab_double = new SonarSlab.Double(reinforcedDirtBlock).setUnlocalizedName("ReinforcedDirtSlab");
		reinforcedDirtSlab_half = registerBlock("ReinforcedDirtSlab", new SonarSlab.Half(reinforcedDirtBlock));

		reinforcedDirtBrick = registerBlock("ReinforcedDirtBrick", new BlockBase(Material.ground, 1.0f, 4.0f));
		reinforcedDirtBrickStairs = registerBlock("ReinforcedDirtBrickStairs", new SonarStairs(reinforcedDirtBrick));
		reinforcedDirtBrickFence = registerBlock("ReinforcedDirtBrickFence", new SonarFence(Material.ground));
		reinforcedDirtBrickGate = registerBlock("ReinforcedDirtBrickGate", new SonarGate(reinforcedDirtBrick));
		reinforcedDirtBrickSlab_double = new SonarSlab.Double(reinforcedDirtBrick).setUnlocalizedName("ReinforcedDirtBrickSlab");
		reinforcedDirtBrickSlab_half = registerBlock("ReinforcedDirtBrickSlab", new SonarSlab.Half(reinforcedDirtBrick));

		toughenedStoneBlock = registerBlock("ToughenedStoneBlock", new BlockBase(Material.rock, 2.0f, 10.0f));
		toughenedStoneBrick = registerBlock("ToughenedStoneBrick", new BlockBase(Material.rock, 2.0f, 10.0f));
		
		toughenedDirtBlock = registerBlock("ToughenedDirtBlock", new BlockBase(Material.ground, 1.0f, 4.0f));
		toughenedDirtBrick = registerBlock("ToughenedDirtBrick", new BlockBase(Material.ground, 1.0f, 4.0f));
		
		stableStone = registerBlock("StableStone", new ConnectedBlock(Material.rock, 0));
		stableGlass = registerBlock("StableGlass", new ConnectedBlock.Glass(Material.glass, 1)).setLightLevel(0.625F).setHardness(0.6F);
		clearStableGlass = registerBlock("ClearStableGlass", new ConnectedBlock.Glass(Material.glass, 2)).setLightLevel(0.625F).setHardness(0.6F);
	}

}