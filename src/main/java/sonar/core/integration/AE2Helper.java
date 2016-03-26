package sonar.core.integration;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.ActionType;
import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

public class AE2Helper {

	public static IAEItemStack convertStoredItemStack(StoredItemStack stack) {
		return AEApi.instance().storage().createItemStack(stack.item).setStackSize(stack.stored);
	}

	public static IAEFluidStack convertStoredFluidStack(StoredFluidStack stack) {
		return AEApi.instance().storage().createFluidStack(stack.fluid).setStackSize(stack.stored);
	}

	public static StoredItemStack convertAEItemStack(IAEStack stack) {
		if (stack.isItem()) {
			IAEItemStack item = (IAEItemStack) stack;
			return new StoredItemStack(item.getItemStack(), item.getStackSize());
		}
		return null;
	}

	public static StoredFluidStack convertAEFluidStack(IAEStack stack) {
		if (stack.isFluid()) {
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
