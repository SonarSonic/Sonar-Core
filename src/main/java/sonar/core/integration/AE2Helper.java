package sonar.core.integration;

import sonar.core.api.ActionType;
import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;

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
