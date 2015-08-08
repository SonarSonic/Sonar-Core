package sonar.core.utils.helpers;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;
import sonar.calculator.mod.CalculatorConfig;
import sonar.core.utils.SonarAPI;
import cpw.mods.fml.common.FMLLog;

public abstract class ValueHelper {

	protected Map<Object, Integer> recipeList = new HashMap();

	/** add all your recipes here */
	public abstract void addRecipes();

	public ValueHelper() {
		this.addRecipes();
	}

	/** get the full list of recipes */
	public Map getRecipes() {
		return this.recipeList;
	}

	/** makes sure each item/block is an itemstack */
	public void addRecipe(Object object, Integer value) {
		Object stack = null;
		if (object == null) {
			return;
		}
		if (object instanceof String) {
			ArrayList<ItemStack> ores = OreDictionary.getOres((String) object);
			if (ores.size() > 0) {
				ItemStack[] oreStacks = new ItemStack[ores.size()];
				stack = ores.toArray(oreStacks);
				
			} else {
				return;
			}

		} else if (object instanceof ItemStack[]) {
			for (int s = 0; s < ((ItemStack[]) object).length; s++) {
				if (((ItemStack[]) object)[s] == null) {
					return;
				}
			}
			stack = object;
		} else {
			stack = fixedStack(object);
		}

		recipeList.put(stack, value);
	}

	/** turns blocks/items into ItemStacks */
	private ItemStack fixedStack(Object obj) {
		if (obj instanceof ItemStack) {
			return ((ItemStack) obj).copy();
		} else if (obj instanceof Item) {
			return new ItemStack((Item) obj, 1);
		} else {
			if (!(obj instanceof Block)) {
				throw new RuntimeException("Invalid Recipe!");
			}
			return new ItemStack((Block) obj, 1);
		}
	}

	/**
	 * @param input full list of inputs
	 * @return full list of output stacks
	 */
	public Integer getOutput(ItemStack input) {
		if (input == null) {
			return null;
		}
		if(SonarAPI.calculatorLoaded() &&!CalculatorConfig.isEnabled(input)){
			return null;
		}

		Iterator iterator = this.recipeList.entrySet().iterator();

		Map.Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}

			entry = (Map.Entry) iterator.next();
		} while (!checkInput(input, entry.getKey()));
		
		return (Integer)entry.getValue();
	}

	/**
	 * @param input input stacks to check
	 * @param key input stacks to check
	 * @return if they are same
	 */
	private boolean checkInput(ItemStack input, Object key) {
		if (key instanceof ItemStack) {
			if (!equalStack(input, ((ItemStack) key), true)) {
				return false;
			}
		} else if (key instanceof ItemStack[]) {
			if (containsStack(input, (ItemStack[]) key, true) == -1) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 
	 * @param stack ItemStack to search for
	 * @param key search field
	 * @param checkSize does the stacksize matter
	 * @return if a match was found
	 */
	public int containsStack(ItemStack stack, Object[] key, boolean checkSize) {
		for (int i = 0; i < key.length; i++) {
			if (key[i] != null) {
				if (key[i] instanceof ItemStack) {
					if (equalStack(stack, ((ItemStack) key[i]), checkSize)) {
						return i;
					}
				} else if (key[i] instanceof ItemStack[]) {
					for (int s = 0; s < ((ItemStack[]) key[i]).length; s++) {
						ItemStack target = ((ItemStack[]) key[i])[s];
						if (target != null) {
							if (equalStack(stack, target, checkSize)) {
								return i;
							}
						}
					}
				}
			}
		}
		return -1;
	}

	/**
	 * @param stack ItemStack to check
	 * @param key ItemStack to compare against
	 * @param checkSize does the stack size matter
	 * @return if the stacks are equal or not
	 */
	private boolean equalStack(ItemStack stack, ItemStack key, boolean checkSize) {
		return stack.getItem() == key.getItem() && (key.getItemDamage() == 32767 || (stack.getItemDamage() == key.getItemDamage())) && (!checkSize || key.stackSize <= stack.stackSize);
	}

}
