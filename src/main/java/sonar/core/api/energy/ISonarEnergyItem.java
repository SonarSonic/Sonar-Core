package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.utils.ActionType;

public interface ISonarEnergyItem {
	
    long addEnergy(ItemStack stack, long maxReceive, ActionType action);
	
    long removeEnergy(ItemStack stack, long maxExtract, ActionType action);

    long getEnergyLevel(ItemStack stack);
	
    long getFullCapacity(ItemStack stack);
}
