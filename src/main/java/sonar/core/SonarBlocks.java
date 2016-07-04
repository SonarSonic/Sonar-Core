package sonar.core;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import sonar.core.common.block.BlockBase;
import sonar.core.common.block.ConnectedBlock;
import sonar.core.common.block.SonarBlockTip;
import sonar.core.common.block.SonarFence;
import sonar.core.common.block.SonarGate;
import sonar.core.common.block.SonarMetaBlock;
import sonar.core.common.block.SonarSlab;
import sonar.core.common.block.SonarStairs;
import sonar.core.common.block.StableStone;
import sonar.core.common.block.StableStone.Variants;

public class SonarBlocks extends SonarCore {

	public static ArrayList<Block> registeredBlocks = new ArrayList();

	public static Block registerBlock(String name, Block block) {
		block.setCreativeTab(tab);
		block.setUnlocalizedName(name);
		GameRegistry.register(block.setRegistryName(name));
		GameRegistry.register(new SonarBlockTip(block).setRegistryName(name));
		registeredBlocks.add(block);
		return block;
	}

	public static Block registerMetaBlock(String name, Block block) {
		block.setCreativeTab(tab);
		block.setUnlocalizedName(name);
		GameRegistry.register(block.setRegistryName(name));
		GameRegistry.register(new SonarMetaBlock(block).setRegistryName(name));
		registeredBlocks.add(block);
		return block;
	}

	public static void registerSlab(String name, Item slab, BlockSlab singleSlab, BlockSlab doubleSlab) {
		//reinforcedStoneBrickSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneBrickSlab");
		//reinforcedStoneBrickSlab_half = registerBlock("ReinforcedStoneBrickSlab", new SonarSlab.Half(Material.ROCK));
	}

	public static void registerBlocks() {
		// common blocks
		reinforcedStoneBlock = registerBlock("ReinforcedStoneBlock", new BlockBase(Material.ROCK, 2.0f, 10.0f));
		reinforcedStoneStairs = registerBlock("ReinforcedStoneStairs", new SonarStairs(reinforcedStoneBlock));
		reinforcedStoneFence = registerBlock("ReinforcedStoneFence", new SonarFence(Material.ROCK));
		reinforcedStoneGate = registerBlock("ReinforcedStoneGate", new SonarGate(reinforcedStoneBlock));
		//reinforcedStoneSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneSlab");
		//reinforcedStoneSlab_half = registerBlock("ReinforcedStoneSlab", new SonarSlab.Half(Material.ROCK));

		reinforcedStoneBrick = registerBlock("ReinforcedStoneBrick", new BlockBase(Material.ROCK, 2.0f, 10.0f));
		reinforcedStoneBrickStairs = registerBlock("ReinforcedStoneBrickStairs", new SonarStairs(reinforcedStoneBrick));
		reinforcedStoneBrickFence = registerBlock("ReinforcedStoneBrickFence", new SonarFence(Material.ROCK));
		reinforcedStoneBrickGate = registerBlock("ReinforcedStoneBrickGate", new SonarGate(reinforcedStoneBrick));
		//reinforcedStoneBrickSlab_double = new SonarSlab.Double(Material.ROCK).setUnlocalizedName("ReinforcedStoneBrickSlab");
		//reinforcedStoneBrickSlab_half = registerBlock("ReinforcedStoneBrickSlab", new SonarSlab.Half(Material.ROCK));

		reinforcedDirtBlock = registerBlock("ReinforcedDirtBlock", new BlockBase(Material.GROUND, 1.0f, 4.0f));
		reinforcedDirtStairs = registerBlock("ReinforcedDirtStairs", new SonarStairs(reinforcedDirtBlock));
		reinforcedDirtFence = registerBlock("ReinforcedDirtFence", new SonarFence(Material.GROUND));
		reinforcedDirtGate = registerBlock("ReinforcedDirtGate", new SonarGate(reinforcedDirtBlock));
		//reinforcedDirtSlab_double = new SonarSlab.Double(Material.GROUND).setUnlocalizedName("ReinforcedDirtSlab");
		//reinforcedDirtSlab_half = registerBlock("ReinforcedDirtSlab", new SonarSlab.Half(Material.GROUND));

		reinforcedDirtBrick = registerBlock("ReinforcedDirtBrick", new BlockBase(Material.GROUND, 1.0f, 4.0f));
		reinforcedDirtBrickStairs = registerBlock("ReinforcedDirtBrickStairs", new SonarStairs(reinforcedDirtBrick));
		reinforcedDirtBrickFence = registerBlock("ReinforcedDirtBrickFence", new SonarFence(Material.GROUND));
		reinforcedDirtBrickGate = registerBlock("ReinforcedDirtBrickGate", new SonarGate(reinforcedDirtBrick));
		//reinforcedDirtBrickSlab_double = new SonarSlab.Double(Material.GROUND).setUnlocalizedName("ReinforcedDirtBrickSlab");
		//reinforcedDirtBrickSlab_half = registerBlock("ReinforcedDirtBrickSlab", new SonarSlab.Half(Material.GROUND));
		
		/*
		toughenedStoneBlock = registerBlock("ToughenedStoneBlock", new BlockBase(Material.rock, 2.0f, 10.0f));
		toughenedStoneBrick = registerBlock("ToughenedStoneBrick", new BlockBase(Material.rock, 2.0f, 10.0f));

		toughenedDirtBlock = registerBlock("ToughenedDirtBlock", new BlockBase(Material.ground, 1.0f, 4.0f));
		toughenedDirtBrick = registerBlock("ToughenedDirtBrick", new BlockBase(Material.ground, 1.0f, 4.0f));
		 */
		int pos = 0;
		for (Variants variant : Variants.values()) {
			Block normal = registerBlock("StableStone" + "_" + variant.name(), new StableStone(Material.ROCK, 100 + pos).setHardness(2.0F));
			Block rimmed = registerBlock("StableStoneRimmed" + "_" + variant.name(), new StableStone(Material.ROCK, 200 + pos).setHardness(2.0F));
			Block black = registerBlock("StableStoneBlackRimmed" + "_" + variant.name(), new StableStone(Material.ROCK, 300 + pos).setHardness(2.0F));
			//if (pos == 0) {
				stableStone[pos] = normal;
				stablestonerimmedBlock[pos] = rimmed;
				stablestonerimmedblackBlock[pos] = black;
			//}
			pos++;
		}
		stableGlass = registerBlock("StableGlass", new ConnectedBlock.Glass(Material.GLASS, 1)).setLightLevel(0.625F).setHardness(0.6F);
		clearStableGlass = registerBlock("ClearStableGlass", new ConnectedBlock.Glass(Material.GLASS, 2)).setLightLevel(0.625F).setHardness(0.6F);
	}
}
