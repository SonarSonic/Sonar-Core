package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.utils.ActionType;

public interface ISonarEnergyItem {
	
	public long addEnergy(ItemStack stack, long maxReceive, ActionType action);
	
	public long removeEnergy(ItemStack stack, long maxExtract, ActionType action);

	public long getEnergyLevel(ItemStack stack);

	public long getFullCapacity(ItemStack stack);
	
}
