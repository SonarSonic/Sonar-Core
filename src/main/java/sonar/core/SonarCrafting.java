package sonar.core;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import sonar.core.common.block.StableStone.Variants;
import sonar.core.integration.SonarLoader;

public class SonarCrafting extends SonarCore {
	public static void registerCraftingRecipes() {
        ResourceLocation group = new ResourceLocation("SonarCore");

        addShapeless(modid, group, new ItemStack(SonarCore.reinforcedStoneBrick, 2), fromBlock(SonarCore.reinforcedStoneBlock), fromBlock(SonarCore.reinforcedStoneBlock));
        addShapeless(modid, group, new ItemStack(SonarCore.stableStone[0], 2), fromBlock(SonarCore.reinforcedStoneBrick), fromBlock(SonarCore.reinforcedStoneBrick));

        addShapeless(modid, group, new ItemStack(SonarCore.stableGlass, 1), fromBlock(SonarCore.clearStableGlass));
        addShapeless(modid, group, new ItemStack(SonarCore.clearStableGlass, 1), fromBlock(SonarCore.stableGlass));
        addShaped(modid, group, new ItemStack(SonarCore.reinforcedStoneStairs, 4), "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedStoneBlock);
        addShaped(modid, group, new ItemStack(SonarCore.reinforcedStoneBrickStairs, 4), "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedStoneBrick);
        addShaped(modid, group, new ItemStack(SonarCore.reinforcedDirtStairs, 4), "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedDirtBlock);
        addShaped(modid, group, new ItemStack(SonarCore.reinforcedDirtBrickStairs, 4), "A  ", "AA ", "AAA", 'A', SonarCore.reinforcedDirtBrick);

        addShapedOre(modid, new ItemStack(SonarCore.reinforcedStoneFence, 6), "ASA", "ASA", 'A', SonarCore.reinforcedStoneBlock, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedStoneBrickFence, 6), "ASA", "ASA", 'A', SonarCore.reinforcedStoneBrick, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedDirtFence, 6), "ASA", "ASA", 'A', SonarCore.reinforcedDirtBlock, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedDirtBrickFence, 6), "ASA", "ASA", 'A', SonarCore.reinforcedDirtBrick, 'S', "stickWood");

        addShapedOre(modid, new ItemStack(SonarCore.reinforcedStoneGate, 1), "SAS", "SAS", 'A', SonarCore.reinforcedStoneBlock, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedStoneBrickGate, 1), "SAS", "SAS", 'A', SonarCore.reinforcedStoneBrick, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedDirtGate, 1), "SAS", "SAS", 'A', SonarCore.reinforcedDirtBlock, 'S', "stickWood");
        addShapedOre(modid, new ItemStack(SonarCore.reinforcedDirtBrickGate, 1), "SAS", "SAS", 'A', SonarCore.reinforcedDirtBrick, 'S', "stickWood");
        //TODO add fence gates

		for (int i = 0; i < 16; i++) {
            addShaped(modid, group, new ItemStack(SonarCore.stableStone[i], 8), "SSS", "SDS", "SSS", 'D', new ItemStack(Items.DYE, 1, Variants.values()[i].getDyeMeta()), 'S', new ItemStack(SonarCore.stableStone[0], 1));
            addShapeless(modid, group, new ItemStack(stablestonerimmedBlock[i], 1), fromBlock(stableStone[i]));
            addShapeless(modid, group, new ItemStack(stablestonerimmedblackBlock[i], 1), fromBlock(stablestonerimmedBlock[i]));
            addShapeless(modid, group, new ItemStack(SonarCore.stableStone[i], 1), fromBlock(SonarCore.stablestonerimmedblackBlock[i]));
		}

		if (!SonarLoader.calculatorLoaded()) {
            addShapelessOre(modid, new ItemStack(SonarCore.reinforcedStoneBlock, 1), "cobblestone", "plankWood");
            addShapelessOre(modid, new ItemStack(SonarCore.reinforcedStoneBlock, 4), "cobblestone", "logWood");
            addShapelessOre(modid, new ItemStack(SonarCore.reinforcedDirtBlock, 1), Blocks.DIRT, "plankWood");
            addShapelessOre(modid, new ItemStack(SonarCore.reinforcedDirtBlock, 4), Blocks.DIRT, "logWood");
            addShapelessOre(modid, new ItemStack(SonarCore.stableGlass, 2), "blockGlass", "blockGlass");
            addShapeless(modid, group, new ItemStack(SonarCore.reinforcedDirtBrick, 2), fromBlock(SonarCore.reinforcedDirtBlock), fromBlock(SonarCore.reinforcedDirtBlock));
        }
		}

    public static Ingredient fromBlock(Block b) {
        return Ingredient.fromItem(Item.getItemFromBlock(b));
	}

    private static int recipeNumber;//TODO make a smarter recipe number?

    public static void addShaped(String modid, ResourceLocation group, ItemStack result, Object... input) {
		if (!result.isEmpty() && input != null) {
			try {
                GameRegistry.addShapedRecipe(new ResourceLocation(modid, result.getUnlocalizedName() + recipeNumber++), group, result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED RECIPE: " + result);
                exception.printStackTrace();
			}
		}
	}

    public static void addShapedOre(String modid, ItemStack result, Object... input) {
		if (!result.isEmpty() && input != null) {
			try {
                ResourceLocation r = new ResourceLocation(modid, result.getUnlocalizedName() + recipeNumber++);
                ShapedOreRecipe oreRecipe = new ShapedOreRecipe(r, result, input);
                oreRecipe.setRegistryName(r);
                ForgeRegistries.RECIPES.register(oreRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED ORE RECIPE: " + result);
                exception.printStackTrace();
			}
		}
	}

    public static void addShapeless(String modid, ResourceLocation group, ItemStack result, Ingredient... input) {
		if (!result.isEmpty() && input != null) {
			try {
                GameRegistry.addShapelessRecipe(new ResourceLocation(modid, result.getUnlocalizedName() + recipeNumber++), group, result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPELESS RECIPE: " + result);
                exception.printStackTrace();
			}
		}
	}

    public static void addShapelessOre(String modid, ItemStack result, Object... input) {
		if (!result.isEmpty() && input != null) {
			try {
                ResourceLocation r = new ResourceLocation(modid, result.getUnlocalizedName() + recipeNumber++);
                ShapelessOreRecipe oreRecipe = new ShapelessOreRecipe(r, result, input);
                oreRecipe.setRegistryName(r);
                ForgeRegistries.RECIPES.register(oreRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPELESS ORE RECIPE: " + result);
                exception.printStackTrace();
			}
		}
	}
}
