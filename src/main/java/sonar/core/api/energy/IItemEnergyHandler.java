package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

/**the handler Flux Networks uses to transfer energy*/
public interface IItemEnergyHandler {
	
	EnergyType getEnergyType();

	/**if this handler can add energy to the stack*/
    boolean canAddEnergy(ItemStack stack);

	/**if this handler can remove energy from the stack*/
    boolean canRemoveEnergy(ItemStack stack);

	/**if this handler can read the energy stored*/
	boolean canReadEnergy(ItemStack stack);
	
	/**returns how much energy was added to the item depending on the TransferType called, this will always be called after canAddEnergy*/
    long addEnergy(long add, ItemStack stack, ActionType actionType);

	/**returns how much energy was removed from the item depending on the TransferType called, this will always be called after canRemoveEnergy*/
    long removeEnergy(long remove, ItemStack stack, ActionType actionType);

	/**returns how much energy is stored in the stack*/
	long getStored(ItemStack stack);

	/**returns the max energy stored in the stack*/
	long getCapacity(ItemStack stack);
	
}
