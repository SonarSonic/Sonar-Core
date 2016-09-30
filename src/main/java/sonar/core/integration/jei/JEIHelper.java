package sonar.core.integration.jei;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.RecipeObjectType;

//FIXME this is JEI STUFF hahah
public class JEIHelper {

	public static class RecipeMapper {

		public Map<RecipeObjectType, Map<Integer, RecipeMapping>> map = new HashMap();
		public IRecipeWrapper wrapper;

		public RecipeMapper(IRecipeWrapper wrapper) {
			this.wrapper = wrapper;
		}

		/** @param type the type of Object
		 * @param recipePos the objects position in the ItemStack Collection
		 * @param slotPos the objects slot position in the container
		 * @param xPos the xPos of the slot
		 * @param yPos the yPos of the slot */
		public void map(RecipeObjectType type, int recipePos, int slotPos, int xPos, int yPos) {
			this.map(type, recipePos, new RecipeMapping(slotPos, xPos, yPos));
		}

		public void map(RecipeObjectType type, int recipePos, RecipeMapping mapping) {
			if (map.get(type) == null) {
				map.put(type, new HashMap());
			}
			map.get(type).put(recipePos, mapping);
		}

		/** maps all the slots to the {@link IGuiItemStackGroup} */
		public void mapTo(IGuiItemStackGroup stacks) {
			for (Entry<RecipeObjectType, Map<Integer, RecipeMapping>> entry : map.entrySet()) {
				List objects = entry.getKey() == RecipeObjectType.INPUT ? wrapper.getInputs() : wrapper.getOutputs();
				for (Entry<Integer, RecipeMapping> mapping : entry.getValue().entrySet()) {
					RecipeMapping recipe = mapping.getValue();
					stacks.init(recipe.slotPos, entry.getKey() == RecipeObjectType.INPUT, recipe.xPos, recipe.yPos);
					Object obj = objects.get(mapping.getKey());
					if (obj instanceof Collection) {
						stacks.set(recipe.slotPos, (Collection) obj);
					} else {
						stacks.set(recipe.slotPos, (ItemStack) obj);
					}
				}
			}
		}

	}

	public static class RecipeMapping {
		public int slotPos, xPos, yPos;

		public RecipeMapping(int slotPos, int xPos, int yPos) {
			this.slotPos = slotPos;
			this.xPos = xPos;
			this.yPos = yPos;
		}
	}

}
