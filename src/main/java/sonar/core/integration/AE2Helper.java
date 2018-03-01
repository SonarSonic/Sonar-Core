package sonar.core.integration;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.core.Api;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;

public class AE2Helper {

	public static IAEItemStack convertStoredItemStack(StoredItemStack stack) {
		return AEApi.instance().storage().createItemStack(stack.item).setStackSize(stack.stored);
	}

	public static IAEFluidStack convertStoredFluidStack(StoredFluidStack stack) {
		return AEApi.instance().storage().createFluidStack(stack.fluid).setStackSize(stack.stored);
	}

	public static IMEMonitor<IAEItemStack> getItemChannel(IStorageMonitorable storage) {
		return storage.getItemInventory();		
	}

	public static IMEMonitor<IAEFluidStack> getFluidChannel(IStorageMonitorable storage) {
		return storage.getFluidInventory();		
	}
	
	public static StoredItemStack convertAEItemStack(IAEStack stack) {
		if (stack != null && stack.isItem()) {
			IAEItemStack item = (IAEItemStack) stack;
			return new StoredItemStack(item.getItemStack(), item.getStackSize());
		}
		return null;
	}

	public static StoredFluidStack convertAEFluidStack(IAEStack stack) {
		if (stack != null && stack.isFluid()) {
			IAEFluidStack fluid = (IAEFluidStack) stack;
			return new StoredFluidStack(fluid.getFluidStack(), fluid.getStackSize());
		}
		return null;
	}

	public static Actionable getActionable(ActionType action) {
		switch (action) {
		case PERFORM:
			return Actionable.MODULATE;
		default:
			return Actionable.SIMULATE;
		}
	}

	public static ActionType getActionType(Actionable action) {
		switch (action) {
		case MODULATE:
			return ActionType.PERFORM;
		default:
			return ActionType.SIMULATE;
		}
	}
}
