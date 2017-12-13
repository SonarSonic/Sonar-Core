package sonar.core.integration;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.core.Api;
import net.minecraft.util.EnumFacing;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;

public class AE2Helper {

	public static IAEItemStack convertStoredItemStack(StoredItemStack stack) {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack.item).setStackSize(stack.stored);
	}

	public static IAEFluidStack convertStoredFluidStack(StoredFluidStack stack) {
		return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class).createStack(stack.fluid).setStackSize(stack.stored);
	}

	public static IMEMonitor<IAEItemStack> getItemChannel(IStorageMonitorable storage) {
		return storage.getInventory(Api.INSTANCE.storage().getStorageChannel(IItemStorageChannel.class));		
	}

	public static IMEMonitor<IAEFluidStack> getFluidChannel(IStorageMonitorable storage) {
		return storage.getInventory(Api.INSTANCE.storage().getStorageChannel(IFluidStorageChannel.class));		
	}
	
	public static StoredItemStack convertAEItemStack(IAEStack stack) {
		if (stack != null && stack.isItem()) {
			IAEItemStack item = (IAEItemStack) stack;
			return new StoredItemStack(item.createItemStack(), item.getStackSize());
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
