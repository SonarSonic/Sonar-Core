package sonar.core;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class SonarCrafting extends SonarCore {

	public static void registerCraftingRecipes() {		
		addShapeless(new ItemStack(SonarCore.stableGlass, 1), new Object[] { SonarCore.clearStableGlass });
		addShapeless(new ItemStack(SonarCore.clearStableGlass, 1), new Object[] { SonarCore.stableGlass });				
		addShaped(new ItemStack(SonarCore.reinforcedStoneStairs, 4), new Object[] { "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedStoneBlock });
		addShaped(new ItemStack(SonarCore.reinforcedStoneBrickStairs, 4), new Object[] { "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedStoneBrick });
		addShaped(new ItemStack(SonarCore.reinforcedDirtStairs, 4), new Object[] { "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedDirtBlock });
		addShaped(new ItemStack(SonarCore.reinforcedDirtBrickStairs, 4), new Object[] { "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedDirtBrick });

		addShapedOre(new ItemStack(SonarCore.reinforcedStoneFence, 6), new Object[] { "ASA", "ASA", "   ", 'A', SonarCore.reinforcedStoneBlock, 'S', "stickWood" });
		addShapedOre(new ItemStack(SonarCore.reinforcedStoneBrickFence, 6), new Object[] { "ASA", "ASA", "   ", 'A', SonarCore.reinforcedStoneBrick, 'S', "stickWood" });
		addShapedOre(new ItemStack(SonarCore.reinforcedDirtFence, 6), new Object[] { "ASA", "ASA", "   ", 'A', SonarCore.reinforcedDirtBlock, 'S', "stickWood" });
		addShapedOre(new ItemStack(SonarCore.reinforcedDirtBrickFence, 6), new Object[] { "ASA", "ASA", "   ", 'A', SonarCore.reinforcedDirtBrick, 'S', "stickWood" });

		for (int i = 0; i < 16; i++) {
			int meta = ~i & 15;
			if (meta != 0) {
				addShaped(new ItemStack(SonarCore.stableStone, 8, meta), new Object[] { "SSS", "SDS", "SSS", 'D', new ItemStack(Items.dye, 1, i), 'S', new ItemStack(SonarCore.stableStone, 1, 0) });
			}
		}

		//for (int i = 0; i < 16; i++) {
			//addShapeless(new ItemStack(stablestonerimmedBlock, 1), new Object[] { new ItemStack(stableStone, 1) });
			//addShapeless(new ItemStack(stablestonerimmedblackBlock, 1), new Object[] { new ItemStack(stablestonerimmedBlock, 1) });
			addShapeless(new ItemStack(SonarCore.stableStone, 1), new Object[] { new ItemStack(SonarCore.stablestonerimmedblackBlock, 1) });
		//}

	}

	public static void addShaped(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				GameRegistry.addRecipe(result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED RECIPE: " + result);
			}
		}
	}

	public static void addShapedOre(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				ShapedOreRecipe oreRecipe = new ShapedOreRecipe(result, input);
				GameRegistry.addRecipe(oreRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED ORE RECIPE: " + result);
			}
		}
	}

	public static void addShapeless(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				GameRegistry.addShapelessRecipe(result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPELESS RECIPE: " + result);
			}
		}
	}
}
