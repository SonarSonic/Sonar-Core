package sonar.core.energy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.IRecipeHelper;
import sonar.core.helpers.ItemStackHelper;

public class DischargeValues implements IRecipeHelper {

	public static Map<ItemStack, Integer> dischargeList = new HashMap<ItemStack, Integer>();

	public static void addValues() {
		addValue(Items.redstone, 1000);
		addValue(Items.coal, 500);
		addValue(Blocks.coal_block, 4500);
		addValue(Blocks.redstone_block, 9000);
	}

	private static void clearList() {
		dischargeList.clear();
	}

	public static void addValue(Object object, int power) {
		if (object != null) {
			ItemStack stack = ItemStackHelper.createStack(object);
			if (stack != null)
				dischargeList.put(stack, power);
		}
	}

	public static int getValueOf(ItemStack stack) {		
		Iterator iterator = dischargeList.entrySet().iterator();

		Map.Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Map.Entry) iterator.next();
		} while (!ItemStackHelper.equalStacksRegular(stack, (ItemStack) entry.getKey()));

		return (Integer) entry.getValue();
	}
	
	@Override
	public String getRecipeID() {
		return "Discharge";
	}

	@Override
	public Map<ItemStack, Integer> getRecipes() {
		return dischargeList;
	}

}
