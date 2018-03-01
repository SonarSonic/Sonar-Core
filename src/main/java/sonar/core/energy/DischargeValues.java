package sonar.core.energy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.IRecipeHelper;
import sonar.core.helpers.ItemStackHelper;
import sonar.core.utils.SonarCompat;

public class DischargeValues implements IRecipeHelper {

    public static Map<ItemStack, Integer> dischargeList = new HashMap<>();

	public static void addValues() {
		addValue(Items.REDSTONE, 1000);
		addValue(Items.COAL, 500);
		addValue(Blocks.COAL_BLOCK, 4500);
		addValue(Blocks.REDSTONE_BLOCK, 9000);
	}

	private static void clearList() {
		dischargeList.clear();
	}

	public static void addValue(Object object, int power) {
		if (object != null) {
			ItemStack stack = ItemStackHelper.createStack(object);
			if (!SonarCompat.isEmpty(stack))
				dischargeList.put(stack, power);
		}
	}

	public static int getValueOf(ItemStack stack) {
        Iterator<Map.Entry<ItemStack, Integer>> iterator = dischargeList.entrySet().iterator();

        Map.Entry<ItemStack, Integer> entry;
        if (!iterator.hasNext()) {
            return 0;
        }
        entry = iterator.next();
        while (!ItemStackHelper.equalStacksRegular(stack, entry.getKey())) {
			if (!iterator.hasNext()) {
				return 0;
			}
            entry = iterator.next();
        }

        return entry.getValue();
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
